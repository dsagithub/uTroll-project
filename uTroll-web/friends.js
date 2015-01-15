var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost" // server ip
var USERNAME = "none";
var PASSWORD = "none";
var GID = -1;
var TROLL = false;

function readCookies() {
	var user;
	var userVar = "username=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(userVar) == 0)
			user = c.substring(userVar.length, c.length);
	}
	
	var pass;
	var passVar = "password=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(passVar) == 0)
			pass = c.substring(passVar.length, c.length);
	}

	window.alert(user);
	window.alert(pass);
	
	USERNAME = user;
	PASSWORD = pass;
}

function getFriends() {
	// window.alert("iniciando 1");
	// falta añadir funcion de los motores quizas separar likes dislikes
	var url = API_BASE_URL + '/friends/getUniqueFriends/';
	// $("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		var users = data;
		// window.alert("iniciando 2");
		$.each(users, function(i, v) {
			var user = v;
			// window.alert("iniciando 3");
			$.each(user, function(i, v) {
				var us = v;
				// window.alert(us.username);
				if (us.username != undefined) {
					createFriends(us.username, false);
				}
			});
		});
	}).fail(function() {
		$("#friends_space").text("No tienes amigos...");
	});
}

function createFriends(u, vote) {

	var tbl = document.getElementById("friends_space"),

	tr = document.createElement('tr');
	tr = tbl.insertRow();// inserta fila en tabla

	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila
	tda.setAttribute('style', 'width:100%');

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u)
	tda.appendChild(ablock);// crea un textnode y lo añade a la celda

	if (!vote) {
		var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
		tdt.setAttribute('class', 'btn btn-primary btn-danger btn-xs');
		tdt.appendChild(document.createTextNode('Vota al Troll'));
		tdt.setAttribute('onclick', 'voteTroll(' + u + ')');// modifica atributo
		tdt.onclick = function() {		voteTroll(u);	};	
		tdt.setAttribute('style', 'float:right');
	}
}

function getPendingFriends() {
	// window.alert("iniciando 1");
	var url = API_BASE_URL + '/friends/getPendingFriends/';
	// $("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		var users = data;
		// window.alert("iniciando 2");
		$.each(users, function(i, v) {
			var user = v;
			// window.alert("iniciando 3");
			$.each(user, function(i, v) {
				var us = v;
				// window.alert(us.username);
				if (us.username != undefined) {
					createPendingFriends(us.username);
				}
			});
		});
	}).fail(function() {
		$("#friends_space").text("No tienes solicitudes pendientes.");
	});
}

function createPendingFriends(u) {

	var tbl = document.getElementById("pending_space"),

	tr = document.createElement('tr');
	tr = tbl.insertRow();// inserta fila en tabla

	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila
	tda.setAttribute('style', 'width:100%');

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u);
	tda.appendChild(ablock);

	var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
	tdt.setAttribute('class', 'btn btn-primary btn-success btn-xs');
	tdt.appendChild(document.createTextNode('Acepta'));
	tdt.setAttribute('onclick', 'acceptFriend(' + u + ')');// modifica atributo
			tdt.onclick = function() {		acceptFriend(u);	};		
	tdt.setAttribute('style', 'float:right');
}

function getSentFriends() {
	// window.alert("iniciando 1");
	var url = API_BASE_URL + '/friends/pendingUnique/' + USERNAME;
	// $("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		var friendlist = data;
		// window.alert("iniciando 2"+friendlist);
		$.each(friendlist, function(i, v) {
			var friend = v;
			// window.alert("iniciando 3"+data);
			$.each(friend, function(i, v) {
				var fr = v;
				// window.alert("aaaa "+fr.friend2);
				if (fr.friend2 != undefined) {
					createSentFriends(fr.friend2);
				}
			});
		});
	}).fail(function() {
		$("#sent_space").text("No solicitudes pendientes de respuesta.");
	});
}

function createSentFriends(u) {

	var tbl = document.getElementById("sent_space"),

	tr = document.createElement('tr');
	tr = tbl.insertRow();// inserta fila en tabla
	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila
	tda.setAttribute('style', 'width:100%');

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u);

	tda.appendChild(ablock);// crea un textnode y lo añade a la celda

}

function acceptFriend(username) {
	// getUserPass();

	var url = API_BASE_URL + '/acceptFriend/' + username;
	var data = JSON.stringify("");

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.friendlist+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		window.alert("Aceptado!");
		// getFriends();
		// getPendingFriends();
		// getSentFriends();
	}).fail(function() {
		window.alert("FAIL");
	});
}

function searchUser() {

	document.getElementById("friends_search").innerHTML = "";
	var mod_search = document.getElementById("search_block");

	mod_search.style.visibility = 'visible'; // visible
	mod_search.style.display = 'block'; // ocupa espacio

	s = $("#search_form").val();

	var url = API_BASE_URL + '/users?username=' + s;
	var data = JSON.stringify("");

	// window.alert(url);

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		var users = data;
		// window.alert("iniciando 2");
		$.each(users, function(i, v) {
			var user = v;
			// window.alert("iniciando 3");
			$.each(user, function(i, v) {
				var us = v;
				// window.alert(us.username);
				if (us.username != undefined) {
					createSearchedFriends(us.username, us.name);
				}
			});
		});
	}).fail(function() {
		$("#friends_space").text("No tienes amigos...");
	});
}

function createSearchedFriends(u, n) {

	var tbl = document.getElementById("friends_search"),

	tr = document.createElement('tr');
	tr = tbl.insertRow();// inserta fila en tabla

	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila
	tda.setAttribute('style', 'width:100%');

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u);
	tda.appendChild(ablock);// crea un textnode y lo añade a la celda

	var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
	tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');
	tdt.appendChild(document.createTextNode('Enviar petición de amistad'));
		tdt.onclick = function() {		addFriend(u);	};	
	tdt.setAttribute('onclick', 'addFriend(' + u + ')');// modifica atributo de

	tdt.onclick = function() {
		addFriend(u);
	}; // TIENES QUE AÑADIR ESTA LÍNEA PARA QUE FUNCIONEN

	tdt.setAttribute('style', 'float:right');// modifica atributo de la celda
	// falta que responda algo como acceptFriend(u);
}

function addFriend(u) {
	// getUserPass();

	window.alert("HOLA");

	var url = API_BASE_URL + '/friends/addFriend/' + u;
	var data = JSON.stringify("");

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.friendlist+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		window.alert("Added!");
		getPendingFriends();
		// checkCookie();
	}).fail(function() {
		window.alert("FAIL");
	});
}