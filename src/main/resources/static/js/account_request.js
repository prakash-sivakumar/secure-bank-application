/*
 *
 */

$(document).ready(function() {
    $('.table .eBtn').on('click',function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var approveForm = document.getElementById("approve");
        approveForm.action =href;
        $('.requestForm #exampleModal').modal();
    });
    $('.table .eBtn2').on('click',function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var approveForm = document.getElementById("decline");
        approveForm.action =href;
        $('.requestForm2 #exampleModal2').modal();
    });
});