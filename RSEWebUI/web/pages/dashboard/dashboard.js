$(function (){ // onload
    window.setInterval(updateActiveUsers,2000);
    window.setInterval(updateActiveStocks,2000);


})

function updateActiveUsers(){
    $.ajax({
        type:'GET',
        url:"/activeUsers",
        dataType:'json',
        timeout:2000,
        success: function (jsonStr){


            $("#activeUsersList").empty();
            var json = jsonStr;

            for(let i=0; i<json.length;i++){
                $(document.createElement("li"))
                    .text(json[i]["userName"] + " | " + (json[i]["isAdmin"]?"Admin":"Trader"))
                    .appendTo("#activeUsersList");
            }

        }
    })
}

function updateActiveStocks(){
    $.ajax({
        type:'GET',
        url:"/ShowAllStocksServlet",
        dataType:'json',
        timeout:2000,
        success: function (jsonStr){


            //$("#activeUsersList").empty(); // I don't want to add all the stocks every time. O just want to add the new ones like in the chat
            var json = jsonStr;

            for(let i=0; i<json.length;i++){            // todo: this looks like it's working - just need to see how the data return and add in to the append (while creating new td)
                $("#activeStocksList").appendChild($(document.createElement('tr'))
                    .appendChild($(document.createElement('td')).text(json[i]["symbol"])));
             /*   $(document.createElement("li"))
                    .text(json[i]["userName"] + " | " + (json[i]["isAdmin"]?"Admin":"Trader"))
                    .appendTo("#activeStocksList");*/
            }

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
