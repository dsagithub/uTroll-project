var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost/troll" //server ip
var USERNAME = "david";
var PASSWORD = "david";

function getRanking() {
	// getUserPass();
	var url = API_BASE_URL + '/users/ranking';
	$("#ranking_space").text('');
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
				// alert(valueOf(data));
				$.each(users, function(i, v) {
					var user = v;
					$.each(user, function(i, v) {
						var us = v;
						//window.alert(us);

						$('<h3> Username: ' + '<strong>'
										+ us.username + '</strong>'
										+ '</h3>').appendTo(
								$('#ranking_space'));
						$('<p>').appendTo($('#ranking_space'));
						$(
								'<strong> Points: </strong> '
										+ us.points + '<br>').appendTo(
								$('#ranking_space'));
						$('</p>').appendTo($('#ranking_space'));
						
//						$(us.username).appendTo($('#ranking_space'));
//						$(' <strong>' + us.points + '</strong>').appendTo(
//								$('#ranking_space'));
					});
				});
			}).fail(function() {
		$("#ranking_space").text("No hay repositorios.");
	});
}

function getComments() {
	//falta añadir funcion de los motores quizas separar likes dislikes
	var url = API_BASE_URL + '/comments';
	$("#comments_space").text('');
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
				var comments = data;
				$.each(comments, function(i, v) {
					var comment = v;
					$.each(comment, function(i, v) {
						var com = v;
							//window.alert(com.username);
						if(com.username!=undefined){
						createComments(com.username,com.content,com.likes,com.dislikes);}
					});
				});
			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}

function createComments(a,c,l,dl){
	
    	var space = document.getElementById("comments_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	
			//window.alert(a);
      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(a);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+a);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
      
           
      //tda.appendChild(document.createTextNode(a));//crea un textnode y lo añade a la celda 
		tda.setAttribute('colSpan', '3');//modifica atributo de la celda      
        
      var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
      tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
      tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
      //tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
			
		  //window.alert(c);
        
      var tr2 = tbl.insertRow();//inserta fila en tabla
      var tdc = tr2.insertCell();//crea una celda y la inserta en la fila
      tdc.appendChild(document.createTextNode(c));//crea un textnode y lo añade a la celda 
      tdc.setAttribute('colSpan', '4');//modifica atributo de la celda
        //tdc.style.border = "1px solid black";
        
      var tr3 = tbl.insertRow();//inserta fila en tabla
      
      var tddlbtn = tr3.insertCell(0);//crea una celda y la inserta en la fila
      tddlbtn.setAttribute('class', 'btn btn-primary btn-danger btn-xs');//modifica atributo de la celda
      tddlbtn.appendChild(document.createTextNode('NO me gusta +1'));//crea un textnode y lo añade a la celda
      //tddlbtn.setAttribute('style', 'float:left');//modifica atributo de la celda
      
      var tddl = tr3.insertCell(1);//crea una celda y la inserta en la fila
      tddl.appendChild(document.createTextNode('No me gusta: '+dl));//crea un textnode y lo añade a la celda
		//tddl.setAttribute('style', 'float:left');//modifica atributo de la celda      
      
      var tdl = tr3.insertCell(2);//crea una celda y la inserta en la fila
      tdl.appendChild(document.createTextNode('Me gusta: '+l));//crea un textnode y lo añade a la celda 
      //tdl.setAttribute('style', 'float:right');//modifica atributo de la celda
 
 
      var tdlbtn = tr3.insertCell(3);//crea una celda y la inserta en la fila
      tdlbtn.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
      tdlbtn.appendChild(document.createTextNode('Me Gusta +1'));//crea un textnode y lo añade a la celda
      //tdlbtn.setAttribute('style', 'float:right');//modifica atributo de la celda
    	  

    
      space.appendChild(tbl);//añade la tabla al espacio
      space.appendChild(document.createTextNode('/'));
    
 
}
function getCommentsO() {
	// window.alert("HOLA");
	// getUserPass();
	// window.alert("HOLA2");
	var url = API_BASE_URL + '/comments';
	$("#comments_space").text('');
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
				var comments = data;
				$.each(comments, function(i, v) {
					var comment = v;
					$.each(comment, function(i, v) {
						var com = v;

						$(
								'<h3> Username: ' + '<strong>'
										+ com.username + '</strong>'
										+ '</h3>').appendTo(
								$('#comments_space'));
						$('<p>').appendTo($('#comments_space'));
						$(
								'<strong> Comment: </strong> '
										+ com.content + '<br>').appendTo(
								$('#comments_space'));
						// $('<strong> Links: </strong> ' +
						// getRequestHeader("next") +
						// '<br>').appendTo($('#comments_space'));
						$('</p>').appendTo($('#comments_space'));
					});
				});
			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}