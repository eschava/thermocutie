# coding=utf-8
from flask_restful import Resource


class SystemRest(Resource):
    url = "/system"

    def __init__(self, **kwargs):
        super(SystemRest, self).__init__()
        self.data = kwargs['data']

    def get(self):
        return self.data

    def put(self, name, title):
        self.data.append({'name': name, 'title': title})

    def post(self, name, title):
        for x in self.data:
            if x['name'] == name:
                x['title'] = title

    def delete(self, name):
        self.data = [x for x in self.data if x['name'] != name]

    @classmethod
    def register(cls, api, data):
        api.add_resource(cls,
                         cls.url,
                         cls.url + '/<name>',
                         cls.url + '/<name>/<title>',
                         resource_class_kwargs={'data': data})
