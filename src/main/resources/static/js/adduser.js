$(document).ready( function () {
    $('#transfer').on('click',function (event) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        var email = document.getElementById('email').value;
        if (!email || !re.test(String(email).toLowerCase())) {
            document.getElementById('email-error').innerText = "Invalid email";
            return;
        } else {
            document.getElementById('email-error').innerText = "";
        }
    });
});