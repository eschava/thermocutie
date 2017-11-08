app.config(function($routeProvider, $locationProvider) {
    $routeProvider
    .when("/", {
        templateUrl : "main.htm",
        controller : "MainController"
    })
    .when("/settings", {
        templateUrl : "settings.htm",
        controller : "SettingsController"
    })
    .when("/system", {
        templateUrl : "system.htm",
        controller : "SystemController"
    })
    .when("/devices", {
        templateUrl : "devices.htm",
        controller : "DevicesController"
    })
    .when("/mqttbrokers", {
        templateUrl : "mqttbrokers.htm",
        controller : "MqttBrokersController"
    })
    .when("/schedule", {
        templateUrl : "schedule.htm",
        controller : "ScheduleController"
    })
    .when("/temperaturemode", {
        templateUrl : "temperaturemode.htm",
        controller : "TemperatureModeController"
    })
    ;

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});