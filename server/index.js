var app = require('express')();
var count = 0;
var namespace = '/';
var server = require('http').Server(app);
var io = require('socket.io')(server);
var rooms = [[ 'ogil','need',[],[] ]]; /* 2-D array contains rooms, each room array contains name, status, users + data, last 50 mssgs */
var soc;
server.listen(process.env.PORT || 8080, function(){
	console.log("==> Server is running...");
});

io.on('connection', function(socket){
	count++;
	console.log("==> New User & User Count:"+count);
	var room_id;
	var room;
	soc = socket;
	socket.on('data', function(data){
		room = getRoom(socket.id, data);
		room_id = room[0];
		socket.join(room_id);
		users = getUsers(room);
		socket.emit('data_join', { id: socket.id, room: room_id, users: users });
		socket.broadcast.to(room_id).emit('new_user_join', { id:socket.id, data:data });
	});

	socket.on('user_mssg', function(data) {
		socket.broadcast.to(room_id).emit('user_mssg', data);
		pushMessage(data, room);
	});

	socket.on('disconnect', function(){
		socket.broadcast.to(room_id).emit('user_disconnected', { id: socket.id });
		updateRoom(room, socket.id);
	});
});

function getRoom(room_id) {
	for(i=0; i<rooms.length; i++){
		if(rooms[i][0]==room_id){
			return rooms[i];
		}
	}
}

function getUsers(room){
	return room[2];
}

function pushMessage(data, room){
	name = data["name"];
	mssg = data["mssg"];
	mssgs = getMssgs(room).push({ name : name, mssg: mssg });
	if(mssgs.length>50)
		mssgs.shift();
	room[3] = mssgs;
}

function getMssgs(room){
	return room[3];
}

function updateRoom(room, socket_id){
	room[1] = 'need';
	room[2] = removeUser(socket_id, room[2]);
}

function removeUser(socket_id, json_array){
	for(i=0; i<json_array.length; i++){
		if(json_array[i]['socket_id'] == socket_id){
			json_array.splice(i, 1);
			return json_array;
		}
	}
}

var LIMIT = 2;

function getRoom(socket_id,data){
	for(i=0; i<rooms.length; i++){
		if(rooms[i][1] == 'need'){
			if(io.nsps[namespace].adapter.rooms[rooms[i][0]] == null){
				rooms[i][2].push({ socket_id:socket_id, data:data });
			}
			else if(io.nsps[namespace].adapter.rooms[rooms[i][0]]["length"]==LIMIT-1){
				rooms[i][1] = 'closed';
				rooms[i][2].push({ socket_id:socket_id, data:data });
			}
			return rooms[i];
		}
	}

	rooms.push([rooms[0][0]+"-"+rooms.length, 'need',[{ socket_id:socket_id, data:data }]]) /* push new room */
	return rooms[rooms.length-1][0];
}