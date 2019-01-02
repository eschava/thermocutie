app.controller('DevicesController', function ($scope, $window, $timeout, $mdDialog, CurrentState, DevicesService, MqttBrokersService) {
    $scope.CurrentState = CurrentState;
    $scope.devices = DevicesService.getDevices()

    var GetScreenCoordinates = function(obj) {
        var p = {};
        p.x = obj.offsetLeft;
        p.y = obj.offsetTop;
        while (obj.offsetParent) {
            p.x = p.x + obj.offsetParent.offsetLeft;
            p.y = p.y + obj.offsetParent.offsetTop;
            if (obj == document.getElementsByTagName("body")[0]) {
                break;
            }
            else {
                obj = obj.offsetParent;
            }
        }
        return p;
    }

    $scope.addMenu = function() {
        var menu = document.getElementById('addDeviceMenu');
        var c = GetScreenCoordinates(event.target);
        menu.style.left = c.x;
        menu.style.top = c.y;

        event.stopPropagation();

        $scope.deviceTypes = ['Sensor'];

        $timeout(function(){
            angular.element(menu).triggerHandler('click');
        }, 300);
    }

    $scope.add = function(deviceType) {
        $mdDialog.show({
          controller: EditDeviceController,
          locals: { action: 'add', device: {type: deviceType} },
          templateUrl: '/device/' + deviceType + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:true,
          skipHide: true
        })
        .then(function(addedDevice) {
            $scope.devices.push(addedDevice);
        });
    }

    $scope.edit = function(device) {
        $mdDialog.show({
          controller: EditDeviceController,
          locals: { action: 'edit', device: angular.copy(device) },
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

    $scope.duplicate = function(device) {
        $mdDialog.show({
          controller: EditDeviceController,
          locals: { action: 'duplicate', device: angular.copy(device) },
          templateUrl: '/device/' + device.type + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:true,
          skipHide: true
        })
        .then(function(addedDevice) {
            $scope.devices.push(addedDevice);
        });
    }

    $scope.delete = function(device) {
        var confirm = $mdDialog.confirm()
            .title('OK to delete device ' + device.name + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            DevicesService.delete({name: device.name}, function() {
                $scope.devices.splice($scope.devices.indexOf(device), 1);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.back = function() {$window.history.back();}

    function EditDeviceController($scope, $mdDialog, action, device) {
        var name = device.name;
        $scope.action = action;
        $scope.title = action == 'edit' ? "Edit " + device.name :
                       action == 'duplicate' ? "Duplicate " + device.name :
                       'Add device';
        $scope.device = device;
        $scope.mqttBrokers = MqttBrokersService.getBrokers()

        var ok = function() { $mdDialog.hide($scope.device); }
        var error = function (r) { alert(r.data.message || "Error"); }

        $scope.add =    function() { DevicesService.add($scope.device, ok, error); };
        $scope.update = function() { DevicesService.update({name: name}, $scope.device, ok, error); };
        $scope.cancel = function() { $mdDialog.cancel(); };
    }
});