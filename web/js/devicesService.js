app.factory('DevicesService', function ($resource) {
    return $resource('/rest/device/:name', {}, {
        getDevices:  { method: 'GET', isArray: true },
        getDevicesByType:  { method: 'GET', isArray: true, params: {name: '@name'} },
        add:    { method: 'POST' },
        update: { method: 'PUT', params: {name: '@name'} },
        delete: { method: 'DELETE', params: {name: '@name'} }
    })
});