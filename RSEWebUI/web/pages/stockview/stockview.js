var admin=false;
function showError(jqXHR, textStatus, errorThrown){
    notifyMe("Error: "+jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
}

$(function () { // onload
    let params = new URLSearchParams(location.search);
    let stockSymbol = params.get('stock');
    window.setTimeout(updateShowStock, 1);
    window.setTimeout(isAdmin,1);
    window.setInterval(updateShowStock, 3000);
    window.setInterval(getMessagesFromServer,2000);
    window.setInterval(updateView,1);
    document.cookie = "stock=" + stockSymbol;
    document.title = String(stockSymbol) + " Stock View";

    document.getElementById('typeSel').onchange = function (event) {
       let sel = document.getElementById('typeSel').value;
       let price = document.getElementById('priceBox');
       if(sel === '1')
           price.hidden = true;
       else
           price.hidden = false;
    };

    $("#buy-sell-form").submit(function (){
        let dir = document.getElementById("dirChoice1").checked ? "buy":"sell";
        let type = document.getElementById("typeSel").options.selectedIndex;
        let quantity = document.getElementById("quantity").value;
        let price = document.getElementById("price").value;
        $.ajax({
            type:'POST',
            data:{
                symbol:stockSymbol,
                dir:dir,
                type:type,
                quantity:quantity,
                price:price
            },
            url:'/TradeCommands',
            error: function (jqXHR, textStatus, errorThrown){
                //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
                showError(jqXHR, textStatus, errorThrown);
            },
            success:function (msg){
                notifyMe(msg);
                document.getElementById("buy-sell-form").reset();
            }
        })
        return false;
    })
})

function drawChart(json) {
    let maxNum = 0;
    let jsonParse = JSON.parse(json);
    let dataArr = jsonParse["stockTransactions"];
    let arr = new Array(dataArr.length+1);
    arr[0] =  new Array(2);
    arr[0][0] = 'Timestamp';
    arr[0][1] = 'Price Per Share';
    for(let i=1; i<arr.length;i++){
        let dataRow = dataArr[dataArr.length-i];
        arr[i]=new Array(2);
        arr[i][0]= String(dataRow["formattedTimestamp"]);
        let num = Number.parseFloat(dataRow["price"]);
        arr[i][1]= num;
        if(maxNum<num)
            maxNum=num;
    }

    let data = google.visualization.arrayToDataTable(arr);
    /*var data = google.visualization.arrayToDataTable([
        ['Year', 'Sales', 'Expenses'],
        ['2004',  1000,      400],
        ['2005',  1170,      460],
        ['2006',  660,       1120],
        ['2007',  1030,      540]
    ]);*/

    maxNum= maxNum+((maxNum%13+1)*50);
    var options = {
        title: 'Share Price',
        curveType: 'function',
        legend: { position: 'bottom' },
        vAxis: {
            viewWindow: {
                min: 0,
                max: maxNum
            },
        }
    };



    var chart = new google.visualization.LineChart(document.getElementById('graph-box'));

    chart.draw(data, options);
}

function updateView(){
    let trans = document.getElementById('transactions-table');
    let sell = document.getElementById('sell-commands-table');
    let buy = document.getElementById('buy-commands-table');
    const rbs = document.querySelectorAll('input[name="view"]');
    let selectedValue;
    for (const rb of rbs) {
        if (rb.checked) {
            selectedValue = rb.value;
            break;
        }
    }
    switch (selectedValue){
        case 'trans':
            trans.hidden = false;
            sell.hidden = true;
            buy.hidden = true;
            break;
        case 'buy':
            trans.hidden = true;
            sell.hidden = true;
            buy.hidden = false;
            break;
        case 'sell':
            trans.hidden = true;
            sell.hidden = false;
            buy.hidden = true;
            break;
    }

}

function deleteAllCookies() {
    var cookies = document.cookie.split(";");

    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function updateShowStock(){
    let stockSymbol = getCookie("stock");
    $.ajax({
        type: 'GET',
        data: {'symbol': stockSymbol},
        url: "/showStock",
        error: function (jqXHR, textStatus, errorThrown){
            //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
            showError(jqXHR, textStatus, errorThrown);
        },
        success: function (res) {
            let data = JSON.parse(res);
            let username = data["username"];
            let userHoldings = data["userHoldings"];
            let stock = data["stockDetails"];
            document.getElementById("username-greet").innerText = "Hi " + username;
            document.getElementById("company-name").innerHTML = "Company Name: " + stock["companyName"];
            document.getElementById("share-value").innerHTML = "Share Value: " + stock["sharePrice"];
            document.getElementById("turnover").innerHTML = "Turnover: " + stock["transactionsTurnOver"];
            document.getElementById("own-holdings").innerHTML = "You own " + userHoldings + " shares";
            document.getElementById("details-box-header").children[0].innerHTML = stock["symbol"] + " Stock View";

            // Show the stock transactions
            let transBody = document.getElementById("transactions-table-body");
            let transactions = data["stockTransactions"];
            drawChart(res);
            transBody.innerHTML = "";
            for (var i = 0; i < transactions.length; i++) {
                let tran = transactions[i];   // the i'th transaction
                let row = transBody.insertRow(0);
                let cell1 = row.insertCell(0);
                let cell2 = row.insertCell(1);
                let cell3 = row.insertCell(2);
                let cell4 = row.insertCell(3);
                let cell5 = row.insertCell(4);
                cell1.innerHTML = tran["formattedTimestamp"];
                cell2.innerHTML = tran["quantity"];
                cell3.innerHTML = tran["price"];
                cell4.innerHTML = tran["sellerName"];
                cell5.innerHTML = tran["buyerName"];
            } // for

            // Show the stock awaiting buy trade commands
            let buycommandsBody = document.getElementById("buy-commands-table-body");
            let buyCommands = data["stockBuyCommands"];
            buycommandsBody.innerHTML = "";
            for (var i = 0; i < buyCommands.length; i++) {
                let buy = buyCommands[i];   // the i'th transaction
                let row = buycommandsBody.insertRow(0);
                let cell1 = row.insertCell(0);
                let cell2 = row.insertCell(1);
                let cell3 = row.insertCell(2);
                let cell4 = row.insertCell(3);
                let cell5 = row.insertCell(4);
                let cell6 = row.insertCell(5);
                cell1.innerHTML = buy["formattedDateTime"];
                cell2.innerHTML = buy["username"];
                cell3.innerHTML = "Buy command";
                cell4.innerHTML = buy["typeString"];
                cell5.innerHTML = buy["wantedPrice"];
                cell6.innerHTML = buy["quantity"];
            } // for

            // Show the stock awaiting sell trade commands
            let sellcommandsBody = document.getElementById("sell-commands-table-body");
            let sellCommands = data["stockSellCommands"];
            sellcommandsBody.innerHTML = "";
            for (var i = 0; i < sellCommands.length; i++) {
                let sell = sellCommands[i];   // the i'th transaction
                let row = sellcommandsBody.insertRow(0);
                let cell1 = row.insertCell(0);
                let cell2 = row.insertCell(1);
                let cell3 = row.insertCell(2);
                let cell4 = row.insertCell(3);
                let cell5 = row.insertCell(4);
                let cell6 = row.insertCell(5);
                cell1.innerHTML = sell["formattedDateTime"];
                cell2.innerHTML = sell["username"];
                cell3.innerHTML = "Sell command";
                cell4.innerHTML = sell["typeString"];
                cell5.innerHTML = sell["wantedPrice"];
                cell6.innerHTML = sell["quantity"];
            } // for
        } // successes
    })// ajax call
}

function isAdmin() {
    $.ajax({
        type: 'GET',
        data: {op: "isAdmin"},
        url: '/UpdateUserDetails',
        error: function (jqXHR, textStatus, errorThrown){
            //notifyMe("Error",jqXHR.status + " " +jqXHR.getResponseHeader("errorMessage"));
            showError(jqXHR, textStatus, errorThrown);
        },
        success: function (res) {
            let box1 = document.getElementById('details-box');
            let box4 = document.getElementById('buy-sell-box');
            let ownHoldings = document.getElementById('own-holdings');
            let viewSel= document.getElementById('trans-or-ops');
            let data = JSON.parse(res);
            let is_admin = data["is_admin"];
            if(Boolean(is_admin)) {
                admin=true;
                box4.hidden = true;
                box1.style.gridRow='1/3';
                ownHoldings.hidden = true;
                ////
                viewSel.hidden = false;

            }else{
                admin=false;
                box4.hidden = false;
                box1.style.gridRow='1';
                ownHoldings.hidden = false;
                ////
                viewSel.hidden = true;
            }
        }
    });
}

function goBack(){
    window.location.replace("../dashboard/dashboard.html");
}

function getMessagesFromServer(){
    $.ajax({
        type: 'GET',
        url: '/MessageFromServer',
        success: function (json) {
            let msgs = JSON.parse(json);
            for(var i =0;i<msgs.length;i++){
                notifyMe(msgs[i]);
            }
        }
    })
}