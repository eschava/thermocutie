import itertools
from xml.etree.ElementTree import parse, ElementTree, Element, SubElement

from .CommentedTreeBuilder import CommentedTreeBuilder
from .TemperatureMode import TemperatureMode


class TemperatureModes(object):
    def __init__(self, file):
        self._file = file
        self._modes = []

    def load(self):
        xml = parse(self._file)
        root = xml.getroot()

        for mode_xml in root.iter('Mode'):
            mode = TemperatureMode()
            mode.load(mode_xml)
            self._modes.append(mode)

    def save(self):
        try:
            xml = CommentedTreeBuilder.parse(self._file)
        except IOError:
            xml = ElementTree(Element('TemperatureModes'))

        root = xml.getroot()

        for mode_xml, mode in itertools.izip_longest(root.iter('Mode'), self._modes):
            if mode is None:
                root.remove(mode_xml)
            else:
                if mode_xml is None:
                    mode_xml = SubElement(root, 'Mode')
                mode.save(mode_xml)

        xml.write(self._file, encoding='UTF-8')

    def list(self):
        return self._modes

    def add(self, mode_changes):
        mode = TemperatureMode()
        mode.name = mode_changes['name']
        mode.icon = mode_changes['icon']
        mode.color = mode_changes['color']
        mode.temperature = mode_changes['temperature']
        self._modes.append(mode)
        self.save()

    def update(self, mode_changes):
        name = mode_changes['name']
        mode = next((m for m in self._modes if m.name == name), None)
        mode.icon = mode_changes['icon']
        mode.color = mode_changes['color']
        mode.temperature = mode_changes['temperature']
        self.save()

    def rename(self, old_name, new_name):
        mode = next((m for m in self._modes if m.name == old_name), None)
        mode.name = new_name
        self.save()

    def delete(self, name):
        self._modes[:] = [m for m in self._modes if m.name != name]
        self.save()
