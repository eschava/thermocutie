app.controller('MainController', function ($scope, $window, $timeout, $mdDialog, $currentSystem, DashboardService, DevicesService, CurrentState) {
    $scope.CurrentState = CurrentState;

    $scope.dashboardOptions = {
        widgetDefinitions: [
            {name: 'Sensor', templateUrl: '/widget/Sensor.htm'},
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

    $scope.more = function(widget) {
        var menu = document.getElementById('editWidgetMenu');
        var c = GetScreenCoordinates(event.target);
        menu.style.left = c.x;
        menu.style.top = c.y;

        event.stopPropagation();

        $scope.currentWidget = widget;

        $timeout(function(){
            angular.element(menu).triggerHandler('click');
        }, 300);
    }

    $scope.settingsMenu = function() {
        var menu = document.getElementById('settingsMenu');
        var c = GetScreenCoordinates(event.target);
        menu.style.left = c.x;
        menu.style.top = c.y;

        event.stopPropagation();

        $scope.widgetTypes = $scope.dashboardOptions.widgetDefinitions.map(function(definition){return definition.name;});

        $timeout(function(){
            angular.element(menu).triggerHandler('click');
        }, 300);
    }

    $scope.addWidget = function(deviceType) {
        $mdDialog.show({
          controller: EditWidgetController,
          locals: { widget: {name: deviceType} },
          templateUrl: '/widget/' + deviceType + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(widget) {
            $scope.dashboardOptions.addWidget(widget);
        });
    }

    $scope.editWidget = function() {
        var widget = $scope.currentWidget;
        $mdDialog.show({
          controller: EditWidgetController,
          locals: { widget: angular.copy(widget) },
          templateUrl: '/widget/' + widget.name + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(changedWidget) {
            angular.copy(changedWidget, widget);
            $scope.dashboardOptions.saveDashboard();
        });
    }

    $scope.removeWidget = function() {
        var widget = $scope.currentWidget;
        var confirm = $mdDialog.confirm()
            .title('OK to delete widget for ' + widget.attrs.device + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            $scope.dashboardOptions.removeWidget(widget);
        });
    }

    function EditWidgetController($scope, $mdDialog, widget) {
        $scope.edit = widget.attrs != undefined;
        $scope.title = $scope.edit ? "Edit widget" : "Add widget";
        $scope.widget = widget;
        $scope.devices = DevicesService.getDevicesByType({system: $currentSystem, name: widget.name});

        $scope.save = function() {
            $mdDialog.hide($scope.widget);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});