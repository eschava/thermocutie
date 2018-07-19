# coding=utf-8
from flask_restful import Resource, request


class DeviceRest(Resource):
    url = '/device'

    def __init__(self, **kwargs):
        super(DeviceRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self, system, name=None):
        system = self.cutie.get_system(system)
        if name is None:
            return list(map(
                lambda device: device.json(),
                system.devices.list()
            ))
        else:  # device by type
            return list(map(
                lambda device: device.name,
                system.devices.list_by_type(name)
            ))

    def put(self, system, name):
        content = request.get_json(silent=True)
        system = self.cutie.get_system(system)
        devices = system.devices
        devices.update(name, content)

    # def post(self, system):
    #     content = request.get_json(silent=True)
    #     modes = self.cutie.get_system(system).temperature_modes
    #     modes.add(content)
    #
    # def delete(self, system, name):
    #     modes = self.cutie.get_system(system).temperature_modes
    #     modes.delete(name)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url + '/<system>',
                         cls.url + '/<system>/<name>',
                         resource_class_kwargs={'cutie': cutie})
