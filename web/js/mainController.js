app.controller('MainController', function ($scope, $window, CurrentState) {
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
        defaultWidgets: [
                          { name: 'random' },
                          { name: 'time' },
                          {
                            name: 'random',
                          },
                          {
                            name: 'time',
                          }
                        ],
        storage: {
            getItem: function (key) {
                return null;
            },
            setItem: function (key, value) {
                return null;
            },
            removeItem: function (key) {
                return null;
            }
        },
        storageId: 'default'
    };
});