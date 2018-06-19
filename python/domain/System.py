import os
from .SystemSettings import SystemSettings
from .DeviceSet import DeviceSet
from .TemperatureModeSet import TemperatureModeSet
from .ScheduleSet import ScheduleSet


class System(object):

    def __init__(self, folder, name, title):
        self._name = name
        self._settings = SystemSettings(title)
        # TODO: path could change
        self._devices = DeviceSet(os.path.join(folder, "devices.xml"))
        self._temperature_modes = TemperatureModeSet(os.path.join(folder, "temperaturemodes.xml"))
        self._schedule_set = ScheduleSet(os.path.join(folder, "schedules.xml"))

    def load(self, folder):
        self._settings.load(os.path.join(folder, "settings.xml"))
        self._devices.load()
        self._temperature_modes.load()
        self._schedule_set.load()

    def save(self, folder):
        self._settings.save(os.path.join(folder, "settings.xml"))
        self._devices.save()
        self._temperature_modes.save()
        self._schedule_set.save()

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
    def devices(self):
        return self._devices

    @property
    def temperature_modes(self):
        return self._temperature_modes

    def get_schedule(self, name):
        return self._schedule_set.get_schedule(name)

    def update_schedule(self, schedule_changes):
        self._schedule_set.update_schedule(schedule_changes)
