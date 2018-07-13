app.controller('MainController', function ($scope, $window, $currentSystem, DashboardService, CurrentState) {
    $scope.CurrentState = CurrentState;

    $scope.randomValue = Math.random();

    $scope.dashboardOptions = {
        widgetDefinitions: [
                               {
                                 name: 'random',
                                 templateUrl: '/widget/test.htm',

                                 attrs: {
                                   value: 'randomValue',
                                 }
                               },
                               {
                                 name: 'time',
                                 directive: 'wt-time'
                               },
                               {
                                 name: 'fluid',
                                 directive: 'wt-fluid',
                               }
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