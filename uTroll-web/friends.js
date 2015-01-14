var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost" //server ip
var USERNAME = "david";
var PASSWORD = "david";
var GID=-1;
var TROLL=false;
   
function getFriends() {
	//window.alert("iniciando 1");
	//falta añadir funcion de los motores quizas separar likes dislikes
	var url = API_BASE_URL + '/friends/getUniqueFriends/';
	//$("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var users = data;
					//window.alert("iniciando 2");
				$.each(users, function(i, v) {
					var user = v;
						//window.alert("iniciando 3");
					$.each(user, function(i, v) {
						var us = v;
							//window.alert(us.username);
						if(us.username!=undefined){
						createFriends(us.username,false);}
					});
				});
			}).fail(function() {
		$("#friends_space").text("No tienes amigos...");
	});
}

function createFriends(u,vote){
	
    	var tbl = document.getElementById("friends_space"),
		
		tr  = document.createElement('tr');
      tr = tbl.insertRow();//inserta fila en tabla
      
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      tda.setAttribute('style','width:100%');
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
        
        if (!vote) {
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-danger btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('onclick', 'voteTroll(' + u + ')');// modifica atributo de
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			}
}

function getPendingFriends() {
   //window.alert("iniciando 1");
	var url = API_BASE_URL + '/friends/getPendingFriends/';
	//$("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var users = data;
					//window.alert("iniciando 2");
				$.each(users, function(i, v) {
					var user = v;
						//window.alert("iniciando 3");
					$.each(user, function(i, v) {
						var us = v;
							//window.alert(us.username);
						if(us.username!=undefined){
						createPendingFriends(us.username);}
					});
				});
			}).fail(function() {
		$("#friends_space").text("No tienes solicitudes pendientes.");
	});
}

function createPendingFriends(u){
	
    	var tbl = document.getElementById("pending_space"),
	
      tr  = document.createElement('tr');
      tr = tbl.insertRow();//inserta fila en tabla
      
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      tda.setAttribute('style','width:100%');
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Acepta'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('onclick', 'acceptFriend(' + u + ')');// modifica atributo de
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			//falta que responda algo como acceptFriend(u);
}

function getSentFriends() {
   //window.alert("iniciando 1");
	var url = API_BASE_URL + '/friends/pendingUnique/'+USERNAME;
	//$("#friends_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var friendlist = data;
					//window.alert("iniciando 2"+friendlist);
				$.each(friendlist, function(i, v) {
					var friend = v;
						//window.alert("iniciando 3"+data);
					$.each(friend, function(i, v) {
						var fr = v;
							//window.alert("aaaa "+fr.friend2);
						if(fr.friend2!=undefined){
						createSentFriends(fr.friend2);}
					});
				});
			}).fail(function() {
		$("#sent_space").text("No solicitudes pendientes de respuesta.");
	});
}

function createSentFriends(u){

    	var tbl = document.getElementById("sent_space"),
	
		tr  = document.createElement('tr');
      tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      tda.setAttribute('style','width:100%');      
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 


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
		//getFriends();
		//getPendingFriends();
		//getSentFriends();
	}).fail(function() {
		window.alert("FAIL");
	});
}

function searchUser() {
	
	document.getElementById("friends_search").innerHTML = "";
	var mod_search=document.getElementById("search_block");
	
	mod_search.style.visibility='visible';			//visible
	mod_search.style.display = 'block';				//ocupa espacio
	
	s = $("#search_form").val();
	
	var url = API_BASE_URL + '/users?username='+ s;
	var data = JSON.stringify("");

		window.alert(url);
	
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var users = data;
					//window.alert("iniciando 2");
				$.each(users, function(i, v) {
					var user = v;
						//window.alert("iniciando 3");
					$.each(user, function(i, v) {
						var us = v;
							//window.alert(us.username);
						if(us.username!=undefined){
						createSearchedFriends(us.username,us.name);}
					});
				});
			}).fail(function() {
		$("#friends_space").text("No tienes amigos...");
	});
}

function createSearchedFriends(u,n){
	
    	var tbl = document.getElementById("friends_search"),
	
      tr  = document.createElement('tr');
      tr = tbl.insertRow();//inserta fila en tabla
      
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      tda.setAttribute('style','width:100%');
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Enviar petición de amistad'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('onclick', 'addFriend(' + u + ')');// modifica atributo de
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			//falta que responda algo como acceptFriend(u);
}

function addFriend(u) {
	// getUserPass();

	
	var url = API_BASE_URL + '/friends/addFriend/'+u;
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
		//checkCookie();
	}).fail(function() {
		window.alert("FAIL");
	});
}