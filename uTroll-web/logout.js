   
function LogOut(){
	window.alert('Hasta la proxima!');
	setCookie('username', '', -1);
	setCookie('name', '', -1);
	setCookie('password', '', -1);
	setCookie('email', '', -1);
	setCookie('groupid', '', -1);
	setCookie('troll', '', -1);	
	setCookie('votedBy', '', -1);
	setCookie('points', '', -1);	
	setCookie('points_max', '', -1);

	window.location="/wellcome.html";
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}