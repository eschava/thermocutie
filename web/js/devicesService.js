app.factory('DevicesService', function ($resource) {
    return $resource('/rest/device/:system/:name', {}, {
        getDevices:  { method: 'GET', isArray: true, params: {system: '@system'} },
        getDevicesByType:  { method: 'GET', isArray: true, params: {system: '@system', name: '@name'} },
        update: { method: 'PUT', params: {system: '@system', name: '@name'} }
    })
});