app.factory('ScheduleService', function ($resource) {
    return $resource('/rest/schedule/:system/:name', {}, {
        query: { method: 'GET', params: {system: '@system', name: '@name'} },
        updateWeekSchedule: { method: 'PUT', params: {system: '@system'} },
    })
});