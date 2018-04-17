class TemperatureMode(object):
    def __init__(self):
        self._name = None
        self._icon = None
        self._color = None
        self._temperature = -1

    def load(self, xml):
        self._name = xml.attrib['name']
        self._icon = xml.attrib['icon']
        self._color = xml.attrib['color']
        self._temperature = float(xml.attrib['temperature'])

    def save(self, xml):
        xml.attrib['name'] = self._name
        xml.attrib['icon'] = self._icon
        xml.attrib['color'] = self._color
        xml.attrib['temperature'] = str(self._temperature)

    @property
    def name(self):
        return self._name

    @name.setter
    def name(self, value):
        self._name = value

    @property
    def icon(self):
        return self._icon

    @icon.setter
    def icon(self, value):
        self._icon = value

    @property
    def color(self):
        return self._color

    @color.setter
    def color(self, value):
        self._color = value

    @property
    def temperature(self):
        return self._temperature

    @temperature.setter
    def temperature(self, value):
        self._temperature = value

