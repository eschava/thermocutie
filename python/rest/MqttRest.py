# coding=utf-8
from flask_restful import Resource


class MqttRest(Resource):
    url = '/mqttbrokers'

    def __init__(self, **kwargs):
        super(MqttRest, self).__init__()
        self.mqtt = kwargs['mqtt']

    def get(self):
        return list(map(
            lambda b: {'name': b.name, 'clientId': b.client_id, 'uri': b.uri},
            self.mqtt.get_clients()
        ))

    @classmethod
    def register(cls, api, mqtt):
        api.add_resource(cls,
                         cls.url,
                         resource_class_kwargs={'mqtt': mqtt})
