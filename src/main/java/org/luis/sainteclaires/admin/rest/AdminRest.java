package org.luis.sainteclaires.admin.rest;

import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sainteclaires.base.bean.Category;
import com.sainteclaires.base.bean.Product;
import com.sainteclaires.base.bean.service.AccountService;

@Controller
@RequestMapping("/auth/")
public class AdminRest {
	@RequestMapping(value = "admin", method = RequestMethod.GET)
	public String login() {
		return "admin/login";
	}
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String loginName, String password, ModelMap map) {
		SimpleMessage sm = accountService.login(loginName, password);
		if(sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)){
			map.put("errorMsg", sm.getHead().getRep_message());
			return "admin/login";
		}
		return "admin/main";
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(String loginName) {
		SimpleMessage sm = new SimpleMessage();

		return "admin/login";
	}
	
	@RequestMapping(value = "add/category", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage addCategory(Category category) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "edit/category", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage editCategory(Category category) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "delete/category", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteCategory(Long id) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "add/product", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage addProduct() {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "edit/product", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage editProduct(Product product) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "delete/product", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteProduct(Long id) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@Autowired
	private AccountService accountService;

}
