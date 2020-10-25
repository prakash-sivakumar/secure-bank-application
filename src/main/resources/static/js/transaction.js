/*
 *
 */

function generate(){
    $.ajax({
        type: "GET",
        url:  "/otp/generateOtp/",
        success : function(data) {

        },
    });
    $('.profileForm #newaccountModal').modal();
}

$(document).ready( function () {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    $('#transfer').on('click',function (event) {
        console.log("here");
        event.preventDefault();
        var from = document.getElementById('AccountNumber');
        if(from === null){
            from = document.getElementById('account1').value;
        }else{
            from = from.value;
        }
        var to = document.getElementById('account');
        var select = document.getElementById('account-select');
        if(to){
            if(from === to.value){
                document.getElementById('description-error').innerText = "From and to account cannot be same";
                event.preventDefault();
                return;
            }
        }
        else if(select){

            if(select.options[select.selectedIndex].value === from){
                document.getElementById('description-error').innerText = "From and to account cannot be same";
                event.preventDefault();
                return;
            }
        }
        var val = document.getElementById('myForm').checkValidity();

        if(val){
            generate();
        }else{
            document.getElementById('myForm').reportValidity()
        }
    });
    $('#phone-btn').on('click',function (event) {
        event.preventDefault();
        var phone = document.getElementById('phone').value;
        if(!phone){
            document.getElementById('phone-error').innerText = "Invalid phone";
            return;
        }else{
            document.getElementById('phone-error').innerText ="";
        }
        var dropdown = $('#account-select');
        document.getElementById('account-id').style.display = 'block';
        dropdown.prop('selectedIndex', 0);
        dropdown.find('option').not(':first').remove();
        var url = '/user/accounts/phone/'+phone;

        $.getJSON(url, function (data) {
            $.each(data, function (key, entry) {
                var type = 'Savings';
                if (entry.accountType === 1){
                    type='Checkings';
                }else if(entry.accountType === 2){
                    type='Savings';
                }else{
                    type='Credit card';
                }
                dropdown.append($('<option></option>').attr('value', entry.accountNo).text(type+': '+entry.accountNo));
            })
        });
        // var val = document.getElementById('myForm').checkValidity();
        // event.preventDefault();
        // if(val){
        //     generate();
        // }
        // else{
        //     document.getElementById('myForm').reportValidity()
        // }
    });

    $('#email-btn').on('click',function (event) {
        event.preventDefault();
        var email = document.getElementById('email').value;
        if(!email || !re.test(String(email).toLowerCase())){
            document.getElementById('email-error').innerText = "Invalid email";
            return;
        }else{
            document.getElementById('email-error').innerText ="";
        }
        var dropdown = $('#account-select');
        document.getElementById('account-id').style.display = 'block';
        dropdown.prop('selectedIndex', 0);
        dropdown.find('option').not(':first').remove();
        var url = '/user/accounts/email/'+email;

        $.getJSON(url, function (data) {
            $.each(data, function (key, entry) {
                var type = 'Savings';
                if (entry.accountType === 1){
                    type='Checkings';
                }else if(entry.accountType === 2){
                    type='Savings';
                }else{
                    type='Credit card';
                }
                dropdown.append($('<option></option>').attr('value', entry.accountNo).text(type+': '+entry.accountNo));
            })
        });
        // var val = document.getElementById('myForm').checkValidity();
        // event.preventDefault();
        // if(val){
        //     generate();
        // }else{
        //     document.getElementById('myForm').reportValidity()
        // }
    });

    $('#new').on('submit',function (event) {
        event.preventDefault();
        document.getElementById("otp").value = document.getElementById("code").value;
        $('#myForm').submit();
    });

});