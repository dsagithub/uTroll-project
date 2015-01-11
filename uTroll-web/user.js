var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "david";
var PASSWORD = "david";
var REPO_NAME = "";

// $.ajaxSetup({
// headers : {
// 'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
// }
// });

$("#btn_mod_enable").click(function(e) {
	e.preventDefault();
	var mod_profile=document.getElementById("mod_profile");
	var user_profile=document.getElementById("user_data");
	
	user_profile.style.visibility='hidden';			//oculto
	user_profile.style.display = 'none';				//no ocupa espacio
	
	mod_profile.style.visibility='visible';			//visible
	mod_profile.style.display = 'block';				//ocupa espacio
	//getOldProfile();	
	
});

function getUserPass() {
	USERNAME = $("#user").val();
	PASSWORD = $("#password").val();
}

function getProfile() {
	//getUserPass();
	//REPO_NAME = $("#repository_name").val();

	var url = API_BASE_URL + '/users/byUsername/david';
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

				$("#user_profile").text('');
				$('<h3> <strong> Name: </strong>' + uProf.name + '</h3>').appendTo($('#user_profile'));
				//$('<p>').appendTo($('#user_profile'));	
				
				$('<h4><strong> Username: </strong> ' + uProf.username + '<br> </h4>').appendTo($('#user_profile'));
				
				$('<h4><strong> Email: </strong> ' + uProf.email + '<br> </h4>').appendTo($('#user_profile'));
				
				$('<strong> Points: </strong> ' + uProf.points + '<br>').appendTo($('#points'));
				$('<strong> Max Point: </strong> ' + uProf.points_max + '<br>').appendTo($('#points'));
				
				$('<strong> GroupID: </strong> ' + uProf.groupid + '<br>').appendTo($('#group'));
				if(uProf.troll){
				$('<strong> Eres el Troll! </strong>').appendTo($('#group'));
					button_vote.style.visibility='hidden';			//oculto
					button_vote.style.display = 'none';				//no ocupa espacio
				}
				else {
					$('<strong> No eres el Troll del grupo! </strong>').appendTo($('#group'));
					}
				$("#imagen").attr("src", repo.owner.avatar_url);
					//$("#imagen").attr("height", 100);
					
			}).fail(function() {
		$("#user_profile").text("Este repositorio no existe");
	});
	//getFriends();

}

function getFriends() {
	//getUserPass();
	var url = API_BASE_URL + '/friends/';
	$("#friends").text('');

	$.ajax({
		//headers : {
		//	'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		//},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
			function(data, status, jqxhr) {
				var repos = data;

				$.each(users, function(i, v) {
					var repo = v;
					
					$('#friends').append('<li class="list-group-item">'+repo.name+'</li>');	

				});

			}).fail(function() {
		$('<li class="list-group-item"></li>').text("No hay repositorios.");
	});
}