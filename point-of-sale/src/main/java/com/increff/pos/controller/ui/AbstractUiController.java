package com.increff.pos.controller.ui;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.model.data.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class AbstractUiController {

	@Autowired
	private InfoData info;

	@Value("${app.baseUrl}")
	private String baseUrl;

	protected ModelAndView mav(String page) {

		// Get current user
		UserPrincipal principal = SecurityUtil.getPrincipal();

		String email = principal == null ? "" : principal.getEmail();
		info.setEmail(email);

		String role = getRole();
		info.setRole(role);

		// Set info
		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("baseUrl", baseUrl);
		return mav;
	}

	private static String getRole() {
		Authentication auth = SecurityUtil.getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "";
		}

		boolean isSupervisor = auth.getAuthorities()
				.stream()
				.anyMatch(it -> it.getAuthority().equalsIgnoreCase("supervisor"));

		return isSupervisor ? "supervisor" : "operator";
	}
}
