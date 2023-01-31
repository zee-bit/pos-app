function checkPassword(form) {
  password1 = form.password.value;
  password2 = form.confirmPassword.value;

  // If password not entered
  if (password1 == '')
     $.notify("Please enter Password", "error");

  // If confirm password not entered
  else if (password2 == '')
     $.notify("Please enter confirm password", "error");

  // If Not same return False.
  else if (password1 != password2) {
     $.notify("Password did not match: Please try again...", "error");
     return false;
  }

  // If same return True.
  return true;
}