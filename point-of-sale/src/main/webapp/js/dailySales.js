function getDailySalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/daily-sales";
}

function filterSalesReport() {
    var url = getDailySalesReportUrl();
    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            displayDailySalesReport(response);
       },
       error: handleAjaxError
    });
}

function displayDailySalesReport(data) {
    var $tbody = $('#daily-sales-table').find('tbody');
    $tbody.empty();
    for(var i in data){
        var b = data[i];
        var row = '<tr>'
        + '<td>&nbsp;</td>'
        + '<td>' + convertTimeStampToDateTime(b.date) + '</td>'
        + '<td>' + b.orderCount + '</td>'
        + '<td>' + b.itemCount + '</td>'
        + '<td>' + b.totalRevenue.toFixed(2) + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

//INITIALIZATION CODE
function init(){
   $('#nav-report').addClass('active');
   $('#get-daily-sales-report').click(filterSalesReport);
}

$(document).ready(init);
$(document).ready(filterSalesReport);