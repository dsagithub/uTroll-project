var API_BASE_URL = "http://localhost:8010/uTroll-api";
var WEB_URL = "http://localhost/" // server ip

$('select#troll_sign').on('change', function() {
	var valor = $(this).val();
	alert(valor);
});

function getRanking() {

	var u = getCookie('username');
	var p = getCookie('password');

	var url = API_BASE_URL + '/users/ranking';
	$("#ranking_space").text('');
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		var users = data;
		// alert(valueOf(data));
		$.each(users, function(i, v) {
			var user = v;
			$.each(user, function(i, v) {
				var us = v;
				// window.alert(us);
				if (us.username != undefined) {
					createRanking(us.username, us.points, i + 1);
				}

			});
		});
	}).fail(function() {
		$("#ranking_space").text("No hay repositorios.");
	});
}

function getGroup() {
	var u = getCookie('username');
	var p = getCookie('password');
	var gid = getCookie('groupid');

	if (gid == 0) {
		$("#group_space").text("No estas en ningun grupo!");
		window.alert("Sin grupo " + gid);

		var btn = document.getElementById("group_btn_space");

		btn.style.visibility = 'visible'; // visible
		btn.style.display = 'block'; // ocupa espacio

	} else if (gid != 0) {
		var url_group = API_BASE_URL + '/users/usersInGroup/' + gid;
		var troll = getCookie('troll');

		$.ajax({
			headers : {
				'Authorization' : "Basic " + btoa(u + ':' + p)
			},
			url : url_group,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
		}).done(function(data, status, jqxhr) {
			var users = data;
			$.each(users, function(i, v) {
				var user = v;
				$.each(user, function(i, v) {
					var us = v;
					if (us.username != undefined) {
						if (us.username != u) {
							createGroup(us.username, troll);
						}

					}

				});
			});
		}).fail(function() {
			window.alert("FAIL getGroup " + gid);
		});
	} else if (gid == -1) {
		window.alert("ERROR 2: Gid no identificado");
	}
}

function getGroupPrice() {

	var u = getCookie('username');
	var p = getCookie('passwor	d');
	var gid = getCookie('groupid');

	if ((gid != '0') || (gid != 'undefined')) {
		var url = API_BASE_URL + '/groups/' + gid;

		$.ajax({
			headers : {
				'Authorization' : "Basic " + btoa(u + ':' + p)
			},
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
			contentType : 'application/vnd.uTroll.api.group+json',
		}).done(function(data, status, jqxhr) {
			var group = data;
			var price = group.price;
			setCookie('price', price, 1);
			window.alert(price);
			var space = document.getElementById("group_points");
			space.appendChild(document.createTextNode('me cago en to'));
		}).fail(function() {
			window.alert("group price" + gid);
		});
	}
}

// por probar
function getTrollMode() {

	var gid = getCookie('groupid');
	var troll = getCookie('troll');

	if (troll == true) {
		var troll_sign = document.getElementById("troll_sign");

		troll_sign.style.visibility = 'visible'; // visible
		troll_sign.style.display = 'block'; // ocupa espacio

		var url_group = API_BASE_URL + '/users/usersInGroup/' + gid;

		$.ajax({
			headers : {
				'Authorization' : "Basic " + btoa(u + ':' + p)
			},
			url : url_group,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
		}).done(function(data, status, jqxhr) {
			var users = data;
			$.each(users, function(i, v) {
				var user = v;
				$.each(user, function(i, v) {
					var us = v;
					if (us.username != undefined) {
						if (us.username != u) {
							createSignList(us.username);
						}

					}

				});
			});
		}).fail(function() {
			window.alert("fail " + gid);
		});
	}
}

function createSignList(u) {

	var troll_sign = document.getElementById("troll_sign");
	var ablock = document.createElement('OPTION');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	troll_sign.appendChild(ablock);

}

