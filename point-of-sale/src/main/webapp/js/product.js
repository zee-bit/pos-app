
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function getBrandCategoryUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/brands";
}

function getRole(){
	var role = $("meta[name=role]").attr("content")
	return role;
}

//BUTTON ACTIONS
function addProduct(event){
    event.preventDefault();

	//Set the values to update
	var $form = $("#product-form");
	var brandCategory = $('#inputBrandCategory').val();
	var brandCategoryJson = extractNameAndCategory(brandCategory);
	
	$form.append('<input type="hidden" name="brandName" value="' + brandCategoryJson.brandName + '" /> ');
	$form.append('<input type="hidden" name="brandCategory" value="' + brandCategoryJson.brandCategory + '" /> ');

	var json = toJson($form);
	
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getProductList();
			$form.trigger("reset");
			$.notify("Product successfully added!", 'success');
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var brandCategory = $('#inputEditBrandCategory').val();
    var brandCategoryJson = extractNameAndCategory(brandCategory);

    $form.append('<input type="hidden" name="brandName" value="' + brandCategoryJson.brandName + '" /> ');
    $form.append('<input type="hidden" name="brandCategory" value="' + brandCategoryJson.brandCategory + '" /> ');
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();
	   		$.notify("Product successfully edited!", 'success');
	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#productFile')[0].files[0];
	// readFileData(file, readFileDataCallback);
	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "product");

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
		},
		error: function(res) {
			console.log("error: "+ res.responseText);
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
	var url = getProductUrl();

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


function addDataToBrandCategoryDropdown(data, formId) {
	var $brandCategory = $(`${formId} select[name=brandCategory]`);
	$brandCategory.empty();

	var brandDefaultOption = '<option value="">Select brand and category</option>';
    $brandCategory.append(brandDefaultOption);

	for (var i in data) {
	  var e = data[i];
	  var option =
		'<option value="' +
		e.brand + '~' + e.category +
		'">' +
		e.brand + '-' + e.category +
		"</option>";
	  $brandCategory.append(option);
	}
  }


function populateBrandCategoryDropDown(formType) {
	var url = getBrandCategoryUrl();
	$.ajax({
	  url: url,
	  type: "GET",
	  success: function (data) {
	    if(formType === 'add-form') {
		    addDataToBrandCategoryDropdown(data, "#product-form");
		} else if(formType === 'edit-form') {
		    addDataToBrandCategoryDropdown(data, "#product-edit-form");
        }
	  },
	  error: handleAjaxError,
	});
  }

//UI DISPLAY METHODS

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var b = data[i];
//		var buttonHtml = '<button onclick="deleteProduct(' + b.id + ')">delete</button>'
		var buttonHtml = ''
		if(getRole() === 'supervisor')
			buttonHtml = '<td><button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Edit" onclick="displayEditProduct(' + b.id + ')"><i class="fa fa-edit fa-lg"></i></button></td>'
		var row = '<tr>'
		+ '<td>&nbsp;</td>'
		+ '<td>' + b.barcode + '</td>'
		+ '<td>' + b.product + '</td>'
		+ '<td>' + b.brandName + '</td>'
		+ '<td>' + b.brandCategory + '</td>'
		+ '<td>' + b.price.toFixed(2) + '</td>'
		+ buttonHtml
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	populateBrandCategoryDropDown("edit-form");

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
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
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data) {
    var brandCategory = data.brandName + "-" + data.brandCategory;
	$("#product-edit-form input[name=product]").val(data.product);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form select[name=brandCategory] option:contains(" + brandCategory + ")").attr("selected", true);
	$("#product-edit-form input[name=price]").val(data.price);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function displayAddModal() {
	$('#add-product-modal').modal('toggle');
	populateBrandCategoryDropDown("add-form");
}


//INITIALIZATION CODE
function init(){
	$('#nav-product').addClass('active');
	$('#product-form').submit(addProduct);
	$('#add-product-button').click(displayAddModal);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getProductList);

