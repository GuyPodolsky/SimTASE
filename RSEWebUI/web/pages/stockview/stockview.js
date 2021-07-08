$(function () { // onload
    let params = new URLSearchParams(location.search);
    let stockSymbol = params.get('stock');
    window.setTimeout(updateShowStock, 1);
    window.setInterval(updateShowStock, 3000);
    document.cookie = "stock=" + stockSymbol;

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
            error:function (){
                alert("PROBLEMMMMM");
            }
        })
        return false;
    })
})



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
        error: function (xhr, httpErrorMessage, customErrorMessage) {
            alert(customErrorMessage);
        },
        success: function (res) {
            let data = JSON.parse(res);
            let username = data["username"];
            let userHoldings = data["userHoldings"];
            let stock = data["stock"];
            document.getElementById("username-greet").innerText = "Hi " + username;
            document.getElementById("company-name").innerHTML = "Company Name: " + stock["companyName"];
            document.getElementById("share-value").innerHTML = "Share Value: " + stock["sharePrice"];
            document.getElementById("turnover").innerHTML = "Turnover: " + stock["transactionsTurnOver"];
            document.getElementById("own-holdings").innerHTML = "You own " + userHoldings + " shares";
            document.getElementById("details-box-header").children[0].innerHTML = stock["symbol"] + " Stock View";

            // Show the stock transactions
            let transBody = document.getElementById("transactions-table-body");
            let transactions = stock["stockTransactions"];
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
                cell4.innerHTML = tran["seller"]["username"];
                cell5.innerHTML = tran["buyer"]["username"];
            } // for

            // Show the stock awaiting buy trade commands
            let buycommandsBody = document.getElementById("buy-commands-table-body");
            let buyCommands = stock["buyCommandDTs"];
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
                cell2.innerHTML = buy["user"]["username"];
                cell3.innerHTML = "Buy command";
                cell4.innerHTML = buy["commandType"];
                cell5.innerHTML = buy["wantedPrice"];
                cell6.innerHTML = buy["quantity"];
            } // for

            // Show the stock awaiting sell trade commands
            let sellcommandsBody = document.getElementById("sell-commands-table-body");
            let sellCommands = stock["sellCommandDTs"];
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
                cell2.innerHTML = sell["user"]["username"];
                cell3.innerHTML = "Sell command";
                cell4.innerHTML = sell["commandType"];
                cell5.innerHTML = sell["wantedPrice"];
                cell6.innerHTML = sell["quantity"];
            } // for
        } // successes
    })// ajax call
}

function goBack(){
    window.location.replace("../dashboard/dashboard.html");
}