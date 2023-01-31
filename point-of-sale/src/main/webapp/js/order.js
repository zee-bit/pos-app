
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getRole(){
	var role = $("meta[name=role]").attr("content")
	return role;
}

//BUTTON ACTIONS

function updateOrder(event){
    var ok = true;
	const data = orderItems.map((it) => {
	    console.log(it.quantity);
        if(isNaN(it.quantity)) {
            $.notify.defaults( {clickToHide:true,autoHide:false} );
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Quantity cannot be empty", "error");
            ok = false;
        }
        if(isNaN(it.sellingPrice)) {
            $.notify.defaults( {clickToHide:true,autoHide:false} );
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Selling Price cannot be empty", "error");
            ok = false;
        }
        return {
          barcode: it.barcode,
          quantity: it.quantity,
          sellingPrice: it.sellingPrice,
        };
      });
    if(!ok) return;
    //	var json = toJson($form);
    const json = JSON.stringify(data);
    var id = $("#order-edit-modal input[name=id]").val();
    var url = getOrderUrl() + "/" + id;
    $.ajax({
       url: url,
       type: 'PUT',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            $('#order-edit-modal').modal('toggle');
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Order successfully edited!", 'success');
            getOrderList();
       },
       error: handleAjaxError
    });
}


function getOrderList(){
	var url = getOrderUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderList(data);
	   },
	   error: handleAjaxError
	});
}

function populateBarcodeDropdown(formId) {
    var url = getInventoryUrl();
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            addDataToBarcodeDropdown(data, formId)
        },
        error: handleAjaxError
    });
}

function addDataToBarcodeDropdown(data, formId) {
	var $barcode = $(`${formId} select[name=barcode]`);
	console.log($barcode);
	$barcode.empty();

	var barcodeDefaultOption = '<option value="">Select a barcode</option>';
    $barcode.append(barcodeDefaultOption);

	for (var i in data) {
	  var e = data[i];
	  var option =
		'<option value="' +
		e.barcode +
		'">' +
		e.barcode +
		"</option>";
	  $barcode.append(option);
	}
  }

function displayCreateOrderItems(orderItems) {
    var $tbody = $('#order-item-table').find('tbody');
    $tbody.empty();

    for(var i in orderItems) {
        var e = orderItems[i];
        const row = `
         <tr>
               <td>&nbsp;</td>
               <td>${e.barcode}</td>
               <td>
                    <input
                      id="order-item-${e.barcode}"
                      type="number"
                      class="form-control quantityData"
                      value="${e.quantity}"
                      onchange="onQuantityChanged('${e.barcode}')"
                      style="width:70%" min="1">
               </td>
               <td>
                    <input
                      id="order-item-sp-${e.barcode}"
                      type="number"
                      class="form-control sellingPriceData"
                      value="${e.sellingPrice}"
                      onchange="onSellingPriceChanged('${e.barcode}')"
                      style="width:70%" min="0" step="0.01">
               </td>
               <td>
                     <button class="btn btn-outline-danger px-4 mx-2" data-toggle="tooltip"
                       title="Delete item" onclick="deleteOrderItem(\'${e.barcode}\')">
                         <i class="fa fa-trash fa-lg"></i>
                     </button>
               </td>
             </tr>
           `;

//        var buttonHtml = `<button class="btn btn-outline-danger px-4 mx-2" data-toggle="tooltip"
//          title="Delete item" onclick="deleteOrderItem(\'${e.barcode}\')">
//            <i class="fa fa-trash fa-lg"></i>
//          </button>`
//        var row = '<tr>'
//        + '<td>&nbsp;</td>'
//        + '<td>' + e.barcode + '</td>'
//        + '<td>' + numberWithCommas(e.quantity) + '</td>'
//        + '<td class="text-right">' + numberWithCommas(e.sellingPrice.toFixed(2)) + '</td>'
//        + '<td>' + buttonHtml + '</td>'
//        + '</tr>';
        $tbody.append(row);
    }
}

//UI DISPLAY METHODS

let orderItems = [];

function getCurrentOrderItem() {
  return {
    barcode: $('#inputBarcode').val(),
    sellingPrice: Number.parseFloat($('#inputSellingPrice').val()),
    quantity: Number.parseInt($('#inputQuantity').val()),
  };
}

function addItem(item) {
  const index = orderItems.findIndex((it) => it.barcode === item.barcode.toString());
  if (index == -1) {
    orderItems.push(item);
  } else {
    orderItems[index].quantity += item.quantity;
  }
}

function deleteOrderItem(barcode) {
    const index = orderItems.findIndex((it) => it.barcode === barcode.toString());
    if (index == -1) return;
    orderItems.splice(index, 1);
    displayCreateOrderItems(orderItems);
}

function addOrderItem(event) {
    event.preventDefault();
    const item = getCurrentOrderItem();
    addItem(item);
    displayCreateOrderItems(orderItems);
    $('#order-item-form').trigger("reset");
}

