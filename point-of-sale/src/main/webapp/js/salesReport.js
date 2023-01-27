function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/sales";
}

function filterSalesReport() {
    var $form = $("#sales-form");
    var json = toJson($form);
    var url = getSalesReportUrl();

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            displaySalesReport(response);
       },
       error: handleAjaxError
    });
}

function displaySalesReport(data) {
    var $tbody = $('#sales-table').find('tbody');
    $tbody.empty();
    for(var i in data){
        var b = data[i];
        var row = '<tr>'
        + '<td>&nbsp;</td>'
        + '<td>' + b.brand + '</td>'
        + '<td>' + b.category + '</td>'
        + '<td>' + numberWithCommas(b.quantity) + '</td>'
        + '<td class="text-right">' + numberWithCommas(b.revenue.toFixed(2)) + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

//INITIALIZATION CODE
function init(){
    $('#nav-report').addClass('active');
	$('#filter-sales-report').click(filterSalesReport);
}

$(document).ready(init);
$(document).ready(filterSalesReport);