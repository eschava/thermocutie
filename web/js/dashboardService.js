app.factory('DashboardService', function ($resource) {
    return $resource('/rest/dashboard', {}, {
        get:  { method: 'GET', isArray: true, transformResponse: function(data, headersGetter, status) {
            return angular.fromJson(data);
        }},
        update: { method: 'PUT' }
    })
});