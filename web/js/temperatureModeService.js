app.factory('TemperatureModeService', function ($resource) {
    return $resource('/rest/temperaturemode/:name/:action/:oldName/:newName', {}, {
        getModes: { method: 'GET', isArray: true },
        add:      { method: 'POST' },
        update:   { method: 'PUT' },
        rename:   { method: 'PUT', params: {action: 'rename', oldName: '@oldName', newName: '@newName'} },
        delete:   { method: 'DELETE', params: {name: '@name'} }
    })
});