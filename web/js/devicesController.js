app.controller('DevicesController', function ($scope, $window, $routeParams, DevicesService) {
    $scope.back = function() {$window.history.back();}

    $scope.devices = DevicesService.getDevices({system: $routeParams.system})
});