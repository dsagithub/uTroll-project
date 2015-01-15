var API_BASE_URL = "http://147.83.7.156:8080/uTroll-api";
var WEB_URL = "http://localhost/" // server ip

function getTrollMode() {
	if (getCookie('troll') == 'true') {
		$("#imagen").attr("src", "/img/utroll.png");
		$("#imagen").attr("height", 150);
		// $('<strong> Eres el Troll del grupo!
		// </strong>').appendTo($('#group_state'));
	}
}

function getGroupDetails() {
	var u = getCookie('username');
	var p = getCookie('password');
	var gid = getCookie('groupid');

	if (gid == 0) {
		$("#group_details").text("No estas en ningun grupo!");
		$('<strong> Tienes: ' + getCookie('points') + '</strong> puntos<br>')
				.appendTo($('#group_state'));

		var btn = document.getElementById("create_btn");

		btn.style.visibility = 'visible'; // visible
		btn.style.display = 'block'; // ocupa espacio

		window.alert("Sin grupo " + gid);

	} else if (gid != 0) {
		var url_group = API_BASE_URL + '/groups/' + gid;

		$
				.ajax({
					headers : {
						'Authorization' : "Basic " + btoa(u + ':' + p)
					},
					url : url_group,
					type : 'GET',
					crossDomain : true,
					dataType : 'json',
				})
				.done(
						function(data, status, jqxhr) {
							var group = data;
							getTrollMode();
							$("#group_details").text('');
							$(
									'<strong> GroupID: </strong> '
											+ group.groupid + '<br>').appendTo(
									$('#group_details'));

							$(
									'<h4><strong> Creado por  ' + group.creator
											+ '</strong><br> </h4>').appendTo(
									$('#group_details'));

							$(
									'<h4><strong> Estado: </strong> '
											+ group.state + '<br> </h4>')
									.appendTo($('#group_details'));

							$('<strong><h1>' + group.price + '</h1></strong>')
									.appendTo($('#group_details2'));
							$("#group_details2").attr("style",
									" text-align:center;font-size:50");
							$('#group_title').text(group.groupname);

							$(
									'<strong> Tienes: ' + getCookie('points')
											+ '</strong> puntos<br>').appendTo(
									$('#group_state'));
							if (getCookie('vote') == 'none') {
								$(
										'<strong> Aun no has votado al troll!</strong><br> </h4>')
										.appendTo($('#group_state'));
							} else {
								$(
										'<strong> Has votado a '
												+ getCookie('vote')
												+ ' como troll!</strong><br> </h4>')
										.appendTo($('#group_state'));
							}
							if (getCookie('votedBy') == '0') {
								$(
										'<strong> Nadie cree que seas el troll!</strong><br> </h4>')
										.appendTo($('#group_state'));
							} else {
								$(
										'<strong>'
												+ getCookie('votedBy')
												+ ' personas creen que eres el troll!</strong><br> </h4>')
										.appendTo($('#group_state'));
							}
						}).fail(function() {
					window.alert("fail " + gid);
				});
	} else if (gid == -1) {
		window.alert("ERROR 2: Gid no identificado");
	}
}

function getGroup() {
	var u = getCookie('username');
	var p = getCookie('password');
	var gid = getCookie('groupid');

	if (gid == 0) {
		$("#group_title").text("No estas en ningun grupo!");
		window.alert("Sin grupo " + gid);
		/*
		 * var btn = document.getElementById("group_btn_space");
		 * 
		 * btn.style.visibility = 'visible'; // visible btn.style.display =
		 * 'block'; // ocupa espacio
		 */

		getGroupList();

	} else if (gid != 0) {
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
							createGroup(us.username);
						}

					}

				});
			});
		}).fail(function() {
			window.alert("fail " + gid);
		});
	} else if (gid == -1) {
		window.alert("ERROR 2: Gid no identificado");
	}
}

function createGroup(u) {

	var space = document.getElementById("group_profile");
	tbl = document.createElement('table');
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

	if ((getCookie('vote') == 'none') && (getCookie('troll') == 'false')) {
		var tdt = tr.insertCell(1);// crea una celda y la inserta en la fila
		tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');
		tdt.appendChild(document.createTextNode('Vota al Troll'));
		tdt.setAttribute('style', 'float:right');
		tdt.setAttribute('onclick', 'voteTroll(' + u + ')');// modifica atributo
		tdt.onclick = function() {
			voteTroll(u);
		};
	}

	space.appendChild(tbl);// añade la tabla al espacio
	space.appendChild(document.createElement('P'));

}

function getGroupList() {
	window.alert("group list 1");

	var u = getCookie('username');
	var p = getCookie('password');

	var url = API_BASE_URL + '/groups';

	window.alert("grouplist " + url);

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.group.collection+json',
	}).done(
			function(data, status, jqxhr) {
				var groups = data;
				$.each(groups, function(i, v) {
					var group = v;
					$.each(group, function(i, v) {
						var gr = v;
						if (gr.groupname != undefined) {
							if (gr.groupid != 0) {
								createGroupList(gr.groupname, gr.groupid,
										gr.price, gr.state);
							}
						}

					});
				});
			}).fail(function() {
		window.alert("group list fail" + gid);
	});

}

function createGroupList(n, gid, p, s) {

	var space = document.getElementById("group_profile");
	tbl = document.createElement('table');
	tbl.style.width = '100%';
	// tbl.style.border = "1px solid red";

	// window.alert(a);
	var tr = tbl.insertRow();
	var tdn = tr.insertCell(0);// crea una celda y la inserta en la fila
	tdn.appendChild(document.createTextNode(n));
	var tdp = tr.insertCell(1);// crea una celda y la inserta en la fila
	tdp.appendChild(document.createTextNode(p));
	if (s = 'open') {

		var tdt = tr.insertCell(2);// crea una celda y la inserta en la fila
		tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');
		tdt.appendChild(document.createTextNode('Entrar'));
		tdt.setAttribute('style', 'float:right');
		tdt.setAttribute('onclick', 'joinGroup(' + gid + ')');
		tdt.onclick = function() {
			joinGroup(gid);
		};
	}

	space.appendChild(tbl);// añade la tabla al espacio
	space.appendChild(document.createElement('P'));

}

function joinGroup(gid) {
	var u = getCookie('username');
	var p = getCookie('password');
	var gidi=getCookie('groupid');
	window.alert("HOLA"+u+p);

	var url = API_BASE_URL + '/users/joingroup/' + gid;
	window.alert(url);
	var data = JSON.stringify("");
	window.alert(url);

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.user+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		window.alert("FUNCIONA");
//		reloadCookies();
//		getGroup();
//		window.location.reload();
	}).fail(function() {
		window.alert("FAIL join Group");
	});
//	window.location.reload();
}

function createGroupAPI() {
	var u = getCookie('username');
	var p = getCookie('password');

	var url = API_BASE_URL + '/groups';
	var group = new Object();
	var month = $("#month").val();
	var day = $("#day").val();
	var date = "2015-" + month + "-" + day + " 12:00:00";
	var month1 = $("#month1").val();
	var day1 = $("#day1").val();
	var date1 = "2015-" + month1 + "-" + day1 + " 12:00:00";

	group.groupname = $("#name").val();
	group.price = $("#price").val();
	group.endingTimestamp = date;
	group.closingTimestamp = date1;
	
	var data = JSON.stringify(group);

	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.group+json',
		data : data,
	}).done(function(data, status, jqxhr) {
		reloadCookies();
		getGroup();
		window.location.reload();
	}).fail(function() {
		window.alert("FAIL join Group");
	});
	window.location.reload();
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