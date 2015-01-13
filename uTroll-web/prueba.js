var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "";
var PASSWORD = "";

$("button_create_user").click(function(){
  $.post(url+'/users',function(data,status){
    alert("Data: " + data + "\nStatus: " + status);
  });
}); 

$("#button_create_user0").click(function(e) {
	e.preventDefault();
	var user = new Object();
	var password_again="*";
	user.username = $("#username").val();
	user.password = $("#password").val();
	password_again = $("#password_again").val();
	user.name = $("#name").val();
	user.email = $("#email").val();
	user.age = parseInt($("#age").val(),10);
	if (user.password == password_again){
			window.alert("coinciden");
			//postUser2(user);
			
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
						data : data
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

function postUser2(user) {
	//getUserPass();
	var url = API_BASE_URL + '/users';
	var data = JSON.stringify(user);
	//$("#repos_result").text('');
	window.alert(url);
	window.alert(data);
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
				window.alert("Ok! Usuario Creado");
			})
			.fail(function() {
				window.alert("Oh! Usuario NO creado");
			});
}