app.controller('DevicesController', function ($scope, $window, $currentSystem, DevicesService) {
    $scope.back = function() {$window.history.back();}

    $scope.devices = DevicesService.getDevices({system: $currentSystem})
});