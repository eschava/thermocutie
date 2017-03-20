app.controller('DevicesController', function ($scope, $window, DevicesService) {
    $scope.back = function() {$window.history.back();}

    $scope.devices = DevicesService.getDevices({system: 'default'})
});