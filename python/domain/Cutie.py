import os
from .SystemState import SystemState
from .SystemSettings import SystemSettings
from .DeviceSet import DeviceSet
from .TemperatureModeSet import TemperatureModeSet
from .ScheduleSet import ScheduleSet


class Cutie(object):
    def __init__(self, folder, mqtt):
        self._folder = folder
        self._mqtt = mqtt
        self._state = SystemState()
        self._settings = SystemSettings()
        self._devices = DeviceSet(os.path.join(folder, 'devices.xml'), mqtt, self._state)
        self._temperature_modes = TemperatureModeSet(os.path.join(folder, 'temperaturemodes.xml'))
        self._schedule_set = ScheduleSet(os.path.join(folder, 'schedules.xml'))

    def load(self):
        self._settings.load(os.path.join(self._folder, 'settings.xml'))
        self._devices.load()
        self._temperature_modes.load()
        self._schedule_set.load()

    # def save(self):
    #     self._settings.save(os.path.join(self._folder, 'settings.xml'))
    #     self._devices.save()
    #     self._temperature_modes.save()
    #     self._schedule_set.save()

    @property
    def devices(self):
        return self._devices

    @property
    def temperature_modes(self):
        return self._temperature_modes

    @property
    def dashboard(self):
        with open(os.path.join(self._folder, 'dashboard.json'), 'r') as f:
            return f.read()

    @dashboard.setter
    def dashboard(self, value):
        with open(os.path.join(self._folder, 'dashboard.json'), 'w') as f:
            f.write(value)

    def get_schedule(self, name):
        return self._schedule_set.get_schedule(name)

    def update_schedule(self, schedule_changes):
        self._schedule_set.update_schedule(schedule_changes)

    def subscribe(self, listener):
        self._state.subscribe(listener)

    def unsubscribe(self, listener):
        self._state.unsubscribe(listener)




