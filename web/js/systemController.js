app.controller('SystemController', function ($scope, $window, $currentSystem, $mdDialog, SystemService) {
    $scope.back = function() {$window.history.back();}

    $scope.systems = SystemService.getSystems()

    $scope.currentSystem = $currentSystem

    $scope.add = function() {
        $mdDialog.show({
          controller: EditSystemController,
          locals: { system: {} },
          templateUrl: '/systemedit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false
        })
        .then(function(system) {
            SystemService.add({name: system.name, title: system.title}, function() {
                $scope.systems.push(system);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.rename = function(system) {
        var confirm = $mdDialog.prompt()
          .title('Input new system title')
          .placeholder('Title')
          .initialValue(system.title)
          .ok('Rename')
          .cancel('Cancel');

        $mdDialog.show(confirm).then(function(newTitle) {
            SystemService.update({name: system.name, title: newTitle}, function() {
                system.title = newTitle;
            }, function(r) {
              alert(r.data.message || "Error");
            });
        });
    }

    $scope.delete = function(system) {
        var confirm = $mdDialog.confirm()
            .title('OK to delete system ' + system.title + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            SystemService.delete({name: system.name}, function() {
                $scope.systems.splice($scope.systems.indexOf(system), 1);
            }, function(r) {
                alert(r.data.message || "Error");
            });
        });
    }

    $scope.switch = function(system) {
        $window.location.href = '/' + system.name + '/'; // not $location.url to reload the page
    }

    function EditSystemController($scope, $mdDialog, system) {
        $scope.edit = system.name != undefined;
        $scope.title = $scope.edit ? "Edit system" : "Add system";
        $scope.system = system;

        $scope.save = function() {
            $mdDialog.hide($scope.system);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});