import os
from .SystemSettings import SystemSettings
from .TemperatureModes import TemperatureModes


class System(object):

    def __init__(self, folder, name, title):
        self._name = name
        self._settings = SystemSettings(title)
        # TODO: path could change
        self._temperature_modes = TemperatureModes(os.path.join(folder, "temperaturemodes.xml"))

    def load(self, folder):
        self._settings.load(os.path.join(folder, "settings.xml"))
        self._temperature_modes.load()

    def save(self, folder):
        self._settings.save(os.path.join(folder, "settings.xml"))
        self._temperature_modes.save()

    @property
    def name(self):
        return self._name

    @property
    def title(self):
        return self._settings.title

    @title.setter
    def title(self, value):
        self._settings.title = value

    @property
    def temperature_modes(self):
        return self._temperature_modes


