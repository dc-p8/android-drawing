var http = require('http');
var fs = require('fs');
var Canvas = require('canvas');
var Image = Canvas.Image;
var canvas;
var ctx;

var server = http.createServer(function(req, res) {
	res.end();
});
server.listen(80);
var io = require('socket.io').listen(server);
var line_per_socket = {};

process.on('SIGINT', function() {
    console.log("Caught interrupt signal");
  	var out = fs.createWriteStream(__dirname + '/out.png');
  	canvas.pngStream().pipe(out);
		out.on('finish', function () {
    process.exit();
  })
});

fs.readFile(__dirname + '/img.png', function(err, squid){
  if (err) throw err;
  img = new Image;
  img.src = squid;
	canvas = new Canvas(img.width, img.height);
	ctx = canvas.getContext('2d');
	
	ctx.lineCap = 'round';
  ctx.drawImage(img, 0, 0, img.width, img.height);
  console.log('read ok');
});


var test = function()
{
	for(var socket in line_per_socket) {
		console.log(socket);
		io.sockets.connected[socket].broadcast.emit('lines', line_per_socket[socket]);
    	delete line_per_socket[socket];
	}
}
setInterval(function(){
	test();
}, 100)


io.sockets.on('connection', function (socket) {
    console.log('Un client est connecté !');
		socket.on('line', function(data){
			ctx.beginPath();
			ctx.moveTo(data.startx, data.starty);
			ctx.lineTo(data.endx, data.endy);
			ctx.strokeStyle = data.colstr;
			ctx.lineWidth = data.stroke;
			ctx.stroke();
			if(!line_per_socket[socket.id])
				line_per_socket[socket.id] = [];
			line_per_socket[socket.id].push(data);
		});
		socket.on('requestbitmap', function(){
			console.log('bitmap request ' + socket.id);
			let stream = canvas.pngStream();
			stream.on('data', function(chunk)
			{
				socket.emit('chunk', chunk);
			});
			stream.on('end', function(){
				console.log('end chunk');
				socket.emit('endchunk');
			});
		});
		socket.on('disconnect', function(){
			console.log('Client déconnecté !');
		});
});

