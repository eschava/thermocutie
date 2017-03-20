app.controller('TemperatureModeController', function ($scope, $window, $mdDialog, TemperatureModeService) {
    $scope.modes = TemperatureModeService.getModes({system: 'default'});

    $scope.back = function() {$window.history.back();}

    $scope.add = function() {
        $mdDialog.show({
          controller: EditModeController,
          locals: { mode: {} },
          templateUrl: 'temperaturemodeedit.htm',
//          contentElement: '#editModeDialog',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false
        })
        .then(function(mode) {
            TemperatureModeService.add({system: 'default'}, mode, function() {
                $scope.modes.append(mode);
            }, function(r) {
                alert(r.data || "Error");
            });
        });
    }

    $scope.edit = function(mode) {
        $mdDialog.show({
          controller: EditModeController,
          locals: { mode: angular.copy(mode) },
          templateUrl: 'temperaturemodeedit.htm',
//          contentElement: '#editModeDialog',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(changedMode) {
            TemperatureModeService.update({system: 'default'}, changedMode, function() {
                angular.copy(changedMode, mode);
            }, function(r) {
                alert(r.data || "Error");
            });
        });
    }

    $scope.rename = function(mode) {
        var confirm = $mdDialog.prompt()
          .title('Input new mode name')
          .placeholder('Name')
          .initialValue(mode.name)
          .ok('Rename')
          .cancel('Cancel');

        $mdDialog.show(confirm).then(function(newName) {
            TemperatureModeService.rename({system: 'default', oldName: mode.name, newName: newName}, function() {
                mode.name = newName;
            }, function(r) {
              alert(r.data || "Error");
            });
        });
    }

    $scope.delete = function(mode) {
        var confirm = $mdDialog.confirm()
            .title('OK to delete mode ' + mode.name + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            TemperatureModeService.delete({system: 'default', name: mode.name}, function() {
                $scope.modes.splice($scope.modes.indexOf(mode), 1);
            }, function(r) {
                alert(r.data || "Error");
            });
        });
    }

    function EditModeController($scope, $mdDialog, mode) {
        $scope.edit = mode.name != undefined;
        $scope.title = $scope.edit ? "Edit mode" : "Add mode";
        $scope.mode = mode;
        $scope.icons = ['stars-and-moon', 'sun-rising', 'sunrise-hour', 'alert-cloud', 'alert-storm']; // TODO: load from folder

        $scope.save = function() {
            $mdDialog.hide($scope.mode);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});