var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost/troll" //server ip
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
	

    	var space = document.getElementById("friends_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
        
        if (!vote) {
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			}
    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('/'));
    
 
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
	
    	var space = document.getElementById("pending_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Acepta'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			//falta que responda algo como acceptFriend(u);
    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('.'));

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

    	var space = document.getElementById("sent_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	
      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('.'));

}


