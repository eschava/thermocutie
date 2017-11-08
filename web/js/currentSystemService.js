app.factory('CurrentSystemService', function ($routeParams) {
    return $routeParams.system != null ? $routeParams.system : 'default'
});