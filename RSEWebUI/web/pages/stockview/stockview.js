$(function () { // onload
    let params = new URLSearchParams(location.search);
    let stockSymbol = params.get('stock');
    window.setInterval(updateShowStock,3000);
    document.cookie = "stock=" +stockSymbol;


})
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
            document.getElementById("username-greet").innerText ="Hi "+username;
            document.getElementById("company-name").innerHTML ="Company Name: " + stock["companyName"];
            document.getElementById("share-value").innerHTML ="Share Value: " + stock["sharePrice"];
            document.getElementById("turnover").innerHTML ="Turnover: " + stock["transactionsTurnOver"];
            document.getElementById("own-holdings").innerHTML ="You own " + userHoldings +" shares";
            ////document.getElementById("#").innerHTML ="Hi "+username;
            //$("#username-greet").innerHTML = "Hi "+username;
            //$("#details-box-header").childNodes[0].innerText = stock["symbol"] +" Stock View";
            //$("#company-name"). innerHTML = "Company Name: " + stock["companyName"];
            //$("#share-value").innerHTML = "Share Value: " + stock["sharePrice"];
            //$("#turnover").innerHTML = "Turnover: " + stock["transactionsTurnOver"];
            //$("#own-holdings").innerHTML = "You own " + userHoldings +" shares";
        }

    })
}