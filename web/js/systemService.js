app.factory('SystemService', function ($resource) {
    return $resource('/rest/system/:name/:title', {}, {
        getSystems: { method: 'GET', isArray: true },
        add: { method: 'PUT', params: {name: '@name', title: '@title'} },
        update: { method: 'POST', params: {name: '@name', title: '@title'} },
        remove: { method: 'DELETE', params: {name: '@name'} },
    })
});