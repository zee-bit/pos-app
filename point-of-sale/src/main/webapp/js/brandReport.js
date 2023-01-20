function getBrandReportUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/reports/brand";
}

function filterBrandReport() {
    var $form = $("#brand-form");
    var json = toJson($form);
    var url = getBrandReportUrl();

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            displayBrandReportList(response);
       },
       error: handleAjaxError
    });
}

//UI DISPLAY METHODS
function displayBrandReportList(data) {
  var $tbody = $("#brandCategory-table").find("tbody");
  $tbody.empty();
  for (var i in data) {
    var e = data[i];
    var row =
      "<tr>" +
      "<td>&nbsp;</td>" +
      "<td>" +
      e.brand +
      "</td>" +
      "<td>" +
      e.category +
      "</td>" +
      "</tr>";
    $tbody.append(row);
  }
}

//INITIALIZATION CODE
function init() {
  $('#nav-report').addClass('active');
  $("#filter-brand-report").click(filterBrandReport);
}

$(document).ready(init);
$(document).ready(filterBrandReport);
