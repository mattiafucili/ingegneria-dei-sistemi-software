var port = 8686;
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

http.createServer(function(req,res){
	console.log('Server ' + req.url + ' send new data');
    res.writeHeader(200, {'Content-Type': 'application/JSON'});
    switch(req.url){
    	case '/accelerometerChange':
            req.on('data', function(value) {
    		console.log('' + value);
    		});
    		break;
    	default:
    		res.write('{"hello" : "world"}');
    }
    res.end();
}).listen(port);
console.log('Server listening on hhtp://localhost:' + port);


