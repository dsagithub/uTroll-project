var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost/" //server ip
var USERNAME = "albert";
var PASSWORD = "albert";
var GID=-1;
var TROLL=false;

function getRanking() {

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
						if(us.username!=undefined){
						createRanking(us.username,us.points,i+1);}
						
					});
				});
			}).fail(function() {
		$("#ranking_space").text("No hay repositorios.");
	});
}

function getGroup() {
//window.alert("aaa");
	var gid=getOwnGroup();
	
	window.alert("getGroup "+gid);
	
	if (gid==0) {
		$("#group_space").text("No estas en ningun grupo!");
		window.alert("Sin grupo "+gid);
		
		var btn=document.getElementById("group_btn_space");
	
		btn.style.visibility='visible';			//visible
		btn.style.display = 'block';				//ocupa espacio		
		
		getGroupList();
		
	}else if (gid!=0) {
	var url_group = API_BASE_URL + '/users/usersInGroup/'+gid;
	var troll=getOwnTroll();
	window.alert("if 2 "+gid);
	//$("#group_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url_group,
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
						if(us.username!=undefined){
							if (us.username!=USERNAME) {
							createGroup(us.username,troll);}
						
						}
						
					});
				});
			}).fail(function() {
			window.alert("fail "+gid);
	});
	}else if (gid==-1) {
		window.alert("ERROR 2: Gid no identificado");
	}
}

function getGroupList() {
	window.alert("a");

	var url = API_BASE_URL + '/groups';

	window.alert(url);
	
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url_group,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var groups = data;
				window.alert(groups);
				$.each(groups, function(i, v) {
					var group = v;
					$.each(group, function(i, v) {
						var gr = v;
						window.alert(gr);
						if(gr.groupname!=undefined ){
							if (gr.groupid!=0) {
							createGroupList(gr.groupname,gr.groupid,gr.price,gr.state);
							}
						}
						
					});
				});
			}).fail(function() {
		window.alert("group list fail"+gid);
	});

}

function getOwnGroup() {
   
   var gid=-253;
	var url = API_BASE_URL + '/users/byUsername/'+USERNAME;

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
				var uProf = data;
					gid=uProf.groupid;
					window.alert("Fase 1 ->"+" gid: "+gid+" GID "+GID);
					return gid;
			}).fail(function() {
		window.alert("No se encuentra el grupo id");
			return -10;
	});
	window.alert("Fase 2 ->"+" gid: "+gid+" GID "+GID);
	gid=GID;
	window.alert("Fase 3 ->"+" gid: "+gid+" GID "+GID);
return gid;

}
function getOwnTroll() {
   
   var troll=false;
	var url = API_BASE_URL + '/users/byUsername/'+USERNAME;

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
				var uProf = data;
					TROLL=uProf.troll;

					return troll;
			}).fail(function() {
		window.alert("No se encuentra el grupo id");
			return -10;
	});

	troll=TROLL;

return troll;

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
						createComments(com.username,com.content,com.likes,com.dislikes,com.commentid);}
					});
				});
			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}

function createComments(a,c,l,dl,cid){
	
    	var space = document.getElementById("comments_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      //tbl.style.border = "1px solid red";
	
			//window.alert(a);
      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(a+' dice:');

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+a);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
      
           
      //tda.appendChild(document.createTextNode(a));//crea un textnode y lo añade a la celda 
		tda.setAttribute('colSpan', '3');//modifica atributo de la celda      
        
      var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
      tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
      tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
      tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
		tdt.setAttribute('onclick', 'voteTroll('+a+')');//modifica atributo de la celda
			
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
      //tddlbtn.setAttribute('onclick', 'postDislike('+cid+')');//modifica atributo de la celda
      tddlbtn.setAttribute('style', 'float:left');//modifica atributo de la celda
      
      var tddl = tr3.insertCell(1);//crea una celda y la inserta en la fila
      tddl.appendChild(document.createTextNode('No me gusta: '+dl));//crea un textnode y lo añade a la celda
		//tddl.setAttribute('style', 'float:left');//modifica atributo de la celda      
      
      var tdl = tr3.insertCell(2);//crea una celda y la inserta en la fila
      tdl.appendChild(document.createTextNode('Me gusta: '+l));//crea un textnode y lo añade a la celda 
      //tdl.setAttribute('style', 'float:right');//modifica atributo de la celda
 
 
      var tdlbtn = tr3.insertCell(3);//crea una celda y la inserta en la fila
      tdlbtn.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
      tdlbtn.appendChild(document.createTextNode('Me Gusta +1'));//crea un textnode y lo añade a la celda
      tdlbtn.setAttribute('onclick', 'postLike('+cid+')');//modifica atributo de la celda
      tdlbtn.setAttribute('style', 'float:right');//modifica atributo de la celda
    	  

    
      space.appendChild(tbl);//añade la tabla al espacio
    	space.appendChild(document.createElement('P'));
    
 
}

