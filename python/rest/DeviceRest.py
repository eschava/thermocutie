# coding=utf-8
from flask_restful import Resource, request


class DeviceRest(Resource):
    url = '/device'

    def __init__(self, **kwargs):
        super(DeviceRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self, system):
        system = self.cutie.get_system(system)
        return map(
            lambda m: {'name': m.name, 'type': m.type},
            system.devices.list()
        )

    # def post(self, system):
    #     content = request.get_json(silent=True)
    #     modes = self.cutie.get_system(system).temperature_modes
    #     modes.add(content)
    #
    # def put(self, system, old_name=None, new_name=None):
    #     modes = self.cutie.get_system(system).temperature_modes
    #     if old_name is None:
    #         content = request.get_json(silent=True)
    #         modes.update(content)
    #     else:
    #         modes.rename(old_name, new_name)

    # def put(self, system, old_name, new_name):
    #     content = request.get_json(silent=True)
    #     modes = self.cutie.get_system(system).temperature_modes
    #     modes.update(content)

    # def delete(self, system, name):
    #     modes = self.cutie.get_system(system).temperature_modes
    #     modes.delete(name)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url + '/<system>',
                         cls.url + '/<system>/<name>',
                         resource_class_kwargs={'cutie': cutie})
