import wrapt


class SystemState(object):
    def __init__(self):
        self._state = {}
        self._listeners = []

    @wrapt.synchronized
    def device(self, name, value):
        self._state[name] = value
        data = {name: value}
        for listener in self._listeners:
            listener(data)

    @wrapt.synchronized
    def subscribe(self, listener):
        self._listeners.append(listener)
        listener(self._state)

    @wrapt.synchronized
    def unsubscribe(self, listener):
        self._listeners.remove(listener)
