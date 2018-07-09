app.controller('DevicesController', function ($scope, $window, $currentSystem, $mdDialog, DevicesService, MqttBrokersService) {
    $scope.back = function() {$window.history.back();}

    $scope.devices = DevicesService.getDevices({system: $currentSystem})

    $scope.edit = function(device) {
        $mdDialog.show({
          controller: EditDeviceController,
          locals: { device: angular.copy(device) },
          templateUrl: '/device/' + device.type + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:true,
          skipHide: true
        })
        .then(function(changedDevice) {
            angular.copy(changedDevice, device);
        });
    }

    function EditDeviceController($scope, $mdDialog, device) {
        var name = device.name;
        $scope.edit = device.name != undefined;
        $scope.title = $scope.edit ? "Edit " + device.name : "Add device";
        $scope.device = device;
        $scope.mqttBrokers = MqttBrokersService.getBrokers()

        $scope.save = function() {
            DevicesService.update({system: $currentSystem, name: name}, $scope.device, function() {
                $mdDialog.hide($scope.device);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});