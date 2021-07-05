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
                var newRow = document.createElement('tr');//.ondblclick("stockSelected(row"+i+")").id("row"+i));
                var cell1 = document.createElement("td");
                cell1.innerText = (json[i].symbol);
                newRow.appendChild(cell1);
                var cell2 = document.createElement("td");
                cell2.innerText = (json[i].companyName);
                newRow.appendChild(cell2);
                var cell3 = document.createElement("td");
                cell3.innerText = (json[i].sharePrice);
                newRow.appendChild(cell3);
                var cell4 = document.createElement("td");
                cell4.innerText = (json[i].transactionsTurnOver);
                newRow.appendChild(cell4);
                newRow.id = "row"+i;
                newRow.ondblclick = function (i){stockSelected(i)};


                document.getElementById("stocksBody").appendChild(newRow);

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

function stockSelected(event) {
    var rowId = event.which.id;
    let row = document.getElementById(rowId);
    let cells = row.childNodes;
    let symbol = cells[0].innerText;
    $.ajax({
        type: 'GET',
        data: {'symbol': symbol},
        url: "/showStock",
        error: function (xhr, httpErrorMessage, customErrorMessage) {
        }
    });
}


