app.controller('MqttBrokersController', function ($scope, $window, $mdDialog, MqttBrokersService) {
    $scope.back = function() {$window.history.back();}

    $scope.updateInternal = function() {
        MqttBrokersService.updateInternal($scope.internalBroker, function() {
            window.history.back();
        });
    }

    $scope.internalBroker = MqttBrokersService.getInternal()
    $scope.brokers = MqttBrokersService.getBrokers()

    $scope.add = function() {
        $mdDialog.show({
          controller: EditMqttBrokerController,
          locals: { broker: {} },
          templateUrl: '/mqttbrokeredit.htm',
//          contentElement: '#editBrokerDialog',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false
        })
        .then(function(broker) {
            MqttBrokersService.add(broker, function() {
                $scope.brokers.push(broker);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.edit = function(broker) {
        $mdDialog.show({
          controller: EditMqttBrokerController,
          locals: { broker: angular.copy(broker) },
          templateUrl: '/mqttbrokeredit.htm',
//          contentElement: '#editBrokerDialog',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(changedBroker) {
            MqttBrokersService.update(changedBroker, function() {
                angular.copy(changedBroker, broker);
                broker.connected = false;
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.rename = function(broker) {
        var confirm = $mdDialog.prompt()
          .title('Input new broker name')
          .placeholder('Name')
          .initialValue(broker.name)
          .ok('Rename')
          .cancel('Cancel');

        $mdDialog.show(confirm).then(function(newName) {
            MqttBrokersService.rename({oldName: broker.name, newName: newName}, function() {
                broker.name = newName;
            }, function(r) {
              alert(r.data.message || "Error");
            });
        });
    }

    $scope.delete = function(broker) {
        var confirm = $mdDialog.confirm()
            .title('OK to delete broker ' + broker.name + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            MqttBrokersService.delete({name: broker.name}, function() {
                $scope.brokers.splice($scope.brokers.indexOf(broker), 1);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.connect = function(broker) {
        MqttBrokersService.connect({name: broker.name}, function() {
            broker.connected = true;
        }, function(r) {
            alert(r.data.message || "Error");
        });
    }

    $scope.disconnect = function(broker) {
        var confirm = $mdDialog.confirm()
            .title('OK to disconnect broker ' + broker.name + '?')
            .ok('Disconnect')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            MqttBrokersService.disconnect({name: broker.name}, function() {
                broker.connected = false;
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    function EditMqttBrokerController($scope, $mdDialog, broker) {
        $scope.edit = broker.name != undefined;
        $scope.title = $scope.edit ? "Edit broker" : "Add broker";
        $scope.broker = broker;

        $scope.save = function() {
            $mdDialog.hide($scope.broker);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});