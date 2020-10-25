/*
 *
 */

$(document).ready(function() {
    $('.table .eBtn').on('click',function (event) {
        event.preventDefault();
        var name = $(this).attr('name');
        $.ajax({
            type:"GET",
            url: "/account/get/"+name,
            success: function(data) {
                document.getElementById("accountno").value = data.accountNo;
                document.getElementById("name").value = data.userName;
                document.getElementById("balance").value = data.balance;
                document.getElementById("account-type").value = data.accountType;
                document.getElementById("interest").value = data.interest;
            }
        });
        $('.accountForm #exampleModal').modal();
    });
    $('.table .eBtn2').on('click',function (event) {
        event.preventDefault();
        var name = $(this).attr('name');
        var approveForm = document.getElementById("delete");
        approveForm.action +=name;
        $('.deleteForm #exampleModal2').modal();
    });
});