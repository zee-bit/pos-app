package com.increff.pos.dto;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.UserService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminApiDto {

    @Autowired
    private UserService service;

    public void add(UserForm userForm) throws ApiException {
        validateForm(userForm);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userForm.setPassword(encoder.encode(userForm.getPassword()));
        userForm.setConfirmPassword(encoder.encode(userForm.getConfirmPassword()));
        UserPojo user = ConversionUtil.getUserPojoFromForm(userForm);
        service.add(user);
    }

    private void validateForm(UserForm userForm) throws ApiException {
        if (!StringUtil.isValidEmail(userForm.getEmail()))
            throw new ApiException("Email is invalid. Please input a valid email!");
        if (StringUtil.isEmpty(userForm.getPassword()))
            throw new ApiException("Password cannot be empty!");
        if (StringUtil.isEmpty(userForm.getConfirmPassword()))
            throw new ApiException("Confirm password cannot be empty!");
        if (!userForm.getPassword().equals(userForm.getConfirmPassword()))
            throw new ApiException("Password does not match!");
    }

    public List<UserData> getAll() {
        List<UserPojo> userPojoList = service.getAll();
        List<UserData> userDataList = new ArrayList<>();
        for (UserPojo userPojo : userPojoList) {
            UserData userData = ConversionUtil.getUserDataFromPojo(userPojo);
            userDataList.add(userData);
        }
        return userDataList;
    }
}
