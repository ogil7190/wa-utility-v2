var app = require('express')();
var count = 0;
var namespace = '/';
var server = require('http').Server(app);
var io = require('socket.io')(server);
var rooms = [[ 'ogil','need',[],[] ]]; /* 2-D array contains rooms, each room array contains name, status, users + data, last 50 mssgs */
var soc;
var room_index = 0;
server.listen(process.env.PORT || 8080, function(){
	console.log("==> Server is running...");
});

io.on('connection', function(socket){
	count++;
	console.log("==> New User & User Count:"+count);
	var room_id;
	soc = socket;
	socket.on('data', function(data) {
		room_id = getRoomID(socket.id, data);
		socket.join(room_id);
		users = getUsers(room_id);
		//console.log("Joined:"+room_id);
		socket.emit('data_join', { id: socket.id, room: room_id, users: users, mssgs: getRoomMssgs(room_id) });
		socket.broadcast.to(room_id).emit('new_user_join', { id:socket.id, data:data });
	});

	socket.on('new_mssg', function(data) {
		socket.broadcast.to(room_id).emit('new_mssg', data);
		//console.log("Message:"+JSON.stringify(data));
		pushMessage(data, getRoom(room_id));
	});

	socket.on('disconnect', function(){
		socket.broadcast.to(room_id).emit('user_disconnected', { id: socket.id });
		//console.log("Disconnect:"+room_id);
		updateRooms(room_id, socket.id);
		count--;
	});

	socket.on('chatRequest', function(data){
		id = getUserID(data);
	});
});

function getRoom(room_id){
	for(i=0; i<rooms.length; i++){
		if(rooms[i][0]== room_id) {
			room_index = i;
			return i;
		}
	}
}

function getUsers(id){
	for(i=0; i<rooms.length; i++){
		if(rooms[i][0]==id){
			return rooms[i][2];
		}
	}
}

function getRoomMssgs(room_id){
	return getMssgs(getRoom(room_id));
}

function pushMessage(data, room){
	mssgs = getMssgs(room);
	//console.log(JSON.stringify(mssgs));
	mssgs.push(data);
	if(mssgs.length>50)
		mssgs.shift();
	return mssgs;
}

function getMssgs(room){
	//console.log("INDEX:"+room_index);
	if(rooms[room_index][3]==null){
		return [];
	}
	return rooms[room_index][3];
}

function updateRooms(room_id, socket_id){
	for(i=0; i<rooms.length; i++){
		if(rooms[i][0]== room_id){
			rooms[i][1] = 'need';
			rooms[i][2] = removeUser(socket_id, rooms[i][2]);
			break;
		}
	}
}

function removeUser(socket_id, json_array){
	for(i=0; i<json_array.length; i++){
		if(json_array[i]['id'] == socket_id){
			json_array.splice(i, 1);
			//console.log("JSON_ARRAY:"+json_array);
			return json_array;
		}
	}
}

var LIMIT = 20; /* room users size */

function getRoomID(socket_id,data){
	for(i=0; i<rooms.length; i++){
		if(rooms[i][1] == 'need'){

			if(io.nsps[namespace].adapter.rooms[rooms[i][0]] == null){
				rooms[i][2].push({ id:socket_id, data:data });
			}
			else 
				if(io.nsps[namespace].adapter.rooms[rooms[i][0]]["length"]==LIMIT-1){
					rooms[i][1] = 'closed';
					rooms[i][2].push({ id:socket_id, data:data });
				}
			return rooms[i][0];
		}
	}

	rooms.push([rooms[0][0]+"-"+rooms.length, 'need',[{ id:socket_id, data:data }]]) /* push new room */
	return rooms[rooms.length-1][0];
}