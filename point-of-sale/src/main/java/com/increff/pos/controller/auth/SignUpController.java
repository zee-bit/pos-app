package com.increff.pos.controller.auth;


import com.increff.pos.controller.ui.AbstractUiController;
import com.increff.pos.dto.AdminApiDto;
import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import io.swagger.annotations.ApiOperation;

@Controller
public class SignUpController extends AbstractUiController {

	@Autowired
	private AdminApiDto dto;
	@Autowired
	private InfoData info;

	@ApiOperation(value = "Display sign-up page")
	@RequestMapping(path = "/site/signup", method = RequestMethod.GET)
	public ModelAndView signUpPage() throws ApiException {
		if (!info.getEmail().isEmpty()) {
			return new ModelAndView("redirect:/ui/home");
		}
		info.setMessage("");
		return mav("signup.html");
	}

	@ApiOperation(value = "Registers a user in the application")
	@RequestMapping(path = "/site/signup", method = RequestMethod.POST)
	public ModelAndView signUpUser(UserForm form) throws ApiException {
		try {
			dto.add(form);
		} catch (ApiException ex) {
			info.setMessage(ex.getMessage());
			return mav("signup.html");
		}
		info.setMessage("");
		return new ModelAndView("redirect:/site/login");
	}
}
