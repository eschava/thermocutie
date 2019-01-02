app.factory('MqttBrokersService', function ($resource) {
    return $resource('/rest/mqttbrokers/:name/:action/:newName', {}, {
        getBrokers: { method: 'GET', isArray: true},
        add:            { method: 'POST'},
        update:         { method: 'PUT'},
        rename:         { method: 'PUT', params: {action: 'rename', name: '@oldName', newName: '@newName'} },
        delete:         { method: 'DELETE', params: {name: '@name'} },
        connect:        { method: 'PUT', params: {action: 'connect', name: '@name'} },
        disconnect:     { method: 'PUT', params: {action: 'disconnect', name: '@name'} },
        getInternal:    { method: 'GET', params: {name: 'internal'} },
        updateInternal: { method: 'POST', params: {name: 'internal'} }
    })
});