package org.luis.sainteclaires.admin.rest;

import java.util.List;

import org.luis.basic.domain.GenericServiceBuilder;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.Product;
import org.luis.sainteclaires.base.bean.service.AccountService;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
//@Secured("ROLE_ADMIN")
public class AdminRest {
	@RequestMapping("admin")
	public String login() {
		return "admin/login";
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String loginName, String password, ModelMap map) {
		// SimpleMessage sm = accountService.login(loginName, password);
		SimpleMessage sm = new SimpleMessage(SimpleMessageHead.OK);
		if (!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)) {
			map.put("errorMsg", sm.getHead().getRep_message());
			return "admin/login";
		}
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
		return "admin/categorys";
	}

	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(String loginName) {

		return "admin/login";
	}

	@RequestMapping("unsettledOrders")
	public String unsettledOrders(ModelMap map) {
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
		return "admin/unsettledOrders";
	}

	@RequestMapping("orders")
	public String orders(ModelMap map) {
		map.put("active", "orders");
		map.put("collapse", "order");
		return "admin/orders";
	}

	@RequestMapping("categorys")
	public String categorys(ModelMap map) {
		List<Category> list = ServiceFactory.getCategoryService().findAll();
		map.put("cats", list);
		map.put("active", "categorys");
		map.put("collapse", "category");
		return "admin/categorys";
	}

	@RequestMapping(value = "categoryAdd", method = RequestMethod.GET)
	public String categoryAdd(ModelMap map) {
		map.put("active", "categoryAdd");
		map.put("collapse", "category");
		return "admin/categoryItem";
	}

	@RequestMapping("products")
	public String products(ModelMap map) {
		List<Product> list = ServiceFactory.getProductService().findAll();
		map.put("products", list);
		map.put("active", "products");
		map.put("collapse", "product");
		return "admin/products";
	}

	@RequestMapping(value = "productAdd", method = RequestMethod.GET)
	public String productAdd(ModelMap map) {
		map.put("active", "productAdd");
		map.put("collapse", "product");
		return "admin/productItem";
	}

	// ////// 页面跳转END /////////

	// /业务
	@RequestMapping(value = "category/add", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage addCategory(Category category) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "category/edit", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage editCategory(Category category) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "category/delete", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteCategory(Long id) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "product/add", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage addProduct() {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "product/edit", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage editProduct(Product product) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "product/delete", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteProduct(Long id) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@Autowired
	private AccountService accountService;

}
