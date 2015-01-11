var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "david";
var PASSWORD = "david";


function getRanking() {
	//getUserPass();
	var url = API_BASE_URL + '/users/ranking';
	$("#ranking_space").text('');

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
				var users = data;
				//alert(valueOf(data));
				$.each(users, function(i, v) {
					var user = v;
					alert(user);
					//$(user.links).appedTo($('#ranking_space'));
					$(user.username).appendTo($('#ranking_space'));
					$(' <strong>' + user.points + '</strong>').appendTo($('#ranking_space'));

				});

			}).fail(function() {
		$("#ranking_space").text("No hay repositorios.");
	});
}

function getComments() {
	getUserPass();
	var url = API_BASE_URL + '/comments';
	$("#comments_space").text('');

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
				var comments = data;

				$.each(comment, function(i, v) {
					var comment = v;
					
					$('<h3> Username: ' + '<strong>'+ comment.username + '</strong>'+'</h3>').appendTo($('#comments_space'));
					$('<p>').appendTo($('#comments_space'));	
					$('<strong> Comment: </strong> ' + comment.content + '<br>').appendTo($('#comments_space'));
				//	$('<strong> Links: </strong> ' + getRequestHeader("next") + '<br>').appendTo($('#comments_space'));		
					$('</p>').appendTo($('#comments_space'));
				});

			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}