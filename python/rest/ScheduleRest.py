# coding=utf-8
from flask_restful import Resource, request


class ScheduleRest(Resource):
    url = '/schedule'

    def __init__(self, **kwargs):
        super(ScheduleRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self, system, name):
        system = self.cutie.get_system(system)
        schedule = system.get_schedule(name)
        return {'name': schedule.name, 'daySets': map(
            lambda ds: {'name': ds.name, 'days': ds.days, 'periods': map(
                lambda p: {'mode': p.mode, 'start': p.start},
                ds.periods
            )},
            schedule.day_sets
        )}

    def put(self, system):
        system = self.cutie.get_system(system)
        content = request.get_json(silent=True)
        system.update_schedule(content)  # TODO: move updating from JSON to separate class

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url + '/<system>',
                         cls.url + '/<system>/<name>',
                         resource_class_kwargs={'cutie': cutie})
