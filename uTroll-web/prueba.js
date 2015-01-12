var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "";
var PASSWORD = "";

$("#button_create_user").click(function(e) {
	e.preventDefault();
	var user = new Object();
	var password_again = "NaN";
	user.username = $("#username").val();
	user.name = $("#name").val();
	user.password = $("#password").val();
	password_again = $("#password_again").val();
	user.email = $("#email").val();
	user.age = parseInt($("#age").val());
	
		
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

	window.alert(data);
	
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