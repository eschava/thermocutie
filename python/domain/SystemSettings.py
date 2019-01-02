from xml.etree.ElementTree import parse, ElementTree, Element
from .CommentedTreeBuilder import CommentedTreeBuilder


class SystemSettings(object):
    def __init__(self):
        pass

    def load(self, f):
        # xml = parse(f)
        # root = xml.getroot()
        pass

    def save(self, f):
        try:
            xml = CommentedTreeBuilder.parse(f)
        except IOError:
            xml = ElementTree(Element('Settings'))

        # root = xml.getroot()
        xml.write(f, encoding='UTF-8')
