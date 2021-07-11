$(function () {
   $('#loginForm').submit(function (){
       $.ajax({
           type:'GET',
           data: $('#loginForm').serialize(),
           url:"/login",
           success: function (){
               window.location.assign("../dashboard/dashboard.html");
           },
           error: function (xhr, httpStatusMessage, customErrorMessage) {
               document.getElementById("status").style.display = 'block';
               document.getElementById("status").style.color = 'red';
               document.getElementById("username").style.border = '1px solid red';
               setTimeout(function () {
                       document.getElementById("status").style.display = 'none';
                       document.getElementById("status").style.color = '';
                       document.getElementById("username").style.border = '';
                   }
                   ,30*1000)
               if(xhr.status === 601) {
                   document.getElementById("status").innerHTML = "Empty username was entered";
               }
               if(xhr.status === 602) {
                   document.getElementById("status").innerHTML ="Existing username was entered";
               }
           }
       });
       return false;
   })
});

function login(){
    $.ajax({
        type:'GET',
        data: $('#loginForm').serialize(),
        url:"/login",
        success: function (){
            notifyMe("You logged in successfully!");
            window.location.url("/../dashboard/dashboard.html");
        },
        error: function (xhr, httpStatusMessage, customErrorMessage) {
            if(xhr.status === 601) {
                alert(customErrorMessage);}
        }
    });
    return false;
}