function addEditOrderItem() {
  const item = getCurrentEditOrderItem();
  addItem(item);
  displayEditOrder(orderItems);
  $('#edit-order-item-form').trigger("reset");
}

function deleteEditOrderItem(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode.toString());
  if (index == -1) return;
  orderItems.splice(index, 1);
  displayEditOrder(orderItems);
}

function displayOrderList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var b = data[i];
        // var buttonHtml = '<button onclick="deleteOrder(' + b.id + ')">delete</button>'
        var orderDateStr = convertTimeStampToDateTime(b.createdAt);
        // var totalBillAmount = calculateTotalPrice(b.orderItems);
        var buttonHtml = '';
        if(getRole() === 'supervisor') {
           if(b.isInvoiceCreated == true) {
                buttonHtml = '<button class="btn btn-outline-dark px-4 mx-2" disabled="disabled" data-toggle="tooltip" title="Edit"><i class="fa fa-edit fa-lg"></i></button>'
           } else {
                buttonHtml = '<button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Edit" onclick="displayEditOrderModal(' + b.id + ')"><i class="fa fa-edit fa-lg"></i></button>'
           }
        }
        buttonHtml += '<button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Details" onclick="displayOrderDetails(' + b.id + ')"><i class="fa fa-info fa-lg"></i></button>'
        buttonHtml += '<button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Download Invoice" onclick="downloadInvoice(' + b.id + ')"><i class="fa fa-file-invoice fa-lg"></i></button>'
            var row = '<tr>'
            + '<td>&nbsp;</td>'
            + '<td>' + orderDateStr + '</td>'
            + '<td class="text-right">' + numberWithCommas(b.billAmount.toFixed(2)) + '</td>'
            + '<td>' + buttonHtml + '</td>'
            + '</tr>';
        $tbody.append(row);
	}
}

function displayEditOrder(data) {
  // const orderId = data.id;
  // const orderItems = data.orderItems;

  //Display list of order items
  const $orderItemsTable = $("#edit-order-items-table");
  $orderItemsTable.find("tbody").empty();

  for (var i in data) {
    var e = data[i];
//    const orderItem = orderItems[i];
//    const barcode = orderItem.barcode;
//    const quantity = orderItem.quantity;
//    const sellingPrice = orderItem.sellingPrice;
//    const buttonHtml = `<button class="btn btn-outline-danger px-4 mx-2" data-toggle="tooltip"
//                         title="Delete item" onclick="deleteOrderItem(\'' + e.barcode + '\')">
//                           <i class="fa fa-trash fa-lg"></i>
//                         </button>`
//
//    const row = '<tr>'
//        + '<td>&nbsp;</td>'
//        + '<td>' + barcode + '</td>'
////        + '<td>' + quantity + '</td>'
//        + '<td><input type="number" class="form-control" name="quantity" id="inputQuantity" value="' + quantity + '" required /></td>'
//        + '<td><input type="number" class="form-control" name="sellingPrice" id="inputSellingPrice" value="' + sellingPrice.toFixed(2) + '" required /></td>'
////        + '<td>' + sellingPrice.toFixed(2) + '</td>'
//        + '<td>' + buttonHtml + '</td>'
//        + '</tr>';
//
//    $orderItemsTable.find("tbody").append(row);
    const row = `
                 <tr>
                   <td>&nbsp;</td>
                   <td class="barcode">${e.barcode}</td>
                   <td>
                     <input
                       id="order-item-${e.barcode}"
                       type="number"
                       class="form-control quantityData"
                       value="${e.quantity}"
                       onchange="onQuantityChanged('${e.barcode}')"
                       style="width:70%" min="1">
                   </td>
                   <td>
                        <input
                          id="order-item-sp-${e.barcode}"
                          type="number"
                          class="form-control sellingPriceData"
                          value="${e.sellingPrice}"
                          onchange="onSellingPriceChanged('${e.barcode}')"
                          style="width:70%" min="0" step="0.01">
                   </td>
                   <td>
                     <button type="button" onclick="deleteEditOrderItem('${e.barcode}')" data-toggle="tooltip"
                          data-placement="bottom" title="Delete" class="btn btn-outline-danger px-4 mx-2">
                          <i class="fa fa-trash fa-lg"></i>
                      </button>
                   </td>
                 </tr>
               `;
    $orderItemsTable.find("tbody").append(row);
  }
}

function displayEditOrderModal(id){
	var url = getOrderUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	    orderItems = data.orderItems;
        $("#order-edit-modal").modal("toggle");
        $("#order-edit-modal input[name=id]").val(id);
        populateBarcodeDropdown("#edit-order-item-form");
        displayEditOrder(orderItems);
	   },
	   error: handleAjaxError
	});	
}

function displayOrderDetails(id) {
  var url = getOrderUrl() + "/" + id;
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderDetailsInModal(data);
    },
    error: handleAjaxError,
  });
}

