# coding=utf-8
from flask import Flask, Blueprint, send_from_directory
from flask_restful import Api
from flask_reggie import Reggie
from flask_sockets import Sockets
from rest.SystemRest import SystemRest

import threading, time

app = Flask(__name__)
Reggie(app)
sockets = Sockets(app)

web_folder = "../web"

data = [
    {'name': 'default', 'title': 'Default'},
    {'name': 'hvac', 'title': u'Отопление'}
]

# register REST services under that /rest
rest = Blueprint('rest', __name__)
rest_api = Api(rest)
app.register_blueprint(rest, url_prefix='/rest')

SystemRest.register(rest_api, data)


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


@app.route('/<regex("(bower_components|css|icons|js)/.*"):f>')
@app.route('/<regex(".*\.htm"):f>')
def static_file(f):
    return send_from_directory(web_folder, f)


if __name__ == "__main__":
    from gevent import pywsgi
    from geventwebsocket.handler import WebSocketHandler
    server = pywsgi.WSGIServer(('', 5000), app, handler_class=WebSocketHandler)
    server.serve_forever()
