
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

    makeAjaxCall(url, 'POST', json, (res) => {
        getInventoryList();
        $form.trigger("reset");
    });
}

function filterInventory() {
    var $form = $("#filter-inventory-form");
    var json = toJson($form);
    var url = getInventoryUrl() + "/search";

    makeAjaxCall(url, 'POST', json, (res) => displayInventoryList(res));
}

function updateInventory(event){
	//Get the ID
	event.preventDefault();
	var barcode = $("#inventory-edit-form input[name=barcode]").val();
	var url = getInventoryUrl();

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

    makeAjaxCall(url, 'PUT', json, (res) => {
        getInventoryList();
        $('#edit-inventory-modal').modal('toggle');
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("Successfully updated inventory!", 'success');
    });
}


function getInventoryList(){
	var url = getInventoryUrl();
	makeAjaxCall(url, 'GET', {}, (res) => displayInventoryList(res));
}


// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];
	if (!file) {
	    $.notify.defaults( {clickToHide:true,autoHide:false} );
	    $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify('No file selected', 'error');
        return;
    }

	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "inventory");

    makeAjaxCall(
        url, 'POST', data,
        (res) => {
            $("#rowCount").text(res.totalCount);
            $("#processCount").text(res.successCount);
            $("#errorCount").text(res.errorCount);
            if (res.errorCount > 0) {
                $('#download-errors').show();
            }
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Successfully updated inventory items!", 'success');
        },
        (res) => {
            $.notify.defaults( {clickToHide:true,autoHide:false} );
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify(res.responseJSON.message, 'error');
        }, {
            contentType: false,
            processData: false,
        });
}

function downloadErrors(){
	var url = "/pos/download/error";
	makeAjaxCall(url, 'GET', {}, (res) => writeFileData(res));
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
	makeAjaxCall(url, 'GET', {}, (res) => displayInventory(res));
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
	$('#filter-inventory').click(filterInventory);
	$('#inventory-edit-form').submit(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

