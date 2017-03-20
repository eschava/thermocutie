app.factory('TemperatureModeService', function ($resource) {
    return $resource('/rest/temperaturemode/:system/:name/:action/:oldName/:newName', {}, {
        getModes: { method: 'GET', isArray: true, params: {system: '@system'} },
        add:      { method: 'POST', params: {system: '@system'} },
        update:   { method: 'PUT', params: {system: '@system'} },
        rename:   { method: 'PUT', params: {system: '@system', action: 'rename', oldName: '@oldName', newName: '@newName'} },
        delete:   { method: 'DELETE', params: {system: '@system', name: '@name'} }
    })
});