app.controller('DevicesController', function ($scope, $window, CurrentSystemService, DevicesService) {
    $scope.back = function() {$window.history.back();}

    $scope.devices = DevicesService.getDevices({system: CurrentSystemService})
});