function displayOrderDetailsInModal(data) {
  const orderId = data.id;
  const orderItems = data.orderItems;
  const orderDateStr = convertTimeStampToDateTime(data.createdAt);
  
  $("#order-id").text(orderId);
  $("#order-date").text(orderDateStr);
  
  //Display list of order items
  var grandTotal = 0;
  const $orderItemsTable = $("#order-items-table");
  $orderItemsTable.find("tbody").empty();

  for (let i = 0; i < orderItems.length; i++) {
    const orderItem = orderItems[i];
    const productName = orderItem.productName;
    const quantity = orderItem.quantity;
    const sellingPrice = orderItem.sellingPrice;
    const totalPrice = quantity * sellingPrice;
    grandTotal += totalPrice;
    const totalPriceStr = totalPrice.toFixed(2);

    const row = '<tr>'
        + '<td>&nbsp;</td>'
        + '<td>' + productName + '</td>'
        + '<td>' + numberWithCommas(quantity) + '</td>'
        + '<td class="text-right">' + numberWithCommas(sellingPrice.toFixed(2)) + '</td>'
        + '<td class="text-right">' + numberWithCommas(totalPriceStr) + '</td>'
        + '</tr>';
    
    $orderItemsTable.find("tbody").append(row);
  }

  const grandTotalStr = grandTotal.toFixed(2);
  $('#grand-total').text(grandTotalStr);
  $("#order-details-modal").modal("toggle");
}

function displayAddModal() {
    orderItems = [];
    $('#order-item-table tbody tr').remove();
	$('#add-order-modal').modal('toggle');
	populateBarcodeDropdown("#order-item-form");
}

function onQuantityChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  if (index == -1) return;
  const newQuantity = $(`#order-item-${barcode}`).val();
  orderItems[index].quantity = Number.parseInt(newQuantity);
}

function onSellingPriceChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  if (index == -1) return;
  const newSellingPrice = $(`#order-item-sp-${barcode}`).val();
  orderItems[index].sellingPrice = Number.parseFloat(newSellingPrice);
}

function getCurrentEditOrderItem() {
  return {
    barcode: $('#inputEditBarcode').val(),
    quantity: Number.parseInt($('#inputEditQuantity').val()),
    sellingPrice: Number.parseFloat($('#inputEditSellingPrice').val()),
  };
}

//INITIALIZATION CODE
function init(){
    // $('#add-order-item').submit()
  $('#nav-order').addClass('active');
  $('#add-order').click(placeNewOrder);
  $('#order-item-form').submit(addOrderItem);
  $('#add-edit-order-item').click(addEditOrderItem);
	$('#add-order-button').click(displayAddModal);
	$('#update-order').click(updateOrder);
	$('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(getOrderList);

// Place Order
function placeNewOrder() {
    var ok = true;
    const data = orderItems.map((it) => {
      if(isNaN(it.quantity)) {
          $.notify.defaults( {clickToHide:true,autoHide:false} );
          $('.notifyjs-wrapper').trigger('notify-hide');
          $.notify("Quantity cannot be empty", "error");
          ok = false;
      }
      if(isNaN(it.sellingPrice)) {
          $.notify.defaults( {clickToHide:true,autoHide:false} );
          $('.notifyjs-wrapper').trigger('notify-hide');
          $.notify("Selling Price cannot be empty", "error");
          ok = false;
      }
      return {
        barcode: it.barcode,
        quantity: it.quantity,
        sellingPrice: it.sellingPrice
      };
    });
    if(!ok) return;
    const json = JSON.stringify(data);
    placeOrder(json);
  }
  
//BUTTON ACTIONS
function placeOrder(json) {
  //Set the values to update
  const url = getOrderUrl();

  $.ajax({
    url: url,
    type: 'POST',
    data: json,
    headers: {
      'Content-Type': 'application/json',
    },
    success: function(response) {
      $('#add-order-modal').modal('toggle');
      $('.notifyjs-wrapper').trigger('notify-hide');
      $.notify("Order successfully placed!", 'success');
      getOrderList();
    },
    error: handleAjaxError,
  });

  return false;
}

function disableOrderEdit(id) {
    //Set the values to update
    const url = getOrderUrl() + "/invoice/" + id;
//    url = `${url}/invoice/${id}`;

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
          getOrderList();
        },
        error: handleAjaxError,
    });
}

function downloadInvoice(id) {
    var req = new XMLHttpRequest();
    req.open("GET", `/pos/download/invoice/${id}`, true);
    req.responseType = "blob";
  
    req.onload = function (event) {
      var blob = req.response;
      $('.notifyjs-wrapper').trigger('notify-hide');
      $.notify("Invoice generated!", "success");
      var link=document.createElement('a');
      link.href=window.URL.createObjectURL(blob);
      link.download=`invoice${id}.pdf`;
      link.click();

      disableOrderEdit(id);
    };
  
    req.send();
}