/*
 *
 */

$(document).ready(function() {
    $('#edit-profile').on('click',function (event) {
        event.preventDefault();
        $('.profileForm #profileModal').modal();
    });

    $('#new-account').on('click',function (event) {
        event.preventDefault();
        $('.profileForm #newaccountModal').modal();
    });
});