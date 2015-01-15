var API_BASE_URL = "http://147.83.7.156:8080/uTroll-api";

$("#btn_mod_enable").click(function(e) {
	e.preventDefault();
	var mod_profile=document.getElementById("mod_profile");
	var user_profile=document.getElementById("user_data");
	
	user_profile.style.visibility='hidden';			//oculto
	user_profile.style.display = 'none';				//no ocupa espacio
	
	mod_profile.style.visibility='visible';			//visible
	mod_profile.style.display = 'block';				//ocupa espacio
	
});

function getUserPass() {
	USERNAME = $("#user").val();
	PASSWORD = $("#password").val();
}

function getUserUrl(){
	 //devuelve el parametro username despues del ?username=
    var url= location.search.replace("?", "");
    //window.alert(url);
    var arrUrl = url.split("=");
    //window.alert(arrUrl[1]);

    return arrUrl[1];
}

function getProfile() {
	var u=getCookie('username');
	var p=getCookie('password');	

	var url = API_BASE_URL + '/users/byUsername/'+getUserUrl();
	var button_vote=document.getElementById("button_vote");
	
	$("#user_profile").text('');
	
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
				var uProf = data;

				$("#user_profile").text('');
				$('<h3> <strong> Name: </strong>' + uProf.name + '</h3>').appendTo($('#user_profile'));
				
				$('<h4><strong> Username: </strong> ' + uProf.username + '<br> </h4>').appendTo($('#user_profile'));
				
				$('<h4><strong> Email: </strong> ' + uProf.email + '<br> </h4>').appendTo($('#user_profile'));
				
				$('<strong> Points: </strong> ' + uProf.points + '<br>').appendTo($('#points'));
				$('<strong> Max Point: </strong> ' + uProf.points_max + '<br>').appendTo($('#points'));
				
				if (uProf.groupid==0) {
				$('<strong> No esta en ningun grupo! </strong><br>').appendTo($('#group'));
				
				
				}else if (uProf.groupid==getCookie('groupid')) {
					
					$('<strong> Esta en tu grupo! </strong><br>').appendTo($('#group'));
				}else {
				$('<strong> GroupID: </strong> ' + uProf.groupid + '<br>').appendTo($('#group'));
}
				

					
			}).fail(function() {
		$("#user_profile").text("FAIL: Creating Text Show!");
	});
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