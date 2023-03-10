function getUserUrl() {
  const baseUrl = $('meta[name=baseUrl]').attr('content');
  return baseUrl + '/api/admin/users';
}

function getUserList() {
  const url = getUserUrl();
  makeAjaxCall(url, 'GET', {}, (res) => displayUserList(res));
}

//UI DISPLAY METHODS

function displayUserList(users) {
  const $tbody = $('#user-table').find('tbody');
  $tbody.empty();

  users.forEach((user) => {
    const row = `<tr>
		<td>&nbsp;</td>
		<td>${user.email}</td>
		<td>${user.role}</td>
	</tr>`;
    $tbody.append(row);
  });
}


//INITIALIZATION CODE
function init() {
  $('#refresh-data').click(getUserList);
  $('#nav-user').addClass('active');
}

$(document).ready(init);
$(document).ready(getUserList);
