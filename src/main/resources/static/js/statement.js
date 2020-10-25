$(document).ready( function () {
    var table = $('#statement-table').DataTable({
        "sAjaxSource": "/statement/list",
        "sAjaxDataProp": "",
        "order": [[ 0, "asc" ]],
        "buttons": [ 'pdfHtml5'],
        "aoColumns": [
            { "mData": "account_no"},
            { "mData": "transaction_timestamp"},
            { "mData": "description" },
            { "mData": function(val){
                    if(val.transaction_type===1){
                        return "Debit";
                    }else{
                        return "Credit";
                    }
                }},
            { "mData": "transaction_amount"},
            { "mData": "balance" },
            { "mData": function(val){
                    if(val.status===1){
                        return "Approved";
                    }else if(val.status ===2){
                        return "Declined";
                    }else{
                        return "Pending";
                    }
                }},
        ]
    })
});