var API_BASE_URL = "http://localhost:8010/uTroll-api";

$("#login_btn").click(function(e) {
	e.preventDefault();
	setCookie('username',$("#username").val(),1)
	setCookie('password',$("#password").val(),1)

	postLogin();
});

function setCookieProfile() {
	
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
				document.cookie="name="+uProf.name;
				document.cookie="email="+uProf.email;
				document.cookie="points="+uProf.points;
				document.cookie="points_max="+uProf.points_max,
				document.cookie="troll="+uProf.troll;
				document.cookie="votedBy="+uProf.votedBy;
				document.cookie="vote="+uProf.vote;
				document.cookie="groupid="+uProf.groupid;
				window.location="/wall.html";
			}).fail(function() {
					window.alert("ERROR: SetCookieProfile");
	});
}

function postLogin() {
	
	var user=new Object();
	var username = getCookie('username');
	var password = getCookie('password');
	
	var url = API_BASE_URL + '/users/login';
	
	user.username=username;
	user.password=password;
		
	var data = JSON.stringify(user);
	
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.user+json',
		data : data,
		dataType:'json',
//		success: function(data){window.alert(data);
  //  	$('#load').fadeOut();
  	
	}).done(function(result, status, jqxhr) {
		var uProf = result;		
		if (uProf.loginSuccessful) {		
			setCookieProfile();	
		}else if (!uProf.loginSuccessful) {
			window.alert("Revisa los usuario y contraseña!");}
		}).fail(function() {
			window.alert("ERROR: LOGIN CHECK");
	});
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
} 

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);{
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);{}}
    }
    return "";
} 
