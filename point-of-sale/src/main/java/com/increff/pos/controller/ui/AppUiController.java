package com.increff.pos.controller.ui;

import com.increff.pos.controller.ui.AbstractUiController;
import com.increff.pos.model.data.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {

	@Autowired
	private InfoData infoData;

	@RequestMapping(value = "")
	public ModelAndView index() {
		if (!infoData.getEmail().isEmpty()) {
			return new ModelAndView("redirect:/ui/home");
		}
		infoData.setMessage("");
		return mav("index.html");
	}

	@RequestMapping(value = "/site/login")
	public ModelAndView login() {
		if(!infoData.getEmail().isEmpty()) {
			return new ModelAndView("redirect:/ui/home");
		}
		infoData.setMessage("");
		return mav("login.html");
	}

	@RequestMapping(value = "/ui/home")
	public ModelAndView home() {
		return mav("home.html");
	}

	@RequestMapping(value = "/ui/brand")
	public ModelAndView brand() {
		return mav("brand.html");
	}

	@RequestMapping(value = "/ui/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/ui/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}

	@RequestMapping(value = "/ui/order")
	public ModelAndView order() {
		return mav("order.html");
	}

	@RequestMapping(value = "/ui/users")
	public ModelAndView user() {
		return mav("user.html");
	}

	@RequestMapping(value = "/ui/reports")
	public ModelAndView reports() {
		return mav("reports.html");
	}

	@RequestMapping(value = "/ui/reports/inventory")
	public ModelAndView inventoryReport() {
		return mav("inventoryReport.html");
	}

	@RequestMapping(value = "/ui/reports/brand")
	public ModelAndView brandReport() {
		return mav("brandReport.html");
	}

	@RequestMapping(value = "/ui/reports/sales")
	public ModelAndView salesReport() {
		return mav("salesReport.html");
	}

	@RequestMapping(value = "/ui/reports/daily-sales")
	public ModelAndView dailySalesReport() {
		return mav("dailySales.html");
	}
}
