var author="hola";
var comment="El principal problema del document.write es que insertamos el código HTML en línea directamente en el momento que";
var spacer="<p></p>";
   
function createComments(){
	
	for (a=0;a<1;a++) {
    	var space = document.getElementById("comments_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

        var tr = tbl.insertRow();//inserta fila en tabla
        var td1 = tr.insertCell(0);//crea una celda y la inserta en la fila
        td1.appendChild(document.createTextNode('author'));//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda

        
        var tr2 = tbl.insertRow();//inserta fila en tabla
        var td3 = tr2.insertCell();//crea una celda y la inserta en la fila
        td3.appendChild(document.createTextNode('comentario de prueba'));//crea un textnode y lo añade a la celda 
        td3.setAttribute('colSpan', '2');//modifica atributo de la celda
        //td3.style.border = "1px solid black";
        
        var tr3 = tbl.insertRow();//inserta fila en tabla
        
        var tdlikes = tr3.insertCell(0);//crea una celda y la inserta en la fila
        tdlikes.appendChild(document.createTextNode('likes/dislikes'));//crea un textnode y lo añade a la celda 
        

        
        var td22 = tr3.insertCell(1);//crea una celda y la inserta en la fila
        td22.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
        td22.appendChild(document.createTextNode('Me Gusta'));//crea un textnode y lo añade a la celda
        td22.setAttribute('style', 'float:right');//modifica atributo de la celda
    	  
    	  var td2 = tr3.insertCell(2);//crea una celda y la inserta en la fila
        td2.setAttribute('class', 'btn btn-primary btn-danger btn-xs');//modifica atributo de la celda
        td2.appendChild(document.createTextNode('NO me gusta'));//crea un textnode y lo añade a la celda
        td2.setAttribute('style', 'float:right');//modifica atributo de la celda
    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('spacer'));
    
 }
}
function createFriends(){
	
	for (a=0;a<10;a++) {
    	var space = document.getElementById("friends_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

        var tr = tbl.insertRow();//inserta fila en tabla
        var td1 = tr.insertCell(0);//crea una celda y la inserta en la fila
        td1.appendChild(document.createTextNode('Friend'));//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-warning btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Vota al Troll'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda

    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('spacer'));
    
 }
}

function createPendingFriends(){
	
	for (a=0;a<3;a++) {
    	var space = document.getElementById("pending_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

        var tr = tbl.insertRow();//inserta fila en tabla
        var td1 = tr.insertCell(0);//crea una celda y la inserta en la fila
        td1.appendChild(document.createTextNode('Friend'));//crea un textnode y lo añade a la celda 
        
        var tdt = tr.insertCell(1);//crea una celda y la inserta en la fila
        tdt.setAttribute('class', 'btn btn-primary btn-success btn-xs');//modifica atributo de la celda
        tdt.appendChild(document.createTextNode('Acepta'));//crea un textnode y lo añade a la celda
        tdt.setAttribute('style', 'float:right');//modifica atributo de la celda

    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('spacer'));
    
 }
}

function createSent(){
	
	for (a=0;a<4;a++) {
    	var space = document.getElementById("sent_space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

        var tr = tbl.insertRow();//inserta fila en tabla
        var td1 = tr.insertCell(0);//crea una celda y la inserta en la fila
        td1.appendChild(document.createTextNode('Friend'));//crea un textnode y lo añade a la celda 
    
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode('spacer'));
    
 }
}

function createTable(){
	
	for (a=0;a<10;a++) {
    	var space = document.getElementById("space"),
      tbl  = document.createElement('table');
      tbl.style.width  = '100%';
      tbl.style.border = "1px solid red";
	

        var tr = tbl.insertRow();//inserta fila en tabla
        var td1 = tr.insertCell(0);//crea una celda y la inserta en la fila
        td1.appendChild(document.createTextNode('author'));//crea un textnode y lo añade a la celda 
        
        var td2 = tr.insertCell(1);//crea una celda y la inserta en la fila
        td2.setAttribute('class', 'btn btn-primary btn-danger btn-xs');//modifica atributo de la celda
        td2.appendChild(document.createTextNode('NO me gusta'));//crea un textnode y lo añade a la celda
        td2.setAttribute('style', 'float:right');//modifica atributo de la celda
        
        var td22 = tr.insertCell(2);//crea una celda y la inserta en la fila
        td22.setAttribute('class', 'btn btn-primary btn-succes btn-xs');//modifica atributo de la celda
        td22.appendChild(document.createTextNode('Me Gusta'));//crea un textnode y lo añade a la celda
        td22.setAttribute('style', 'float:right');//modifica atributo de la celda
        
        var tr2 = tbl.insertRow();//inserta fila en tabla
        var td3 = tr2.insertCell();//crea una celda y la inserta en la fila
        td3.appendChild(document.createTextNode('commmm mmmmmmm mmmmmmmmmmm mmmmmm mmmm mmmm mm'));//crea un textnode y lo añade a la celda 
        td3.setAttribute('colSpan', '2');//modifica atributo de la celda
        //td3.style.border = "1px solid black";
    	  
    space.appendChild(tbl);//añade la tabla al espacio
    space.appendChild(document.createTextNode(spacer));
    
 }
}


function createRow(){
	for (a=0;a<10;a++) {
    var space = document.getElementById("space"),
    tbl  = document.createElement('table');
    //tbl.style.width  = '100px';
    //tbl.style.border = "1px solid black";
	
    for(var i = 0; i < 3; i++){
        var tr = tbl.insertRow();//inserta fila en tabla
        
        for(var j = 0; j < 2; j++){
            if(i == 2 && j == 1){
                break;
            } else {
                var td = tr.insertCell();//crea una celda y la inserta en la fila
                td.appendChild(document.createTextNode('Cell'));//crea un textnode y lo añade a la celda 
                td.style.border = "1px solid black";
                if(i == 1 && j == 1){
                    td.setAttribute('rowSpan', '2');//modifica atributo de la celda
                }
            }
        }
    }
    space.appendChild(tbl);//añade la tabla al espacio
    //space.appendChild("<p></p>");//añade la tabla al espacio
 }
}