function getComments() {

	var u = getCookie('username');
	var p = getCookie('password');

	// falta añadir funcion de los motores quizas separar likes dislikes
	var url = API_BASE_URL + '/comments';
	$("#comments_space").text('');// limpiar al recargar
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
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
						// window.alert(com.username);
						if (com.username != undefined) {
							createComments(com.username, com.content,
									com.likes, com.dislikes, com.commentid,
									com.groupid);
						}
					});
				});
			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}

function createComments(a, c, l, dl, cid, gid) {
	var ugid = getCookie('groupid');

	var space = document.getElementById("comments_space");
	tbl = document.createElement('table');
	tbl.style.width = '100%';
	// tbl.style.border = "1px solid red";

	// window.alert(a);
	var tr = tbl.insertRow();// inserta fila en tabla
	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila

	var ablock = document.createElement('A');
	var t = document.createTextNode(a + ' dice:');

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + a);

	tda.appendChild(ablock);
	tda.setAttribute('colSpan', '3');// modifica atributo de la celda

	if (ugid = gid) {
		var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
		tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');

		tdt.setAttribute('style', 'float:right');// modifica atributo de la
		// celda
		tdt.setAttribute('onclick', 'voteTroll(' + a + ')');
		tdt.onclick = function() {
			voteTroll(a);
		};

		if ((t = true) || (v != 'none')) {
			tdt.setAttribute('disabled', 'true');// modifica atributo
			tdt.appendChild(document.createTextNode('Ya has votado al Troll'));
		} else {
			tdt.appendChild(document.createTextNode('Vota al Troll'));
		}
	}
	// window.alert(c);

	var tr2 = tbl.insertRow();// inserta fila en tabla
	var tdc = tr2.insertCell();// crea una celda y la inserta en la fila
	tdc.appendChild(document.createTextNode(c));
	tdc.setAttribute('colSpan', '4');// modifica atributo de la celda

	var tr3 = tbl.insertRow();// inserta fila en tabla

	var tddlbtn = tr3.insertCell(0);// crea una celda y la inserta en la fila
	tddlbtn.setAttribute('class', 'btn btn-primary btn-danger btn-xs');
	tddlbtn.appendChild(document.createTextNode('NO me gusta +1'));

	tddlbtn.setAttribute('onclick', 'postDislike(' + cid + ')');// modifica
	tddlbtn.setAttribute('style', 'float:left');// modifica atributo de la celda

	var tddl = tr3.insertCell(1);// crea una celda y la inserta en la fila
	tddl.appendChild(document.createTextNode('No me gusta: ' + dl));// crea un

	var tdl = tr3.insertCell(2);// crea una celda y la inserta en la fila
	tdl.appendChild(document.createTextNode('Me gusta: ' + l));
	// tdl.setAttribute('style', 'float:right');//modifica atributo de la celda

	var tdlbtn = tr3.insertCell(3);// crea una celda y la inserta en la fila
	tdlbtn.setAttribute('class', 'btn btn-primary btn-success btn-xs');
	tdlbtn.appendChild(document.createTextNode('Me Gusta +1'));
	tdlbtn.setAttribute('onclick', 'postLike(' + cid + ')');
	tdlbtn.setAttribute('style', 'float:right');// modifica atributo de la celda

	space.appendChild(tbl);// añade la tabla al espacio
	space.appendChild(document.createElement('P'));

}

function createGroup(u) {
	var t = getCookie('troll');
	var v = getCookie('vote');

	var space = document.getElementById("group_space"), tbl = document
			.createElement('table');
	tbl.style.width = '100%';
	// tbl.style.border = "1px solid red";

	// window.alert(a);
	var tr = tbl.insertRow();// inserta fila en tabla
	var tda = tr.insertCell(0);// crea una celda y la inserta en la fila

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u);

	tda.appendChild(ablock);

	var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
	tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');

	tdt.setAttribute('style', 'float:right');
	tdt.setAttribute('onclick', 'voteTroll(' + u + ')');// modifica atributo
	tdt.onclick = function() {
		voteTroll(a);
	};
	
	if ((t = true) || (v != 'none')) {
		tdt.setAttribute('disabled', 'true');// modifica atributo
		tdt.appendChild(document.createTextNode('Ya has votado al Troll'));
	} else {
		tdt.appendChild(document.createTextNode('Vota al Troll'));
	}
	space.appendChild(tbl);// añade la tabla al espacio
	space.appendChild(document.createElement('P'));

}

