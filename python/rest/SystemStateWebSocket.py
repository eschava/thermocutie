import json
import logging


class SystemStateWebSocket(object):
    def __init__(self, cutie, websocket):
        self.cutie = cutie
        self.websocket = websocket

    def send(self, data):
        try:
            self.websocket.send(json.dumps(data))
        except Exception as e:
            logging.warn("Websocket error '%s', unsubscribing device state listener" % str(e))
            self.cutie.unsubscribe(self.send)

