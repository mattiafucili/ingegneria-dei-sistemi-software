var http = require("http");
var port = 8585;

function randomInt (low, high){
	return Math.floor(Math.random() * (high-low) + low);
}

function sleep(milliseconds) {
  var start = new Date().getTime();
  for (var i = 0; i < 1e7; i++) {
    if ((new Date().getTime() - start) > milliseconds) {
      break;
    }
  }
}

http.createServer(function(req,res){
	console.log('New incoming client request for ' + req.url);
    res.writeHeader(200, {'Content-Type': 'application/JSON'});
    switch(req.url){
    	case '/accelerometer':
        var options = {
          hostname: 'localhost',
          path: '/accelerometerChange',
          port: 8686,
          method: 'POST'
        };
        res = http.request(options);
        res.write('acc: x-y-z');
        res.end();
    		break;
    	default:
    		res.write('{"hello" : "world"}');
    }
    res.end();
}).listen(port);
console.log('Server listening on hhtp://localhost:' + port);