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
            let username = data["username"];
            let userHoldings = data["userHoldings"];
            let stock = data["stock"];
            $("#username-greet").innerText = "Hi "+username;
            //$("#details-box-header").childNodes[0].innerText = stock["symbol"] +" Stock View";
            $("#company-name"). innerText = "Company Name: " + stock["companyName"];
            $("#share-value").innerText = "Share Value: " + stock["sharePrice"];
            $("#turnover").innerText = "Turnover: " + stock["transactionsTurnOver"];
            $("#own-holdings").innerText = "You own " + userHoldings +" shares";
        }

    })
}