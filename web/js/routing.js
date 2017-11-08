app.config(function($routeProvider, $locationProvider) {
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
});