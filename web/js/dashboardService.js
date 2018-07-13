app.factory('DashboardService', function ($resource) {
    return $resource('/rest/dashboard/:system', {}, {
        get:  { method: 'GET', params: {system: '@system'}, transformResponse: function(data, headersGetter, status) {
            return angular.fromJson(data);
        }},
        update: { method: 'PUT', params: {system: '@system'} }
    })
});