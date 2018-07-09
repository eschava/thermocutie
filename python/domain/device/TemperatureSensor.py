from .Device import Device


class TemperatureSensor(Device):
    TYPE = 'Temperature'

    def __init__(self):
        Device.__init__(self, self.TYPE)
        self._mqttBroker = None
        self._mqttTopic = None

    @property
    def mqtt_broker(self):
        return self._mqttBroker

    @property
    def mqtt_topic(self):
        return self._mqttTopic

    def load(self, xml):
        super(TemperatureSensor, self).load(xml)

        self._mqttBroker = xml.attrib.get('mqttBroker', None)
        self._mqttTopic = xml.attrib.get('mqttTopic', None)

    def save(self, xml):
        super(TemperatureSensor, self).save(xml)

        if self._mqttBroker:
            xml.attrib['mqttBroker'] = self._mqttBroker
            xml.attrib['mqttTopic'] = self._mqttTopic
        else:
            del xml.attrib['mqttBroker']
            del xml.attrib['mqttTopic']

    def json(self):
        json = super(TemperatureSensor, self).json()
        json.update({'mqtt_broker': self._mqttBroker, 'mqtt_topic': self._mqttTopic})
        return json

    def update(self, changes):
        self._name = changes['name']
        if 'mqtt_broker' in changes:
            self._mqttBroker = changes['mqtt_broker']
            self._mqttTopic = changes['mqtt_topic']
        else:
            self._mqttBroker = None
            self._mqttTopic = None

