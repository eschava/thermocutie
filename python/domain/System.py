import os
from .SystemSettings import SystemSettings


class System(object):

    def __init__(self, name, title):
        self._name = name
        self._settings = SystemSettings(title)

    def load(self, folder):
        self._settings.load(os.path.join(folder, "settings.xml"))

    def save(self, folder):
        self._settings.save(os.path.join(folder, "settings.xml"))

    @property
    def name(self):
        return self._name

    @property
    def title(self):
        return self._settings.title

    @title.setter
    def title(self, value):
        self._settings.title = value


