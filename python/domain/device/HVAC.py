from .Device import Device


class HVAC(Device):
    TYPE = 'HVAC'

    def __init__(self):
        Device.__init__(self, self.TYPE)
