function getInventoryReportUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/reports/inventory";
}

function filterInventoryReport() {
    var $form = $("#inventory-form");
    var json = toJson($form);
    var url = getInventoryReportUrl();

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            displayInventoryReportList(response);
       },
       error: handleAjaxError
    });
}

//UI DISPLAY METHODS
function displayInventoryReportList(data) {
  var $tbody = $("#inventory-report-table").find("tbody");
  $tbody.empty();
  for (var i in data) {
    var e = data[i];
    var row =
      "<tr>" +
      "<td>" +
      e.brand +
      "</td>" +
      "<td>" +
      e.category +
      "</td>" +
      "<td>" +
      e.quantity +
      "</td>" +
      "</tr>";
    $tbody.append(row);
  }
}

//INITIALIZATION CODE
function init() {
  $('#nav-report').addClass('active');
  $("#filter-inventory-report").click(filterInventoryReport);
}

$(document).ready(init);
$(document).ready(filterInventoryReport);
