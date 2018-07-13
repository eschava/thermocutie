app.controller('MainController', function ($scope, $http, CurrentState) {
    $scope.CurrentState = CurrentState;

    $http.get("/example/data/engine.json").then(function(data){
        $scope.data = {};
        $scope.data.WEconfiguration = data.data;
        $scope.data.WEcallback = function(e, configuration){
            console.log(e, configuration);
        };
    });
});