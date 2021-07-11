/*
function notifyMe(title,body) {
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

function notifyMe(msg){
    localStorage.noteMessage = msg;
}

