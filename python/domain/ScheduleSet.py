import itertools
from xml.etree.ElementTree import parse, ElementTree, Element, SubElement

from .CommentedTreeBuilder import CommentedTreeBuilder
from .WeekSchedule import WeekSchedule


class ScheduleSet(object):
    def __init__(self, file):
        self._file = file
        self._schedules = []

    def load(self):
        xml = parse(self._file)
        root = xml.getroot()

        for schedule_xml in root.iter('Schedule'):
            type = schedule_xml.attrib['type']
            if type == 'week':
                schedule = WeekSchedule()
                schedule.load(schedule_xml)
                self._schedules.append(schedule)

    def save(self):
        try:
            xml = CommentedTreeBuilder.parse(self._file)
        except IOError:
            xml = ElementTree(Element('Schedules'))

        root = xml.getroot()

        for schedule_xml, schedule in itertools.izip_longest(root.iter('Schedule'), self._schedules):
            if schedule is None:
                root.remove(schedule_xml)
            else:
                if schedule_xml is None:
                    schedule_xml = SubElement(root, 'Schedule')
                schedule.save(schedule_xml)

        xml.write(self._file, encoding='UTF-8')

    def get_schedule(self, name):
        return next((s for s in self._schedules if s.name == name), None)

    def update_schedule(self, schedule_changes):
        name = schedule_changes['name']
        self.get_schedule(name).update(schedule_changes)
        self.save()
