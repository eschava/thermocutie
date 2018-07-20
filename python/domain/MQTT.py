import os
import logging
from xml.etree.ElementTree import parse, ElementTree, Element, SubElement

try:
    from itertools import izip_longest as zip_longest
except ImportError:
    # noinspection PyUnresolvedReferences
    from itertools import zip_longest

from .MQTTClient import MQTTClient
from .MQTTServer import MQTTServer
from .CommentedTreeBuilder import CommentedTreeBuilder


class MQTT(object):
    def __init__(self, folder):
        self._config_file = os.path.join(folder, "mqtt.xml")
        self._server_enabled = False
        self._server = MQTTServer()
        self._clients = []

    def load(self):
        xml = parse(self._config_file)
        root = xml.getroot()

        server = root.find('Server')
        if server is not None:
            self._server_enabled = server.attrib['enabled'].lower() == 'true'
            self._server.port = int(server.attrib['port'])

            if self._server_enabled:
                if self._server.supported:
                    self._server.start()
                else:
                    logging.error('MQTT server is not supported')

        for client in root.findall('Clients/Client'):
            self._clients.append(MQTTClient(client))

    def save(self):
        try:
            xml = CommentedTreeBuilder.parse(self._config_file)
        except IOError:
            xml = ElementTree(Element('MQTT'))
        root = xml.getroot()

        server = root.find('Server')
        if server is None:
            server = SubElement(root, 'Server')
        server.attrib['enabled'] = 'true' if self._server_enabled else 'false'
        server.attrib['port'] = str(self.server_port)

        clients = root.find('Clients')
        if clients is None:
            clients = SubElement(root, 'Clients')

        for client_xml, client in zip_longest(clients.iter('Client'), self._clients):
            if client is None:
                clients.remove(client_xml)
            else:
                if client_xml is None:
                    client_xml = SubElement(clients, 'Client')
                client.save(client_xml)

        xml.write(self._config_file, encoding='UTF-8')

    def get_clients(self):
        return self._clients

    @property
    def server_supported(self):
        return self._server.supported

    @property
    def server_enabled(self):
        return self._server_enabled

    @property
    def server_port(self):
        return self._server.port

    def server_update(self, enabled, port):
        if self._server_enabled != enabled:
            if enabled:
                self._server.start()
            else:
                self._server.stop()

        self._server_enabled = enabled
        self._server.port = port
        self.save()

    def subscribe(self, name, topic, listener):
        client = next((c for c in self._clients if c.name == name), None)
        client.subscribe(topic, listener)

    def unsubscribe(self, name, topic, listener):
        client = next((c for c in self._clients if c.name == name), None)
        client.unsubscribe(topic, listener)
