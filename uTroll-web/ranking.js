var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "david";
var PASSWORD = "david";

function getProfile() {
	//getUserPass();
	//REPO_NAME = $("#repository_name").val();

	var url = API_BASE_URL + '/users/ranking';
	var button_vote=document.getElementById("button_vote");
	
	$("#user_profile").text('');
	
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var uProf = data;
				var usrname=uProf[0].users;
				$("#user_profile").text('');
				$('<h3> <strong> Name: </strong>' + usrname + '</h3>').appendTo($('#user_profile'));
				//$('<p>').appendTo($('#user_profile'));	
				
				$('<h4><strong> Username: </strong> ' + uProf.username + '<br> </h4>').appendTo($('#user_profile'));
				
				$('<h4><strong> Email: </strong> ' + uProf.email + '<br> </h4>').appendTo($('#user_profile'));
				

					
			}).fail(function() {
		$("#user_profile").text("Este repositorio no existe");
	});
	//getFriends();

}
