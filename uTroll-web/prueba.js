var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "";
var PASSWORD = "";

$("#button_create_user").click(function(e) {
	e.preventDefault();
	var user = new Object();
	user.username = $("#username").val();
	user.password = $("#password").val();
	user.password_again = $("#password_again").val();
	user.name = $("#name").val();
	user.email = $("#email").val();
	user.age = $("#age").val();
	
	window.alert(user.password);
	window.alert(user.password_again);
	if ((user.password == user.password_again)!=null){
			window.alert("coinciden");
			postUser(user);
			
			}
			
	else {
		window.alert("ERROR: Los campos de contraseña no coinciden");
	}
});

function postUser(user) {
	window.alert("He entrado en el post");
	
	var url = API_BASE_URL + '/users';
	var data = JSON.stringify(user);
	window.alert(url);
	window.alert(user.username);
	$.ajax(
					{
						url : url,
						type : 'POST',
						crossDomain : true,
						dataType : 'json',
						data : data,
					})
			.done(
					function(data, status, jqxhr) {
						window.alert("Ok! Usuario Creado");

					})
			.fail(
					function() {
						
						window.alert("Oh! Usuario NO creado");
					});
}