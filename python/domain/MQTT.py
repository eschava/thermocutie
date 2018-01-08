# TODO


class MQTT(object):
    def __init__(self, folder):
        self.folder = folder

    def load(self):
        pass

    def get_brokers(self):
        return [
            {'name': 'test', 'clientId': 'clientId', 'uri': 'tcp://1232312'}
        ]

    @property
    def server_enabled(self):
        return True

    @property
    def server_port(self):
        return 12345

    def server_update(self, enabled, port):
        pass