from .Device import Device


class TemperatureSensor(Device):
    TYPE = 'Temperature'

    def __init__(self):
        Device.__init__(self, self.TYPE)
        self._name = None
