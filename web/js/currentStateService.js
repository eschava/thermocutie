app.factory('CurrentState', function($websocket, $location,) {
    // Open a WebSocket connection
    var ws = $websocket('ws://' + $location.host() + ':' + $location.port() + '/state');
    ws.send(""); // subscribe to the active system state

    var state = [];

    ws.onMessage(function(message) {
        angular.extend(state, JSON.parse(message.data));
    });

    var methods = {
        state: state,
        get: function() {
            ws.send(JSON.stringify({ action: 'get' }));
        }
    };

    return methods;
});