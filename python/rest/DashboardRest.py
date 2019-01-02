# coding=utf-8
import json
from flask_restful import Resource, request


class DashboardRest(Resource):
    url = '/dashboard'

    def __init__(self, **kwargs):
        super(DashboardRest, self).__init__()
        self.cutie = kwargs['cutie']

    def get(self):
        return json.loads(self.cutie.dashboard)
        # return system.dashboard

    def put(self):
        self.cutie.dashboard = json.dumps(request.get_json(silent=True), indent=4, sort_keys=True)

    @classmethod
    def register(cls, api, cutie):
        api.add_resource(cls,
                         cls.url,
                         resource_class_kwargs={'cutie': cutie})
