# coding=utf-8
import threading
import time

from flask import Flask, Blueprint, send_from_directory
from flask_reggie import Reggie
from flask_restful import Api
from flask_sockets import Sockets

from domain.Cutie import Cutie
from domain.MQTT import MQTT
from rest.MqttInternalRest import MqttInternalRest
from rest.MqttRest import MqttRest
from rest.DeviceRest import DeviceRest
from rest.ScheduleRest import ScheduleRest
from rest.SystemRest import SystemRest
from rest.TemperatureModeRest import TemperatureModeRest

app = Flask(__name__)
Reggie(app)
sockets = Sockets(app)

web_folder = "../web"
conf_folder = "../conf"

cutie = Cutie(conf_folder)
cutie.load()
mqtt = MQTT(conf_folder)
mqtt.load()

# register REST services under that /rest
rest = Blueprint('rest', __name__)
rest_api = Api(rest)
app.register_blueprint(rest, url_prefix='/rest')

SystemRest.register(rest_api, cutie)
MqttRest.register(rest_api, mqtt)
MqttInternalRest.register(rest_api, mqtt)
DeviceRest.register(rest_api, cutie)
TemperatureModeRest.register(rest_api, cutie)
ScheduleRest.register(rest_api, cutie)


@sockets.route('/state')
def state_websocket(ws):
    while not ws.closed:
        message = ws.receive()
        def worker():
            for x in range(20, 30):
                ws.send('{"currentTemperature":0.0,"targetMode":{"name":"Home","temperature":' + str(x) + ',"color":"#cf2500","icon":"sun-rising"}}')
                time.sleep(2)

        t = threading.Thread(target=worker)
        t.start()


# noinspection PyUnusedLocal
@app.route('/<regex("[a-z]*"):page>')
@app.route('/<regex("[a-z]+/[a-z]*"):page>')
def controller(page):
    return send_from_directory(web_folder, 'index.htm')


@app.route('/<regex("(bower_components|css|icons|js|device)/.*"):f>')
@app.route('/<regex(".*\.htm"):f>')
def static_file(f):
    return send_from_directory(web_folder, f)


if __name__ == "__main__":
    from gevent import pywsgi
    from geventwebsocket.handler import WebSocketHandler
    server = pywsgi.WSGIServer(('', 5000), app, handler_class=WebSocketHandler)
    server.serve_forever()
