var API_BASE_URL = "http://localhost:8010/uTroll-api";
var USERNAME = "david";
var PASSWORD = "david";
function getRanking() {
	// getUserPass();
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
				// alert(valueOf(data));
				$.each(users, function(i, v) {
					var user = v;
					$.each(user, function(i, v) {
						var us = v;
						window.alert(us);

						$('<h3> Username: ' + '<strong>'
										+ us.username + '</strong>'
										+ '</h3>').appendTo(
								$('#ranking_space'));
						$('<p>').appendTo($('#ranking_space'));
						$(
								'<strong> Points: </strong> '
										+ us.points + '<br>').appendTo(
								$('#ranking_space'));
						$('</p>').appendTo($('#ranking_space'));
						
//						$(us.username).appendTo($('#ranking_space'));
//						$(' <strong>' + us.points + '</strong>').appendTo(
//								$('#ranking_space'));
					});
				});
			}).fail(function() {
		$("#ranking_space").text("No hay repositorios.");
	});
}
function getComments() {
	// window.alert("HOLA");
	// getUserPass();
	// window.alert("HOLA2");
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
				$.each(comments, function(i, v) {
					var comment = v;
					$.each(comment, function(i, v) {
						var com = v;

						$(
								'<h3> Username: ' + '<strong>'
										+ com.username + '</strong>'
										+ '</h3>').appendTo(
								$('#comments_space'));
						$('<p>').appendTo($('#comments_space'));
						$(
								'<strong> Comment: </strong> '
										+ com.content + '<br>').appendTo(
								$('#comments_space'));
						// $('<strong> Links: </strong> ' +
						// getRequestHeader("next") +
						// '<br>').appendTo($('#comments_space'));
						$('</p>').appendTo($('#comments_space'));
					});
				});
			}).fail(function() {
		$("#comments_space").text("No hay comments.");
	});
}