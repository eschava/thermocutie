import logging
import threading
import wrapt
import paho.mqtt.client as mqtt
try:
    from urlparse import urlparse
except ImportError:
    # noinspection PyUnresolvedReferences
    from urllib.parse import urlparse


class MQTTClient(object):
    def __init__(self, xml):
        self._name = xml.attrib['name']
        self._client_id = xml.attrib['clientId']
        self._uri = xml.attrib['uri']
        self._subscribers = {}
        self._last_message = {}

        self._connected = False
        self._client = None
        self.connect()

    def save(self, xml):
        xml.attrib['name'] = self._name
        xml.attrib['clientId'] = self._client_id
        xml.attrib['uri'] = self._uri

    @property
    def name(self):
        return self._name

    @property
    def client_id(self):
        return self._client_id

    @property
    def uri(self):
        return self._uri

    def connect(self):
        self._connected = False
        self._client = mqtt.Client(client_id=self._client_id)
        self._client.on_connect = self.on_connect
        self._client.on_disconnect = self.on_disconnect
        self._client.on_message = self.on_message

        t = threading.Thread(name=self._name + " MQTT loop", target=self.start_loop)
        t.start()

    def start_loop(self):
        url = urlparse(self._uri)
        self._client.connect(url.hostname, url.port)
        self._client.loop_forever()

    # noinspection PyUnusedLocal
    def on_connect(self, client, userdata, flags, rc):
        self._connected = True

    # noinspection PyUnusedLocal
    def on_disconnect(self, client, userdata, rc):
        self._connected = False

    # noinspection PyUnusedLocal
    @wrapt.synchronized
    def on_message(self, client, userdata, message):
        subscribers = self._subscribers.get(message.topic, None)
        if subscribers is not None:
            payload = message.payload.decode("utf-8")
            self._last_message[message.topic] = payload
            for s in subscribers:
                try:
                    s.mqtt_message(message.topic, payload)
                except:
                    logging.exception(message.topic)

    @wrapt.synchronized
    def subscribe(self, topic, listener):
        subscribers = self._subscribers.get(topic, None)
        if subscribers is None:
            subscribers = []
            self._subscribers[topic] = subscribers
            self._client.subscribe(topic)
        else:
            last_message = self._last_message.get(topic, None)
            if last_message is not None:
                listener.mqtt_message(topic, last_message)

        subscribers.append(listener)

    @wrapt.synchronized
    def unsubscribe(self, topic, listener):
        subscribers = self._subscribers.get(topic, None)
        if subscribers is not None and listener in subscribers:
            subscribers.remove(listener)
            if len(subscribers) == 0:
                self._client.unsubscribe(topic)
                del self._subscribers[topic]
                if topic in self._last_message:
                    del self._last_message[topic]
