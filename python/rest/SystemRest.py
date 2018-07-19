# coding=utf-8
from flask_restful import Resource


class SystemRest(Resource):
    url = '/system'

    def __init__(self, **kwargs):
        super(SystemRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self):
        return list(map(
            lambda s: {'name': s.name, 'title': s.title},
            self.cutie.get_systems()
        ))

    def put(self, name, title):
        self.cutie.add_system(name, title)

    def post(self, name, title):
        self.cutie.change_system(name, title)

    def delete(self, name):
        self.cutie.delete_system(name)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url,
                         cls.url + '/<name>',
                         cls.url + '/<name>/<title>',
                         resource_class_kwargs={'cutie': cutie})
