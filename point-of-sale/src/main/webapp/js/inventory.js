
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getRole(){
	var role = $("meta[name=role]").attr("content")
	return role;
}

//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();
			$form.trigger("reset");
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var barcode = $("#inventory-edit-form input[name=barcode]").val();
	var url = getInventoryUrl();

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();
	   		$('.notifyjs-wrapper').trigger('notify-hide');
	   		$.notify("Successfully updated inventory!", 'success');
	   },
	   error: handleAjaxError
	});

	return false;
}


function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}


// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];
	if (!file) {
	    $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify('No file selected', 'error');
        return;
    }
	// readFileData(file, readFileDataCallback);
	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "inventory");

	$.ajax({
		url: url,
		type: 'POST',
		data: data,
		contentType: false,
		processData: false,
		success: function(res) {
			$("#rowCount").text(res.totalCount);
            $("#processCount").text(res.successCount);
            $("#errorCount").text(res.errorCount);
            if (res.errorCount > 0) {
                $('#download-errors').show();
            }
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Successfully updated inventory items!", 'success');
		},
		error: function(res) {
			console.log("error: "+ res.responseText);
			$('.notifyjs-wrapper').trigger('notify-hide');
			$.notify(res.responseJSON.message, 'error');
		}
	})
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getInventoryUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();  
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	var url = "/pos/download/error";
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            writeFileData(data);
       },
       error: handleAjaxError
    });
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var b = data[i];
//		var buttonHtml = '<button onclick="deleteInventory(' + b.id + ')">delete</button>'
		var buttonHtml = '';
		if(getRole() === "supervisor")
			var buttonHtml = '<td><button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Edit" onclick="displayEditInventory(\'' + b.barcode + '\')"><i class="fa fa-edit fa-lg"></i></button></td>'
		var row = '<tr>'
		+ '<td>' + b.product + '</td>'
		+ '<td>' + b.barcode + '</td>'
		+ '<td>' + numberWithCommas(b.quantity) + '</td>'
		+ buttonHtml
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditInventory(barcode){
	var url = getInventoryUrl() + "/" + barcode;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
 	$('#download-errors').hide();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#inventory-edit-form input[name=product]").val(data.product);
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#nav-inventory').addClass('active');
	$('#add-inventory').click(addInventory);
	$('#inventory-edit-form').submit(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

