/*
 *
 */

$(document).ready(function() {
    $('.table .eBtn').on('click',function (event) {
        event.preventDefault();
        var name = $(this).attr('name');
        $.ajax({
            type:"GET",
            url: "/employee/get/"+name,
            success: function(data) {
                document.getElementById("employeeid").value = data.employee_id;
                document.getElementById("name").value = data.employee_name;
                document.getElementById("gender").value = data.gender;
                document.getElementById("age").value = data.age;
                document.getElementById("tier_level").value = data.tier_level;
                document.getElementById("contact").value = data.contact_no;
                document.getElementById("email").value = data.email_id;
                document.getElementById("address").value = data.address;
                document.getElementById("designation").value = data.designation_id;
            }
        });
        $('.employeeForm #exampleModal').modal();
    });
    $('#create').on('click',function (event) {
        event.preventDefault();
        $('.employeeForm1 #exampleModal1').modal();
    });

    $('.table .eBtn2').on('click',function (event) {
        event.preventDefault();
        var name = $(this).attr('name');
        var approveForm = document.getElementById("delete");
        approveForm.action +=name;
        $('.deleteForm #exampleModal2').modal();
    });
});