function createGroup(u,vote){
	
    	var space = document.getElementById("group_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      //tbl.style.border = "1px solid red";
	
			//window.alert(a);
      var tr = tbl.insertRow();//inserta fila en tabla
      var tda = tr.insertCell(0);//crea una celda y la inserta en la fila
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
      
           
      //tda.appendChild(document.createTextNode(a));//crea un textnode y lo añade a la celda 
		//tda.setAttribute('colSpan', '3');//modifica atributo de la celda      
      if (!vote) {  
      var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
      tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
      tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
      tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
		tdt.setAttribute('onclick', 'voteTroll('+u+')');//modifica atributo de la celda
		}
    
      space.appendChild(tbl);//añade la tabla al espacio
    	space.appendChild(document.createElement('P'));
    
 
}

function createGroupList(n,gid,p,s){
	
    	var space = document.getElementById("group_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      //tbl.style.border = "1px solid red";
      
		//window.alert(a);
      var tr = tbl.insertRow();//inserta fila en tabla
      
      var tdn = tr.insertCell(0);//crea una celda y la inserta en la fila
      tdn.appendChild(document.createTextNode(n));//crea un textnode y lo añade a la celda 

      var tdp = tr.insertCell(1);//crea una celda y la inserta en la fila
      tdp.appendChild(document.createTextNode(p));//crea un textnode y lo añade a la celda   
      
		if (s='open') {      
      
      var tdt = tr.insertCell(2);//crea una celda y la inserta en la fila
      tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
      tdt.appendChild(document.createTextNode('Entrar'));//crea un textnode y lo añade a la celda
      tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
		tdt.setAttribute('onclick', 'joinGroup('+gid+')');//modifica atributo de la celda

      }
    
      space.appendChild(tbl);//añade la tabla al espacio
    	space.appendChild(document.createElement('P'));
    
}

function createRanking(u,p,rnk){
	
	  	var space = document.getElementById("ranking_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      //tbl.style.border = "1px solid red";
	
			//window.alert(a);
      var tr = tbl.insertRow();//inserta fila en tabla
      
      var tdr = tr.insertCell(0);//crea una celda y la inserta en la fila
      tdr.appendChild(document.createTextNode(rnk));//crea un textnode y lo añade a la celda
      tdr.setAttribute('style', 'width:31px');//modifica atributo de la celda      
      
      var tda = tr.insertCell(1);//crea una celda y la inserta en la fila
		tda.setAttribute('style', 'float:left');//modifica atributo de la celda      
      
      var ablock= document.createElement('A');
      var t=document.createTextNode(u);

		ablock.appendChild(t);      
      ablock.setAttribute('href', WEB_URL+'/profile.html?username='+u);//modificar para q pase como param 
      
      tda.appendChild(ablock);//crea un textnode y lo añade a la celda 
      
      var tdt = tr.insertCell(2);//crea una celda y la inserta en la fila

      tdt.appendChild(document.createTextNode(p));//crea un textnode y lo añade a la celda
      tdt.setAttribute('style', 'float:right');//modifica atributo de la celda
      
      
      space.appendChild(tbl);//añade la tabla al espacio
      //space.appendChild(document.createTextNode('/'));
    	space.appendChild(document.createElement('P'));
 
 }

function voteTroll(username) {
	getUserPass();
	var url = API_BASE_URL + '/user/repos';
	var data = JSON.stringify(repo);
	$("#repos_result").text('');
	
	$.ajax(
			{
			headers : {
			'Authorization' : "Basic "+ btoa(USERNAME + ':' + PASSWORD)
			},
			url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			data : data,
			})
			.done(function(data, status, jqxhr) {
			$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Created</div>').appendTo($("#repos_result"));
			})
			.fail(function() {
			$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#repos_result"));
			});
}

function postLike(cid) {
	//getUserPass();

	var url = API_BASE_URL + '/comments/like/'+cid;
	var data = JSON.stringify("");
	
	//$("#repos_result").text('');

	$
			.ajax(
					{
						headers : {
							'Authorization' : "Basic "
									+ btoa(USERNAME + ':' + PASSWORD)
						},
						url : url,
						type : 'POST',
						crossDomain : true,
						dataType : 'json',
						data : data,
					})
			.done(
					function(data, status, jqxhr) {
						$(
								'<div class="alert alert-success"> <strong>Ok!</strong> Repository Created</div>')
								.appendTo($("#repos_result"));
					})
			.fail(
					function() {
						$(
								'<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>')
								.appendTo($("#repos_result"));
					});
}