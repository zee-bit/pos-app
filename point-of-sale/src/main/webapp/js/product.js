
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

    makeAjaxCall(url, 'POST', json, (res) => {
        getProductList();
        $form.trigger("reset");
        $('#add-product-modal').modal('toggle');
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("Product successfully added!", 'success');
    });
}

function filterProduct() {
    var $form = $("#filter-product-form");
    var json = toJson($form);
    var url = getProductUrl() + "/search";

    makeAjaxCall(url, 'POST', json, (res) => displayProductList(res));
}

function updateProduct(event){
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

    makeAjaxCall(url, 'PUT', json, (res) => {
        getProductList();
        $('#edit-product-modal').modal('toggle');
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("Product successfully edited!", 'success');
    });

	return false;
}


function getProductList(){
	var url = getProductUrl();
	makeAjaxCall(url, 'GET', {}, (res) => displayProductList(res));
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#productFile')[0].files[0];
	if (!file) {
	    $.notify.defaults( {clickToHide:true,autoHide:false} );
	    $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify('No file selected', 'error');
        return;
    }
	// readFileData(file, readFileDataCallback);
	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "product");

    makeAjaxCall(
        url, 'POST', data,
        (res) => {
            $("#rowCount").text(res.totalCount);
            $("#processCount").text(res.successCount);
            $("#errorCount").text(res.errorCount);
            if (res.errorCount > 0) {
                $('#download-errors').show();
            }
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
	makeAjaxCall(url, 'GET', {}, (data) => {
	    if(formType === 'add-form') {
            addDataToBrandCategoryDropdown(data, "#product-form");
        } else if(formType === 'edit-form') {
            addDataToBrandCategoryDropdown(data, "#product-edit-form");
        }
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
		+ '<td class="text-right">' + numberWithCommas(b.price.toFixed(2)) + '</td>'
		+ buttonHtml
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	populateBrandCategoryDropDown("edit-form");

    makeAjaxCall(url, 'GET', {}, (res) => displayProduct(res));
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
 	$('#download-errors').hide();
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
	$('#filter-product').click(filterProduct);
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

