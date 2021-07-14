

$(function (){ // onload
    window.setTimeout(updateActiveUsers,1);
    window.setTimeout(updateActiveStocks,1);
    //window.setTimeout(updateUserDetails,1);
    window.setTimeout(isAdmin,1);
    window.setInterval(updateActiveUsers,2000);
    window.setInterval(updateActiveStocks,2000);
    //window.setInterval(updateUserDetails,2000);
    window.setInterval(getMessagesFromServer,2000);

    $("#addingToBalance").submit(function (){
        $.ajax({
            type:'POST',
            data:{
                op:"add",
                amount:document.getElementById("add-amount").value},
            url:'/UpdateUserDetails',
            error:function (jqXHR, textStatus, errorThrown){
                //notifyMe("Error: "+jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
                showError(jqXHR, textStatus, errorThrown);
            },
            success:function (){
                document.getElementById("add-amount").value = null;
            }
        })
        return false;
    })

    $("#addPublicOffering").submit(function(){
        $.ajax({
            type:'POST',
            data:$(this).serialize(),
            url:"/AddNewStockServlet",
            error:function (jqXHR, textStatus, errorThrown){
                //notifyMe("Error: "+jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
                showError(jqXHR, textStatus, errorThrown);
                },
            success: function (msg){
                notifyMe(msg,"IPO Is Complete","success")//notifyMe(msg);
                document.getElementById("addPublicOffering").reset();
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
            error:function (jqXHR, textStatus, errorThrown){
                //notifyMe("Error",qXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
                showError(jqXHR, textStatus, errorThrown);
                },
            success: function (){
                document.getElementById("transferMoney").reset();
            }
        })
        return false;
    })

    $("#xmlLoad").submit(function () {
        let file = this[0].files[0];
        let formData = new FormData();
        formData.append("key", file);//"fake-key"
        $.ajax({
            method: 'POST',
            data: formData,
            url: '/LoadXMLServlet',
            processData: false, // Don't process the files
            contentType: false,
            error: function (jqXHR, textStatus, errorThrown) {
                //notifyMe("Error:"+jqXHR.status + " " + jqXHR.getResponseHeader("errorMessage"));
                //showError(jqXHR, textStatus, errorThrown);
            },
            success: function (msg) {
                //notifyMe(msg);
                //notifyMe("File loaded successfully!");
            }
        });
    });


});

function isAdmin(){
    $.ajax({
        type: 'GET',
        data: {op: "isAdmin"},
        url: '/UpdateUserDetails',
        error: function (jqXHR, textStatus, errorThrown) {
            //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
            showError(jqXHR, textStatus, errorThrown);
        },
        success: function (res) {
            var userSec = document.getElementById('user-section');
            let actUsers = document.getElementById('activeUsersSection');
            let actStocks = document.getElementById('activeStocksSection');
            let IPO= document.getElementById('addPublicOfferBlock');
            let data = JSON.parse(res);
            let is_admin = Boolean(data["is_admin"]);
            if(is_admin) {
               userSec.style.display='none';
               IPO.style.display='none';
               document.getElementById("activeUsersSection").style.height = "100%";
               document.getElementById("activeStocksSection").style.height = "100%";
               document.getElementById("body").style.height = "100%";
            }
            else {
                window.setTimeout(updateUserDetails,1);
                window.setInterval(updateUserDetails,2000);
            }
        }
    })
}

function showError(jqXHR, textStatus, errorThrown){
    //notifyMe("Error: "+jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
    notifyMe(jqXHR.getResponseHeader("errorMessage"),"Error: "+jqXHR.status,"error");
}
function updateActiveUsers(){ //TODO: make the refresh less annoying.
    $.ajax({
        type:'GET',
        url:"/activeUsers",
        dataType:'json',
/*
        timeout:2000,
*/
        error:function (jqXHR, textStatus, errorThrown){
            //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
            //showError(jqXHR, textStatus, errorThrown);
            notifyMe("Problem with getting active users. Please check your connection.");
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
        var formData = new FormData();
        formData.append("key",files[0]);
        console.log(files);
        $.ajax({
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                url: '/LoadXMLServlet',
                error: function (jqXHR, textStatus, errorThrown){
                    //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
                    showError(jqXHR, textStatus, errorThrown);
                },
                success: function (msg) {
                    notifyMe(msg,"XML File Loading Is Complete")//notifyMe(msg);
                }
            },
        )
    };
    input.click();
}

function stockSelected(event) {

    let row = event.currentTarget;
    let cells = row.childNodes;
    let symbol = cells[0].innerText;

    window.location.replace("../stockview/stockview.html?stock="+symbol);
}

function updateUserDetails() {
    $.ajax({
        type: 'GET',
        data:{op:"actions"},
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
        }
    })
}

function getMessagesFromServer(){
    $.ajax({
        type: 'GET',
        url: '/MessageFromServer',
        success: function (json) {
           let msgs = JSON.parse(json);
           for(var i =0;i<msgs.length;i++){
               notifyMe(msgs[i],"Message from server:","info")//notifyMe(msgs[i]);
           }
        }
    })
}
