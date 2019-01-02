app.factory('ScheduleService', function ($resource) {
    return $resource('/rest/schedule/:name', {}, {
        query: { method: 'GET', params: {name: '@name'} },
        updateWeekSchedule: { method: 'PUT' },
    })
});