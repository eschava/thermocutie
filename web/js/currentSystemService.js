app.factory('$currentSystem', function ($routeParams) {
    return $routeParams.system != null ? $routeParams.system : 'default'
});