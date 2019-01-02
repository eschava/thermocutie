app.config(function($routeProvider, $locationProvider) {
    $routeProvider

    .when("/settings",                  { templateUrl : "/settings.htm", controller : "SettingsController"})
    .when("/devices",                   { templateUrl : "/devices.htm", controller : "DevicesController"})
    .when("/mqttbrokers",               { templateUrl : "/mqttbrokers.htm", controller : "MqttBrokersController"})
    .when("/schedule",                  { templateUrl : "/schedule.htm", controller : "ScheduleController"})
    .when("/temperaturemode",           { templateUrl : "/temperaturemode.htm", controller : "TemperatureModeController"})
    .when("/",                          { templateUrl : "/main.htm", controller : "MainController"})

    ;

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});