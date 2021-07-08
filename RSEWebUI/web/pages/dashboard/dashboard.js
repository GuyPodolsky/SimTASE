

$(function (){ // onload
    window.setTimeout(updateActiveUsers,1);
    window.setTimeout(updateActiveStocks,1);
    window.setTimeout(updateUserDetails,1);
    window.setInterval(updateActiveUsers,2000);
    window.setInterval(updateActiveStocks,2000);
    window.setInterval(updateUserDetails,2000);

    $("#addingToBalance").submit(function (){
        $.ajax({
            type:'POST',
            data:{
                op:"add",
                amount:document.getElementById("add-amount").value},
            url:'/UpdateUserDetails',
            error:function (){
                alert("PROBLEMMMMM");
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

    $("#transferMoney").submit(function (){
        $.ajax({
            type:'POST',
            data:{
                op:"transfer",
                to:document.getElementById("user-select").value,
                amount:document.getElementById("trans-amount").value},
            url:'/UpdateUserDetails',
            error:function (){
                alert("PROBLEMMMMM");
            }
        })
        return false;
    })
})

function updateActiveUsers(){ //TODO: make the refresh less annoying.
                              //TODO: self-transfer.
    $.ajax({
        type:'GET',
        url:"/activeUsers",
        dataType:'json',
        timeout:2000,
        error:function (msg){
          console.error(msg);
        },
        success: function (jsonStr){
            $("#activeUsersList").empty();
            $("#user-select").empty();
            let json = jsonStr;
            let sel = document.getElementById("user-select");
            let val = sel.value;
            for(let i=0; i<json.length;i++){
                $(document.createElement("li"))
                    .text(json[i]["userName"] + " | " + (json[i]["isAdmin"]?"Admin":"Trader"))
                    .appendTo("#activeUsersList");
                if(!json[i]["isAdmin"]) {
                    let id = json[i]["userName"] + "Sel";
                    let op = document.createElement('option');
                    op.id = id;
                    op.text = json[i]["userName"];
                    sel.appendChild(op);
                }
            }
            sel.selectedIndex = val;
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

            var json = jsonStr;
            var table = $("#stocksBody");
            table.empty();

            for(let i=0; i<json.length;i++){
                var newRow = document.createElement('tr');
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
    //var rowId = event.relatedTarget.id;
    //let row = document.getElementById(rowId);
    let row = event.currentTarget;
    let cells = row.childNodes;
    let symbol = cells[0].innerText;

    window.location.replace("../stockview/stockview.html?stock="+symbol);
/*    $.ajax({
        type: 'GET',
        data: {'symbol': symbol},
        url: "/showStock",
        error: function (xhr, httpErrorMessage, customErrorMessage) {
        },
        success: function (){

        }

    });*/
}

function updateUserDetails() {
    $.ajax({
        type: 'GET',
        url: '/UpdateUserDetails',
        success: function (json) {
            let result = JSON.parse(json);
            document.getElementById("userBalance").innerHTML = result["balance"];
            let table = document.getElementById("userCurrentBody");
            table.innerHTML = "";
            let actions = result["actions"];
            for(let i=0;i<actions.length;i++){
                let row = table.insertRow(0);
                let cell1 = row.insertCell(0);
                let cell2 = row.insertCell(1);
                let cell3 = row.insertCell(2);
                let cell4 = row.insertCell(3);
                let cell5 = row.insertCell(4);
                cell1.innerHTML = actions[i]["type"];
                cell2.innerHTML = actions[i]["date"];
                cell3.innerHTML = actions[i]["amount"];
                cell4.innerHTML = actions[i]["preBalance"];
                cell5.innerHTML = actions[i]["postBalance"];
            }
        },
        error: function () {

        }
    })
}


