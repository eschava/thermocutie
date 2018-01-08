# coding=utf-8
from flask_restful import Resource


class MqttRest(Resource):
    url = '/mqttbrokers'

    def __init__(self, **kwargs):
        super(MqttRest, self).__init__()
        self.mqtt = kwargs['mqtt']

    def get(self):
        return map(
            lambda b: {'name': b['name'], 'clientId': b['clientId'], 'uri': b['uri']},
            # lambda b: {'name': b.name, 'clientId': b.clientId, 'uri': b.uri},
            self.mqtt.get_brokers()
        )

    @classmethod
    def register(cls, api, mqtt):
        api.add_resource(cls,
                         cls.url,
                         resource_class_kwargs={'mqtt': mqtt})
