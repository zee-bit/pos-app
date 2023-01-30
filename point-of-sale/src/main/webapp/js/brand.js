
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
	event.preventDefault();
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();

    makeAjaxCall(url, 'POST', json, (res) => addBrandCallback());
	$form.trigger("reset");
}

function addBrandCallback() {
    getBrandList();
    $('#add-brand-modal').modal('toggle');
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify("Brand successfully created!", 'success');
}

function updateBrand(event){
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

    makeAjaxCall(url, 'PUT', json, (res) => updateBrandCallback(res));
//	makeAjaxCallWithData(url, 'PUT', json, updateBrandCallback, handleAjaxError);
}

function updateBrandCallback(data) {
    getBrandList();
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify("Brand successfully edited!", 'success');
    $('#edit-brand-modal').modal('toggle');
}

function getBrandList(){
	var url = getBrandUrl();
//	makeAjaxCall(url, displayBrandList);
    makeAjaxCall(url, 'GET', {}, (res) => displayBrandList(res));
}

function filterBrand() {
    var $form = $("#brand-form");
    var json = toJson($form);
    var url = getBrandUrl() + "/search";

    makeAjaxCall(url, 'POST', json, (res) => displayBrandList(res));
//    makeAjaxCallWithData(url, 'POST', json, displayBrandList);
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

	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "brand");

    makeAjaxCall(
        url, 'POST', data,
        (res) => processDataSuccessCallback(res),
        (err) => processDataErrorCallback(err),
        {
            contentType: false,
            processData: false
        });
//    makeAjaxCallWithData(
//        url, 'POST', data,
//        processDataSuccessCallback,
//        processDataErrorCallback,
//        {
//            contentType: false,
//            processData: false
//        }
//    )
}

function processDataSuccessCallback(res) {
    $("#rowCount").text(res.totalCount);
    $("#processCount").text(res.successCount);
    $("#errorCount").text(res.errorCount);
    if (res.errorCount > 0) {
        $('#download-errors').show();
    }
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify("Successfully uploaded all valid brand-categories!", 'success');
}

function processDataErrorCallback(error) {
    console.log(error.responseText);
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify(error.responseJSON.message, 'error');
}

function downloadErrors(){
	var url = "/pos/download/error";
	makeAjaxCall(url, writeFileData);
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
	makeAjaxCall(url, 'GET', {}, (res) => displayBrand(res));
//	makeAjaxCall(url, displayBrand);
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


//INITIALIZATION CODE
function init(){
    $('#nav-brand').addClass('active');
	$('#brand-form').submit(addBrand);
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

