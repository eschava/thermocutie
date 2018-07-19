from xml.etree.ElementTree import parse, ElementTree, Element, SubElement

from .CommentedTreeBuilder import CommentedTreeBuilder
from .device.Sensor import Sensor
from .device.HVAC import HVAC

try:
    from itertools import izip_longest as zip_longest
except ImportError:
    # noinspection PyUnresolvedReferences
    from itertools import zip_longest


class DeviceSet(object):
    def __init__(self, file, mqtt, system_state):
        self._file = file
        self._mqtt = mqtt
        self._system_state = system_state
        self._devices = []

    def load(self):
        xml = parse(self._file)
        root = xml.getroot()

        for device_xml in root:
            type = device_xml.tag
            device = self.create_device(type)
            device.load(device_xml)
            self._devices.append(device)

    def save(self):
        try:
            xml = CommentedTreeBuilder.parse(self._file)
        except IOError:
            xml = ElementTree(Element('Devices'))

        root = xml.getroot()

        for device_xml, device in zip_longest(root, self._devices):
            if device is None:
                root.remove(device_xml)
            else:
                if device_xml is None:
                    device_xml = SubElement(root, device.type)
                device.save(device_xml)

        xml.write(self._file, encoding='UTF-8')

    def list(self):
        return self._devices

    def list_by_type(self, type):
        return [d for d in self._devices if d.type == type]

    def update(self, name, changes):
        device = next((s for s in self._devices if s.name == name), None)
        if device is None:
            raise Exception('Unknown device : ' + name)
        device.update(changes)
        self.save()

    # def add(self, mode_changes):
    #     mode = TemperatureMode()
    #     mode.name = mode_changes['name']
    #     mode.icon = mode_changes['icon']
    #     mode.color = mode_changes['color']
    #     mode.temperature = mode_changes['temperature']
    #     self._modes.append(mode)
    #     self.save()
    #
    # def delete(self, name):
    #     self._modes[:] = [m for m in self._modes if m.name != name]
    #     self.save()

    def create_device(self, type):
        if type == Sensor.TYPE:
            return Sensor(self._mqtt, self._system_state)
        elif type == HVAC.TYPE:
            return HVAC()
        else:
            raise Exception('Unknown device type: ' + type)

