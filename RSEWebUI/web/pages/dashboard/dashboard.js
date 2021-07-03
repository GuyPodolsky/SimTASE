$(function (){ // onload
    window.setInterval(updateActiveUsers,2000)
})

function updateActiveUsers(){
    $.ajax({
        type:'GET',
        url:"/activeUsers",
        dataType:'json',
        //timeout:2000,
        success: function (jsonStr){

            var users = jsonStr.items.map(function (user) {
               return user.key + ": " + user.value;
            });

            $("#activeUsersList").empty();

            for(let i=0;i<users.length;i++){
                let content = '<li>' + users.join('</li><li>') + ''</li>';
                let list = $('<ul/>').html(content);
                ("#activeUsersList").append(list);
            }
            //var json = $.praseJSON(jsonStr)
           /* let parsedJson = JSON.parse(jsonStr);
            parsedJson.forEach((user) => {
                $("#activeUsersList").innerHTML += "<li>"+ user.userName+"|"+ user.isAdmin +"</li>";
            });*/
           /*let users = $("#activeUsersSection").getElementsByTagName("ul");
              users.empty();
              for(let i=0; i<json.length;i++){
                let li = $.createElement("li");
                li.innerText = json[i].getAttribute("username") + " | " + json[i].getAttribute("is_admin"); /!* innerHTML \ textContent *!/
                users.insertAfter(li);
            }*/
        }
    })
}

function loadXMLFile() {
    let input = document.createElement('input');
    input.type = 'file';
    input.onchange = _ => {
        // you can use this method to get file and perform respective operations
        let files =   Array.from(input.files);
        console.log(files);
        // todo: add here the call to the loading xml servlet
    };
    input.click();

}

function stockSelected(r,rowId){
    /*var symbol = document.getElementById(rowId).getElementsByTagName(dt)[0].value();
    $(function ())*/

    let symbol = this.getElementsByTagName('dt')[0].value(); // todo: check if this works properly
    $.ajax({
        type:'GET',
        data:{'symbol':symbol},
        url: "enter here",                                  // todo: replace the url to the real servlet
        success: function(result){
            //var jsonStr = JSONArray.toJSONString(result);   // <-- NOT GOOD :: result is a string the represent a json object
            // var json = eval(result);
            // var json = $.praseJSON(result);
            var json = JSON.parse(result);          // <-- like in Aviad's powerpoint
            // todo: here we need to take all the data from the returned json object
            // and insert it to the right html

            window.location.reload("");         // todo: update the html to the third page

        }});



}
