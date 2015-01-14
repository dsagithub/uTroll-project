var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "";
var PASSWORD_LENGTH = 2;


$("#button_create_user").click(function(e) {
	e.preventDefault();
	var password_again;
	var user = new Object();
	expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	user.username = $("#username").val();
	user.password = $("#password").val();
	password_again = $("#password_again").val();
	user.name = $("#name").val();
	user.email = $("#email").val();
	user.age = $("#age").val();
	
	if (user.password=="") {
		window.alert("El password no puede quedar vacio");
	}else if (user.password != password_again) {
		window.alert("El password password no coincide: "+user.password+" Password 2 : "+password_again);
	}else if ( password_again.length<PASSWORD_LENGTH ){
   	window.alert("La contraseña debe ser de almenos "+PASSWORD_LENGTH+" caracteres");
	}else if ( !expr.test(user.email) ){
      window.alert("Introduce una dirección de correo valida.");
   }else	if ((user.password == password_again) && (user.password!="") && (expr.test(user.email)) && (password_again.length>PASSWORD_LENGTH)){
		postUser(user);
	}else {
		function fail() {
		   window.alert("Error pre Post");
	}
	}
});

function validarEmail(email) {
    expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	 if ( !expr.test(email) ){
        window.alert("Introduce una dirección de correo valida.");}
}

function postUser(user) {
	var url = API_BASE_URL + '/users';
	var data = JSON.stringify(user);

	window.alert(data);
	window.alert(url);
	
	$.ajax({
//		headers : {
//			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
//		},
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.uTroll.api.user+json',
		data : data,
	}).done(function(data, status, jqxhr) {
			window.alert("Usuario creado correctamente, ahora encuentra a tus amigos");
			//guardar username y pwd en cookie antes de redireccionar
			window.location="/friend.html";
	}).fail(function() {
		window.alert("FAIL");
	});
}
