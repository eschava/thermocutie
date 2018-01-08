from xml.etree.ElementTree import parse, ElementTree, Element


class SystemSettings(object):
    def __init__(self, title):
        self._title = title

    def load(self, f):
        xml = parse(f)
        root = xml.getroot()
        self._title = root.attrib['title']

    def save(self, f):
        try:
            xml = parse(f)
        except IOError:
            xml = ElementTree(Element('Settings'))

        root = xml.getroot()
        root.attrib['title'] = self._title
        xml.write(f)

    @property
    def title(self):
        return self._title

    @title.setter
    def title(self, value):
        self._title = value
