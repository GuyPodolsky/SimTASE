function login(){
    $.ajax({
        type:'GET',
        data: {'username':$('#username').value,'is_admin':$('#is_admin').value},
        url:"/login",
        success: function (){
            var isOk= "<%=request.getAttribute(\"is_ok\");%>";
            if(isOk===true) {
               //window.location.url('/../dashboard/dashboard.html');
                "<%=Logger.getServerLogger().post(\"true\");%>"
                notifyMe('all good','welcome');
            } else {
                //window.location.url('login.html');
                "<%=Logger.getServerLogger().post(\"false\");%>"
                notifyMe('not good','try again');
            }
        }
    });
}