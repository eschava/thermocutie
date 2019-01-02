app.controller('MainController', function ($scope, $timeout, $mdDialog, CurrentState, DashboardService, DevicesService) {
    $scope.CurrentState = CurrentState;

    $scope.widgets = DashboardService.get()
    $scope.widgets.$promise.then(function( widgets ) {
        $scope.widgets = widgets; // to work with promise as with array
    });

    $scope.saveWidgets = function() {
        if ($scope.widgets.$resolved) { // sometimes callback is called when widgets are not loaded yet
            DashboardService.update($scope.widgets, function() {},
                function(r) {
                    alert(r.data.message || "Error");
            });
        }
    }

    $scope.gridsterOpts = {
        columns: 20, // the width of the grid, in columns
        margins: [0, 0], // the pixel distance between each widget
        minColumns: 1, // the minimum columns the grid must have
        minRows: 1, // the minimum height of the grid, in rows
        defaultSizeX: 1, // the default width of a gridster item, if not specifed
        defaultSizeY: 1, // the default height of a gridster item, if not specified
    };

    // save widgets if size/order is changed
    $scope.$on('gridster-draggable-changed', function(gridster) {
        $scope.saveWidgets();
    })
    $scope.$on('gridster-resizable-changed', function(gridster) {
        $scope.saveWidgets();
    })

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

        $scope.widgetTypes = ['Sensor'];

        $timeout(function(){
            angular.element(menu).triggerHandler('click');
        }, 300);
    }

    $scope.addWidget = function(deviceType) {
        $mdDialog.show({
          controller: EditWidgetController,
          locals: { widget: {type: deviceType} },
          templateUrl: '/widget/' + deviceType + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(widget) {
            $scope.widgets.push(widget);
            $scope.saveWidgets();
        });
    }

    $scope.editWidget = function() {
        var widget = $scope.currentWidget;
        $mdDialog.show({
          controller: EditWidgetController,
          locals: { widget: angular.copy(widget) },
          templateUrl: '/widget/' + widget.type + '_edit.htm',
          parent: angular.element(document.body),
          //targetEvent: ev,
          clickOutsideToClose:false,
          skipHide: true
        })
        .then(function(changedWidget) {
            angular.copy(changedWidget, widget);
            $scope.saveWidgets();
        });
    }

    $scope.removeWidget = function() {
        var widget = $scope.currentWidget;
        var confirm = $mdDialog.confirm()
            .title('OK to delete widget for ' + widget.device + '?')
            .ok('Delete')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            var index = $scope.widgets.indexOf(widget);
            if (index > -1) {
                $scope.widgets.splice(index, 1);
                $scope.saveWidgets();
            }
        });
    }

    function EditWidgetController($scope, $mdDialog, widget) {
        $scope.edit = widget.device != undefined;
        $scope.title = $scope.edit ? "Edit widget" : "Add widget";
        $scope.widget = widget;
        $scope.devices = DevicesService.getDevicesByType({name: widget.type});

        $scope.save = function() {
            $mdDialog.hide($scope.widget);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});