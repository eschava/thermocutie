app.factory('DevicesService', function ($resource) {
    return $resource('/rest/device/:system/:name', {}, {
        getDevices:  { method: 'GET', isArray: true, params: {system: '@system'} },
        update: { method: 'PUT', params: {system: '@system', name: '@name'} }
    })
});