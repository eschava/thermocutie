app.controller('MqttBrokersController', function ($scope, $window, MqttBrokersService) {
    $scope.back = function() {$window.history.back();}

    $scope.updateInternal = function() {
        MqttBrokersService.updateInternal($scope.internalBroker, function() {
            window.history.back();
        });
    }

    $scope.internalBroker = MqttBrokersService.getInternal()
    $scope.brokers = MqttBrokersService.getBrokers()
});