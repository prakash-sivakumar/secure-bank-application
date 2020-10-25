let Keyboard = window.SimpleKeyboard.default;

let keyboard = new Keyboard({
    onChange: input => onChange(input),
    onKeyPress: button => onKeyPress(button)
});

/**
 * Update simple-keyboard when input is changed directly
 */
document.querySelector(".input").addEventListener("input", event => {
    keyboard.setInput(event.target.value);
});

console.log(keyboard);

function onChange(input) {
    document.querySelector(".input").value = input;
}

function onKeyPress(button) {

    /**
     * If you want to handle the shift and caps lock buttons
     */
    if (button === "{shift}" || button === "{lock}") handleShift();
}

function handleShift() {
    let currentLayout = keyboard.options.layoutName;
    let shiftToggle = currentLayout === "default" ? "shift" : "default";

    keyboard.setOptions({
        layoutName: shiftToggle
    });
}

$(document).ready(function() {
    $('.form-signin').on('submit',function (event) {
        event.preventDefault();

        var data  = $("#email1").val();
        $.ajax({
            type: "GET",
            url:  "/otp/generateOtp/"+data,
            success : function(data) {

            },
        });
        document.getElementById('password').value = document.getElementById('password1').value;
        document.getElementById('email').value = document.getElementById('email1').value;
        $('.profileForm #newaccountModal').modal();

    });
});
