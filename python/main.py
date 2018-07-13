# coding=utf-8
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
from rest.DashboardRest import DashboardRest
from rest.TemperatureModeRest import TemperatureModeRest
from rest.SystemStateWebSocket import SystemStateWebSocket

app = Flask(__name__)
Reggie(app)
sockets = Sockets(app)

web_folder = "../web"
conf_folder = "../conf"

mqtt = MQTT(conf_folder)
mqtt.load()

cutie = Cutie(conf_folder, mqtt)
cutie.load()

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
DashboardRest.register(rest_api, cutie)


@sockets.route('/state')
def state_websocket(ws):
    while not ws.closed:
        system_name = ws.receive()
        listener = SystemStateWebSocket(cutie, ws, system_name)
        cutie.subscribe(system_name, listener.send)


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
