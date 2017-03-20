app.factory('DevicesService', function ($resource) {
    return $resource('/rest/device/:system', {}, {
        getDevices:  { method: 'GET', isArray: true, params: {system: '@system'} },
    })
});