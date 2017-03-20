app.factory('MqttBrokersService', function ($resource) {
    return $resource('/rest/mqttbrokers/:name', {}, {
        getBrokers: { method: 'GET', isArray: true},
        getInternal: { method: 'GET', params: {name: 'internal'}},
        updateInternal: { method: 'POST', params: {name: 'internal'} }
    })
});