# coding=utf-8
from flask_restful import Resource, request
from domain.MQTTClient import MQTTClient


class MqttRest(Resource):
    url = '/mqttbrokers'

    def __init__(self, **kwargs):
        super(MqttRest, self).__init__()
        self.mqtt = kwargs['mqtt']

    def get(self):
        return list(map(
            lambda c: {'name': c.name, 'clientId': c.client_id, 'uri': c.uri, 'connected': c.connected},
            self.mqtt.get_clients()
        ))

    def post(self):
        content = request.get_json(silent=True)
        client = MQTTClient()
        client.name = content['name']
        client.uri = content['uri']
        client.client_id = content['clientId']
        self.mqtt.add_client(client)

    def put(self, action='update', name=None, new_name=None):
        if action == 'update':
            content = request.get_json(silent=True)
            self.mqtt.update_client(content)
        elif action == 'rename':
            self.mqtt.rename_client(name, new_name)
        elif action == 'connect':
            # TODO: sync
            self.mqtt.connect_client(name)
        elif action == 'disconnect':
            self.mqtt.disconnect_client(name)
        else:
            raise Exception('Unknown action: ' + action)

    def delete(self, name):
        self.mqtt.delete_client(name)

    @classmethod
    def register(cls, api, mqtt):
        api.add_resource(cls,
                         cls.url,
                         cls.url + '/<name>',
                         cls.url + '/<name>/<action>',
                         cls.url + '/<name>/<action>/<new_name>',
                         resource_class_kwargs={'mqtt': mqtt})
