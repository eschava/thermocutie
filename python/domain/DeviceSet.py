import itertools
from xml.etree.ElementTree import parse, ElementTree, Element, SubElement

from .CommentedTreeBuilder import CommentedTreeBuilder
from .device.TemperatureSensor import TemperatureSensor
from .device.HVAC import HVAC


class DeviceSet(object):
    def __init__(self, file):
        self._file = file
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

        for device_xml, device in itertools.izip_longest(root, self._devices):
            if device is None:
                root.remove(device_xml)
            else:
                if device_xml is None:
                    device_xml = SubElement(root, device.type)
                device.save(device_xml)

        xml.write(self._file, encoding='UTF-8')

    def list(self):
        return self._devices

    # def add(self, mode_changes):
    #     mode = TemperatureMode()
    #     mode.name = mode_changes['name']
    #     mode.icon = mode_changes['icon']
    #     mode.color = mode_changes['color']
    #     mode.temperature = mode_changes['temperature']
    #     self._modes.append(mode)
    #     self.save()
    #
    # def update(self, mode_changes):
    #     name = mode_changes['name']
    #     mode = next((m for m in self._modes if m.name == name), None)
    #     mode.icon = mode_changes['icon']
    #     mode.color = mode_changes['color']
    #     mode.temperature = mode_changes['temperature']
    #     self.save()
    #
    # def rename(self, old_name, new_name):
    #     mode = next((m for m in self._modes if m.name == old_name), None)
    #     mode.name = new_name
    #     self.save()
    #
    # def delete(self, name):
    #     self._modes[:] = [m for m in self._modes if m.name != name]
    #     self.save()

    @staticmethod
    def create_device(type):
        if type == TemperatureSensor.TYPE:
            return TemperatureSensor()
        elif type == HVAC.TYPE:
            return HVAC()
        else:
            raise Exception('Unknown device type: ' + type)

