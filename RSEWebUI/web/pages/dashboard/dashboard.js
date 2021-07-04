$(function (){ // onload
    window.setInterval(updateActiveUsers,2000);
    window.setInterval(updateActiveStocks,2000);

    $("#addingToBalance").submit(function (){
        $.ajax({
            type:'POST',
            data: $(this).serialize(),
            url:"/AddToUserBalance",
            success: function (res){
                let result = JSON.parse(res);
                //alert(result["massage"]);           // todo: for personal check. delete after
                $("#userBalance").text(result["addition"]);


            }
        })
        return false;
    })

    $("#addPublicOffering").submit(function(){
        $.ajax({
            type:'POST',
            data:$(this).serialize(),
            url:"/AddNewStockServlet",
            error:function (msg){
                alert("error on addPublicOffering"+msg);
            },
            success: function (msg){
                alert(msg);

            }


        })
        return false;
    })


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
            var table = $("#stocksBody");
            table.empty();


            for(let i=0; i<json.length;i++){            // todo: this looks like it's working - just need to see how the data return and add in to the append (while creating new td)
                var newRow = $(document.createElement('tr').ondblclick(stockSelected("row"+i)).id("row"+i));
                //var row = table.insertRow(-1); // will append after the last row
                newRow.insertCell(0).innerHTML(json[i].symbol);
                newRow.insertCell(1).innerHTML(json[i].companyName);
                newRow.insertCell(2).innerHTML(json[i].sharePrice);
                newRow.insertCell(3).innerHTML(json[i].transactionsTurnOver);
      /*          row.id("row"+i)
                row.ondblclick(stockSelected("row"+i));*/

                // $("#activeStocksList").appendChild($(document.createElement('tr'))
                //     .appendChild($(document.createElement('td')).text(json[i]["symbol"])));
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
        $.ajax({
            type:'POST',
            data:files,
            url:"/LoadXMLServlet",
            error:function (num,msg){
                alert(msg);
            },
            success:function (){
                updateActiveStocks();
            }
        })
    };
    input.click();


}

function stockSelected(rowId){
    /*var symbol = document.getElementById(rowId).getElementsByTagName(dt)[0].value();
    $(function ())*/

    let symbol = $(rowId).getElementsByTagName('dt')[0].value(); // todo: check if this works properly
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


