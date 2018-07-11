import json
import logging


class SystemStateWebSocket(object):
    def __init__(self, cutie, websocket, name):
        self.cutie = cutie
        self.name = name
        self.websocket = websocket

    def send(self, data):
        try:
            self.websocket.send(json.dumps(data))
        except Exception, e:
            logging.error("Websocket error '%s', disconnected" % str(e))
            self.cutie.unsubscribe(self.name, self.send)

