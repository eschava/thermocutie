# import os
import logging
from threading import Thread

try:
    import asyncio
    from hbmqtt.broker import Broker
    MQTT_SERVER_SUPPORTED = True
except ImportError:
    MQTT_SERVER_SUPPORTED = False

default_config = {
    'listeners': {
        'default': {
            'type': 'tcp',
            'bind': '0.0.0.0:1883',
        },
    },
    'sys_interval': 10,
    'auth': {
        'allow-anonymous': True,
        # 'password-file': os.path.join(os.path.dirname(os.path.realpath(__file__)), "passwd"),
        'plugins': [
            'auth_file', 'auth_anonymous'
        ]
    },
    'topic-check': {
        'enabled': False
    }
}


class MQTTServer(object):
    def __init__(self):
        self._port = 0
        self._broker = None
        self._loop = asyncio.get_event_loop() if MQTT_SERVER_SUPPORTED else None

    @property
    def supported(self):
        return MQTT_SERVER_SUPPORTED

    @property
    def port(self):
        return self._port

    @port.setter
    def port(self, value):
        self._port = value

    def start(self):
        config = default_config
        config['listeners']['default']['bind'] = '0.0.0.0:' + str(self._port)
        self._broker = Broker(default_config)
        tt = Thread(target=self.run)
        tt.daemon = True
        tt.start()

    def run(self):
        try:
            self._loop.run_until_complete(self._broker.start())
            self._loop.run_forever()
            self._loop.run_until_complete(self._broker.shutdown())
            # self._loop.close()
        except:
            logging.exception("MQTT server exception")

    def stop(self):
        self._loop.call_soon_threadsafe(self._loop.stop)


