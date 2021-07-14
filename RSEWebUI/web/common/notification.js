/*function notifyMe(title,body) {
    if (!window.Notification) {
        console.log('Browser does not support notifications.');
    } else {
        // check if permission is already granted
        if (Notification.permission === 'granted') {
            // show notification here
            let notify = new Notification(title, {
                body: body,
            });
            setTimeout(() => notify.close(),60*1000);
            console.log('Notification sent.');
        } else {
            // request permission from user
            Notification.requestPermission().then(function (p) {
                if (p === 'granted') {
                    // show notification here
                    let notify = new Notification(title, {
                        body: body,
                    });
                    setTimeout(() => notify.close(),60*1000);
                    console.log('Notification sent.');
                } else {
                    console.log('User blocked notifications.');
                }
            }).catch(function (err) {
                console.error(err);
            });
        }
    }
}*/

function notifyMe(msg,title,type,timeout){
    if(timeout===undefined)
        timeout="-1";
    if(title===undefined)
        title="Notification";
    if(type!="error" && type!="info" && type!="success" && type!="warning")
        type="info";

    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": false,
        "positionClass": "toast-bottom-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "-1",
        "hideDuration": "-1",
        "timeOut": timeout,
        "extendedTimeOut": "-1",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut",
    }

    toastr[type](msg, title)


/*    if(sessionStorage.getItem("noteMessage") == null)
        sessionStorage.setItem("noteMessage",JSON.stringify([]));
    let arr = JSON.parse(sessionStorage.getItem("noteMessage"));
    arr.push(msg);
    sessionStorage.setItem("noteMessage",JSON.stringify(arr));
    //document.getElementById('notifyUser').hidden = Boolean(localStorage.isShow);*/
}



/*function notifyMe(title,body) {
    if (!window.Notification) {
        console.log('Browser does not support notifications.');
    } else {
        // check if permission is already granted
        if (Notification.permission === 'granted') {
            // show notification here
            let notify = new Notification(title, {
                body: body,
            });
            setTimeout(() => notify.close(),60*1000);
            console.log('Notification sent.');
        } else {
            // request permission from user
            Notification.requestPermission().then(function (p) {
                if (p === 'granted') {
                    // show notification here
                    let notify = new Notification(title, {
                        body: body,
                    });
                    setTimeout(() => notify.close(),60*1000);
                    console.log('Notification sent.');
                } else {
                    console.log('User blocked notifications.');
                }
            }).catch(function (err) {
                console.error(err);
            });
        }
    }
}*/
