   
function LogOut(){
	window.alert('Hasta la proxima!');
	setCookie('username', '', -1);
	setCookie('name', '', -1);
	setCookie('password', '', -1);
	setCookie('email', '', -1);
	setCookie('groupid', '', -1);
	setCookie('troll', '', -1);	
	setCookie('votedBy', '', -1);
	setCookie('vote', '', -1);
	setCookie('points', '', -1);	
	setCookie('points_max', '', -1);

	window.location="/index.html";
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

function reloadCookies() {
	
	var u = getCookie('username');
	var p = getCookie('password');
	
	var url = API_BASE_URL + '/users/byUsername/'+ u;
	
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(u + ':' + p)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		contentType: 'application/vnd.uTroll.api.user+json',
	}).done(
			function(data, status, jqxhr) {
				var uProf = data;
				//Cambiar por el uso de setCookie
				setCookie("name",uProf.name,1);
				setCookie("email",uProf.email,1);
				setCookie("points",uProf.points,1);
				setCookie("points_max",uProf.points_max,1);
				setCookie("troll",uProf.troll,1);
				setCookie("votedBy",uProf.votedBy,1);
				setCookie("vote",uProf.vote,1);
				setCookie("groupid",uProf.groupid,1);
			}).fail(function() {
					window.alert("ERROR: Cookies Error");
	});
}