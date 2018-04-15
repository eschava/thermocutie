class MQTTClient(object):
    def __init__(self, xml):
        self._name = xml.attrib['name']
        self._client_id = xml.attrib['clientId']
        self._uri = xml.attrib['uri']

    def save(self, xml):
        xml.attrib['name'] = self._name
        xml.attrib['clientId'] = self._client_id
        xml.attrib['uri'] = self._uri

    @property
    def name(self):
        return self._name

    @property
    def client_id(self):
        return self._client_id

    @property
    def uri(self):
        return self._uri
