
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

function getRole(){
	var role = $("meta[name=role]").attr("content")
	return role;
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();
			$form.trigger("reset");
			$('#add-brand-modal').modal('toggle');
			$('.notifyjs-wrapper').trigger('notify-hide');
			$.notify("Brand successfully created!", 'success');
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();
	   		$('.notifyjs-wrapper').trigger('notify-hide');
	   		$.notify("Brand successfully edited!", 'success');
	   		$('#edit-brand-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}

function filterBrand() {
    var $form = $("#filter-brand-form");
    var json = toJson($form);
    var url = getBrandUrl() + "/search";

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            displayBrandList(response);
       },
       error: handleAjaxError
    });
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#brandFile')[0].files[0];
	if (!file) {
	    $('.notifyjs-wrapper').trigger('notify-hide');
	    $.notify('No file selected', 'error');
	    return;
	}
	// readFileData(file, readFileDataCallback);
	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "brand");

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
			$.notify("Successfully uploaded all valid brand-categories!", 'success');
		},
		error: function(res) {
			console.log(res.responseText);
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
	var url = getBrandUrl();

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

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var b = data[i];
//		var buttonHtml = '<button onclick="deleteBrand(' + b.id + ')">delete</button>'
		var buttonHtml = '';
		if(getRole() === "supervisor")
			buttonHtml = '<td><button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Edit" onclick="displayEditBrand(' + b.id + ')"><i class="fa fa-edit fa-lg"></i></button></td>'
		var row = '<tr>'
		+ '<td>&nbsp;</td>'
		+ '<td>' + b.brand + '</td>'
		+ '<td>' + b.category + '</td>'
		+ buttonHtml
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
 	$('#download-errors').hide();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function displayAddModal() {
	$('#add-brand-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
    $('#nav-brand').addClass('active');
	$('#brand-form').submit(addBrand);
	$('#add-brand-button').click(displayAddModal);
	$('#brand-edit-form').submit(updateBrand);
	$('#filter-brand').click(filterBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);

