import itertools
from xml.etree.ElementTree import SubElement


class WeekSchedule(object):
    def __init__(self):
        self._name = None
        self._day_sets = []

    def load(self, xml):
        self._name = xml.attrib['name']

        for dayset_xml in xml.iter('DaySet'):
            dayset = DaySet()
            dayset.load(dayset_xml)
            self._day_sets.append(dayset)

    def save(self, xml):
        xml.attrib['type'] = 'week'
        xml.attrib['name'] = self._name

        for dayset_xml, dayset in itertools.izip_longest(xml.iter('DaySet'), self._day_sets):
            if dayset is None:
                xml.remove(dayset_xml)
            else:
                if dayset_xml is None:
                    dayset_xml = SubElement(xml, 'DaySet')
                dayset.save(dayset_xml)

    def update(self, changes):
        for dayset_change, dayset in itertools.izip_longest(changes['daySets'], self._day_sets):
            if dayset_change is None:
                self._day_sets.remove(dayset)
            else:
                if dayset is None:
                    dayset = DaySet()
                    self._day_sets.append(dayset)
                dayset.update(dayset_change)

    @property
    def name(self):
        return self._name

    @name.setter
    def name(self, value):
        self._name = value

    @property
    def day_sets(self):
        return self._day_sets


class DaySet(object):
    def __init__(self):
        self._name = None
        self._days = None
        self._periods = []

    def load(self, xml):
        self._name = xml.attrib['name']
        self._days = xml.attrib['days']

        for period_xml in xml.iter('Period'):
            period = Period()
            period.load(period_xml)
            self._periods.append(period)

    def save(self, xml):
        xml.attrib['name'] = self._name
        xml.attrib['days'] = self._days

        for period_xml, period in itertools.izip_longest(xml.iter('Period'), self._periods):
            if period is None:
                xml.remove(period_xml)
            else:
                if period_xml is None:
                    period_xml = SubElement(xml, 'Period')
                period.save(period_xml)

    def update(self, change):
        self._name = change['name']
        self._days = change['days']

        for period_change, period in itertools.izip_longest(change['periods'], self._periods):
            if period_change is None:
                self._periods.remove(period)
            else:
                if period is None:
                    period = Period()
                    self._periods.append(period)
                period.update(period_change)

    @property
    def name(self):
        return self._name

    @property
    def days(self):
        return self._days

    @property
    def periods(self):
        return self._periods


class Period(object):
    def __init__(self):
        self._mode = None
        self._start = None

    def load(self, xml):
        self._mode = xml.attrib['mode']  # TODO: should be instance of Mode class
        self._start = xml.attrib['start']

    def save(self, xml):
        xml.attrib['mode'] = self._mode
        xml.attrib['start'] = self._start

    def update(self, change):
        self._mode = change['mode']
        self._start = change['start']

    @property
    def mode(self):
        return self._mode

    @property
    def start(self):
        return self._start
