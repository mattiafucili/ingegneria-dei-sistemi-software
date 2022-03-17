var http = require('http');

var options = {
  hostname: 'localhost',
  path: '/accelerometer',
  port: 8585,
  method: 'POST'
};

callback = function(response){
    response.on('data', function(value) {
    	console.log('' + value);
    });
}

req = http.request(options, callback);
req.end();
