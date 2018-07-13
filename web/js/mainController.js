app.controller('MainController', function ($scope, $window, $currentSystem, DashboardService, CurrentState) {
    $scope.CurrentState = CurrentState;

    $scope.dashboardOptions = {
        widgetDefinitions: [
            {name: 'sensor', templateUrl: '/widget/sensor.htm'},
        ],
        defaultWidgets: [],
        stringifyStorage: false,
        storage: {
            getItem: function (key) {
                return DashboardService.get({system: $currentSystem}).$promise;
            },
            setItem: function (key, value) {
                DashboardService.update({system: $currentSystem}, value, function() {},
                    function(r) {
                        alert(r.data.message || "Error");
                    });
            },
            removeItem: function (key) {
                return null;
            }
        },
        storageId: 'default'
    };
});