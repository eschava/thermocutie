# coding=utf-8
from flask_restful import Resource, request


class MqttInternalRest(Resource):
    url = '/mqttbrokers/internal'

    def __init__(self, **kwargs):
        super(MqttInternalRest, self).__init__()
        self.mqtt = kwargs['mqtt']

    def get(self):
        return {'supported': self.mqtt.server_supported,
                'enabled': self.mqtt.server_enabled,
                'port': self.mqtt.server_port}

    def post(self):
        content = request.get_json(silent=True)
        self.mqtt.server_update(content['enabled'], content['port'])

    @classmethod
    def register(cls, api, mqtt):
        api.add_resource(cls,
                         cls.url,
                         resource_class_kwargs={'mqtt': mqtt})