function createRanking(u, p, rnk) {

	var space = document.getElementById("ranking_space");
	tbl = document.createElement('table');
	tbl.style.width = '100%';
	// tbl.style.border = "1px solid red";

	// window.alert(a);
	var tr = tbl.insertRow();// inserta fila en tabla

	var tdr = tr.insertCell(0);// crea una celda y la inserta en la fila
	tdr.appendChild(document.createTextNode(rnk));// crea un textnode y lo
	// añade a la celda
	tdr.setAttribute('style', 'width:31px');// modifica atributo de la celda

	var tda = tr.insertCell(1);// crea una celda y la inserta en la fila
	tda.setAttribute('style', 'float:left');// modifica atributo de la celda

	var ablock = document.createElement('A');
	var t = document.createTextNode(u);

	ablock.appendChild(t);
	ablock.setAttribute('href', WEB_URL + '/profile.html?username=' + u);

	tda.appendChild(ablock);// crea un textnode y lo añade a la celda

	var tdt = tr.insertCell(2);// crea una celda y la inserta en la fila

	tdt.appendChild(document.createTextNode(p));// crea un textnode y lo añade a
	// la celda
	tdt.setAttribute('style', 'float:right');// modifica atributo de la celda

	space.appendChild(tbl);// añade la tabla al espacio
	// space.appendChild(document.createTextNode('/'));
	space.appendChild(document.createElement('P'));

}

function postLike(cid) {
	var u = getCookie('username');
	var p = getCookie('password');

	var url = API_BASE_URL + '/comments/like/' + cid;
	var data = JSON.stringify("");

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.comment+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		window.alert("LIKE!");
		getComments();
	}).fail(function() {
		window.alert("FAIL PostLike");
	});
}

function postDislike(cid) {
	var u = getCookie('username');
	var p = getCookie('password');

	var url = API_BASE_URL + '/comments/dislike/' + cid;
	var data = JSON.stringify("");

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.comment+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		window.alert("DISLIKE!");
		getComments();
	}).fail(function() {
		window.alert("FAIL Post Dislike");
	});
}

function postComment() {

	c = $("#new_comment").val();
	if (c == '') {
		window.alert("Escribe algo a comentar!");
	} else {

		var comment = new Object();
		comment.content = c;
		var u = getCookie('username');
		var p = getCookie('password');
		var t = getCookie('troll');

		comment.creator = u;

		window.alert("troll? " + t);

		if (t == false) {
			window.alert("troll? " + t);
			comment.username = u;
		} else if (t == true) {
			comment.username = $("#troll_sign").val();
		}

		var url = API_BASE_URL + '/comments';
		var data = JSON.stringify(comment);

		window.alert("troll? " + data);
		$.ajax({
			headers : {
				'Authorization' : "Basic " + btoa(u + ':' + p)
			},
			url : url,
			type : 'POST',
			crossDomain : true,
			dataType : 'json',
			contentType : 'application/vnd.uTroll.api.comment+json',
			data : data,
		}).done(function(data, status, jqxhr) {
			window.alert("Post!");
			$("#new_comment").val("");

			getComments();
			// limpiar el new coments

		}).fail(function() {
			window.alert("FAIL Post Comment");
		});
	}
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		{
			if (c.indexOf(name) == 0)
				return c.substring(name.length, c.length);
			{
			}
		}
	}
	return "";
}