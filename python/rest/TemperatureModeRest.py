# coding=utf-8
from flask_restful import Resource, request


class TemperatureModeRest(Resource):
    url = '/temperaturemode'

    def __init__(self, **kwargs):
        super(TemperatureModeRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self):
        return list(map(
            lambda m: {'name': m.name, 'color': m.color, 'icon': m.icon, 'temperature': m.temperature},
            self.cutie.temperature_modes.list()
        ))

    def post(self):
        content = request.get_json(silent=True)
        modes = self.cutie.temperature_modes
        modes.add(content)

    def put(self, old_name=None, new_name=None):
        modes = self.cutie.temperature_modes
        if old_name is None:
            content = request.get_json(silent=True)
            modes.update(content)
        else:
            modes.rename(old_name, new_name)

    # def put(self, old_name, new_name):
    #     content = request.get_json(silent=True)
    #     modes = self.cutie.temperature_modes
    #     modes.update(content)

    def delete(self, name):
        modes = self.cutie.temperature_modes
        modes.delete(name)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url,
                         cls.url + '/<name>',
                         cls.url + '/rename/<old_name>/<new_name>',
                         resource_class_kwargs={'cutie': cutie})
