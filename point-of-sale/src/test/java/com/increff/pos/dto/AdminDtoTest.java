package com.increff.pos.dto;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.UserService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.utils.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AdminDtoTest extends AbstractUnitTest {

    @Autowired
    private AdminApiDto adminApiDto;
    @Autowired
    private UserService userService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void addSupervisorTest() throws ApiException {
        UserForm userForm = TestUtils.getUserForm("zeeshan@increff.com","abcdef");
        adminApiDto.add(userForm);
        List<UserData> list = adminApiDto.getAll();
        List<UserPojo> pojos = userService.getAll();
        assertEquals(pojos.size(), list.size());
        assertEquals(pojos.get(0).getEmail(), list.get(0).getEmail());
        assertEquals("supervisor", list.get(0).getRole());
    }


    @Test
    public void addOperatorTest() throws ApiException {
        UserForm userForm = TestUtils.getUserForm("operator1@increff.com","xyz");
        adminApiDto.add(userForm);
        List<UserData> list = adminApiDto.getAll();
        List<UserPojo> pojos = userService.getAll();
        assertEquals(pojos.size(), list.size());
        assertEquals(pojos.get(0).getEmail(), list.get(0).getEmail());
        assertEquals("operator", list.get(0).getRole());
    }

    @Test
    public void invalidEmailTest() throws ApiException {
        UserForm userForm = TestUtils.getUserForm("admin", "abcd");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Invalid email!");
        adminApiDto.add(userForm);
    }

    @Test
    public void invalidPasswordTest() throws ApiException {
        UserForm userForm = TestUtils.getUserForm("admin@xyz.com", "");
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Password cannot be empty!");
        adminApiDto.add(userForm);
    }
}
