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
        let input = document.createElement('input');
        input.type = 'file';
        input.onchange = _ => {
            // you can use this method to get file and perform respective operations
            let files = Array.from(input.files);
            if (files.length > 1) {
                document.getElementById('file-upload-status').style.display = 'block';
                document.getElementById('file-upload-status').innerHTML = 'Can\'t load more than one file';
                document.getElementById('file-upload-status').style.color = 'red';
                setTimeout(function () {
                    document.getElementById('file-upload-status').style.display = 'none';
                }, 30 * 1000);
            }
            console.log(files);
            $.ajax({
                    type: 'POST',
                    data: {'xmlFile': files[0]},
                },
            )
        };
        input.click();
    }
}

function stockSelected(rowId) {
    let row = document.getElementById(rowId);
    let cells = row.getElementsByTagName('td')
    let symbol = cells[0].innerText;
    $.ajax({
        type: 'GET',
        data: {'symbol': symbol},
        url: "/showStock",
        error: function (xhr, httpErrorMessage, customErrorMessage) {
        }
    });
}


