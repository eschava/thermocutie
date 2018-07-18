from .Device import Device


class Sensor(Device):
    TYPE = 'Sensor'

    def __init__(self, mqtt, system_state):
        Device.__init__(self, self.TYPE)
        self._mqtt = mqtt
        self._system_state = system_state
        self._mqttBroker = None
        self._mqttTopic = None

    @property
    def mqtt_broker(self):
        return self._mqttBroker

    @property
    def mqtt_topic(self):
        return self._mqttTopic

    def load(self, xml):
        super(Sensor, self).load(xml)

        self._mqttBroker = xml.attrib.get('mqttBroker', None)
        self._mqttTopic = xml.attrib.get('mqttTopic', None)

        self.subscribe()

    def save(self, xml):
        super(Sensor, self).save(xml)

        if self._mqttBroker:
            xml.attrib['mqttBroker'] = self._mqttBroker
            xml.attrib['mqttTopic'] = self._mqttTopic
        else:
            del xml.attrib['mqttBroker']
            del xml.attrib['mqttTopic']

    def json(self):
        json = super(Sensor, self).json()
        json.update({'mqtt_broker': self._mqttBroker, 'mqtt_topic': self._mqttTopic})
        return json

    def update(self, changes):
        self.unsubscribe()

        self._name = changes['name']
        if 'mqtt_broker' in changes:
            self._mqttBroker = str(changes['mqtt_broker'])
            self._mqttTopic = str(changes['mqtt_topic'])
        else:
            self._mqttBroker = None
            self._mqttTopic = None

        self.subscribe()

    def subscribe(self):
        if self._mqttBroker is not None and self._mqttTopic is not None:
            self._mqtt.subscribe(self._mqttBroker, self._mqttTopic, self)

    def unsubscribe(self):
        if self._mqttBroker is not None and self._mqttTopic is not None:
            self._mqtt.unsubscribe(self._mqttBroker, self._mqttTopic, self)

    # noinspection PyUnusedLocal
    def mqtt_message(self, topic, payload):
        value = float(str(payload))
        self._system_state.device(self._name, value)

