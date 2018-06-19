class Device(object):
    def __init__(self, type):
        self._type = type
        self._name = None

    @property
    def type(self):
        return self._type

    @property
    def name(self):
        return self._name

    def load(self, xml):
        self._name = xml.attrib['name']

    def save(self, xml):
        xml.attrib['name'] = self._name
