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
        except Exception as e:
            logging.warn("Websocket error '%s', unsubscribing device state listener" % str(e))
            self.cutie.unsubscribe(self.name, self.send)

