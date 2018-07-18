app.config(function($routeProvider, $locationProvider, $provide) {
    $routeProvider

    .when("/settings",                  { templateUrl : "/settings.htm", controller : "SettingsController"})
    .when("/:system/settings",          { templateUrl : "/settings.htm", controller : "SettingsController"})

    .when("/system",                    { templateUrl : "/system.htm", controller : "SystemController"})
    .when("/:system/system",            { templateUrl : "/system.htm", controller : "SystemController"})

    .when("/devices",                   { templateUrl : "/devices.htm", controller : "DevicesController"})
    .when("/:system/devices",           { templateUrl : "/devices.htm", controller : "DevicesController"})

    .when("/mqttbrokers",               { templateUrl : "/mqttbrokers.htm", controller : "MqttBrokersController"})
    .when("/:system/mqttbrokers",       { templateUrl : "/mqttbrokers.htm", controller : "MqttBrokersController"})

    .when("/schedule",                  { templateUrl : "/schedule.htm", controller : "ScheduleController"})
    .when("/:system/schedule",          { templateUrl : "/schedule.htm", controller : "ScheduleController"})

    .when("/temperaturemode",           { templateUrl : "/temperaturemode.htm", controller : "TemperatureModeController"})
    .when("/:system/temperaturemode",   { templateUrl : "/temperaturemode.htm", controller : "TemperatureModeController"})

    .when("/",                          { templateUrl : "/main.htm", controller : "MainController"})
    .when("/:system/",                  { templateUrl : "/main.htm", controller : "MainController"})

    ;

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });

    // TODO: temporary fix for https://github.com/brianpkelley/md-color-picker/issues/100 issue
    // TODO: taken from https://github.com/brianpkelley/md-color-picker/issues/100#issuecomment-316028858
    // Decorate the $mdDialog service using $provide.decorator
    $provide.decorator("$mdDialog", function ($delegate) {
        // Get a handle of the show method
        var methodHandle = $delegate.show;
        function decorateDialogShow () {
            var args = angular.extend({}, arguments[0], { multiple: true })
            return methodHandle(args);
        }
        $delegate.show = decorateDialogShow;
        return $delegate;
    });
});