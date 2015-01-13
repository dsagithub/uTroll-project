var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "";
var PASSWORD = "";


$("#button_create_user").click(function(e) {
	e.preventDefault();
	window.alert("HOLA");
	
	var user = new Object();
	user.username = $("#username").val();
	user.password = $("#password").val();
	user.password_again = $("#password_again").val();
	user.name = $("#name").val();
	user.email = $("#email").val();
	user.age = $("#age").val();
	
	window.alert(user.password);
	window.alert(user.password_again);
	if (user.password == user.password_again)
		postUser(user);
	else {
		function() {
		$('<div class="alert alert-danger"> <strong>ERROR</strong> Los campos de contraseña no coinciden </div>').appendTo($("#user_result"));
	}
	}
});

function postUser(user) {
	window.alert("He entrado en el post");
	
	var url = API_BASE_URL + '/users';
	var data = JSON.stringify(user);
	window.alert(url);
	
	//$("#user_result").text('');

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
						$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Created</div>')
								.appendTo($("#repos_result"));
					})
			.fail(
					function() {
						$(
								'<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>')
								.appendTo($("#repos_result"));
					});
}