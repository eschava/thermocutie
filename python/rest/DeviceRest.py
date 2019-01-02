# coding=utf-8
from flask_restful import Resource, request


class DeviceRest(Resource):
    url = '/device'

    def __init__(self, **kwargs):
        super(DeviceRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self, name=None):
        devices = self.cutie.devices
        if name is None:
            return list(map(
                lambda device: device.json(),
                devices.list()
            ))
        else:  # device by type
            return list(map(
                lambda device: device.name,
                devices.list_by_type(name)
            ))

    def put(self, name):
        content = request.get_json(silent=True)
        devices = self.cutie.devices
        devices.update(name, content)

    # def post(self):
    #     content = request.get_json(silent=True)
    #     modes = self.cutie.temperature_modes
    #     modes.add(content)
    #
    # def delete(self, name):
    #     modes = self.cutie.temperature_modes
    #     modes.delete(name)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url,
                         cls.url + '/<name>',
                         resource_class_kwargs={'cutie': cutie})
