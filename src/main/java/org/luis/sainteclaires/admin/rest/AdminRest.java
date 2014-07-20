package org.luis.sainteclaires.admin.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.bean.Account;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.Order;
import org.luis.sainteclaires.base.bean.Product;
import org.luis.sainteclaires.base.bean.service.AccountService;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.luis.sainteclaires.base.util.BaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
// @Secured("ROLE_ADMIN")
public class AdminRest {
	@RequestMapping("admin")
	public String login() {
		return "admin/login";
	}
 
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String loginName, String password, ModelMap map,
			HttpServletRequest req) {
		SimpleMessage<Account> sm = BaseUtil.getAccountService().login(loginName, password);
		if (!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)) {
			map.put("errorMsg", sm.getHead().getRep_message());
			return "admin/login";
		}
		req.getSession().setAttribute(INameSpace.KEY_SESSION_ADMIN, sm.getItem());
		map.put("account", sm.getItem().getLoginName());
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
		return "admin/categorys";
	}

	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(HttpServletRequest req) {
		req.getSession().removeAttribute(INameSpace.KEY_SESSION_ADMIN);
		return "admin/login";
	}

	/**
	 * 查询未发货订单
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("unsettledOrders")
	public String unsettledOrders(ModelMap map) {
		FilterAttributes fa = FilterAttributes.blank().add("status", 1);
		List<Order> orders = ServiceFactory.getOrderService().findByAttributes(
				fa);
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
		map.put("orders", orders);
		return "admin/unsettledOrders";
	}

	/**
	 * 查询订单
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("orders")
	public String orders(ModelMap map) {
		FilterAttributes fa = FilterAttributes.blank().add("status", 1);
		List<Order> orders = ServiceFactory.getOrderService().findByAttributes(
				fa);
		map.put("active", "orders");
		map.put("collapse", "order");
		map.put("orders", orders);
		return "admin/orders";
	}

	@RequestMapping("categorys")
	public String categorys(ModelMap map) {
		setCats(map);
		return "admin/categorys";
	}

	@RequestMapping(value = "category/find", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> findCates() {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		List<Category> list = ServiceFactory.getCategoryService().findAll();
		for (Category category : list) {
			sm.addRecord(category);
		}
		return sm;
	}

	@RequestMapping(value = "parentCats/find", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> findParentCates() {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		List<Category> list = BaseUtil.getParentCates();
		for (Category category : list) {
			sm.addRecord(category);
		}
		return sm;
	}

	// @RequestMapping(value = "categoryAdd", method = RequestMethod.GET)
	// public String categoryAdd(ModelMap map) {
	// setCats(map);
	// return "admin/categoryItem";
	// }

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
		List<Category> parents = BaseUtil.getParentCates();
		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		map.put("active", "productAdd");
		map.put("collapse", "product");
		map.put("parents", parents);
		map.put("subcatMap", subcatMap);
		return "admin/productItem";
	}

	// ////// 页面跳转END /////////

	// /业务
	@RequestMapping(value = "category/save", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> saveCategory(Category category, ModelMap map) {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		boolean b = ServiceFactory.getCategoryService().save(category);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存错误");
			return sm;
		} else {
			cleanCategory(category);
			sm.setItem(category);
		}
		return sm;
	}

	@RequestMapping(value = "category/delete/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<Category> deleteCategory(@PathVariable("id") Long id) {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		boolean b = ServiceFactory.getCategoryService().deleteById(id);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存出错");
		} else {
			BaseUtil.clearParentCats();
			BaseUtil.clearAllSubCats();
			sm.getHead().setRep_code("200");
			sm.getHead().setRep_message("删除成功");
		}
		return sm;
	}

	@RequestMapping(value = "product/add", method = RequestMethod.POST)
	public String addProduct(Product product, ModelMap map) {
		boolean b = ServiceFactory.getProductService().save(product);
		if (!b) {
			map.put("error", "保存出错");
		}
		map.put("active", "products");
		map.put("collapse", "product");
		return "admin/products";
	}

	@RequestMapping(value = "product/edit", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage editProduct(Product product) {
		SimpleMessage sm = new SimpleMessage();
		boolean b = ServiceFactory.getProductService().update(product);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存出错");
		}
		return sm;
	}

	@RequestMapping(value = "product/delete", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteProduct(Long id) {
		SimpleMessage sm = new SimpleMessage();
		boolean b = ServiceFactory.getProductService().deleteById(id);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存出错");
		} else {
			sm.getHead().setRep_code("200");
			sm.getHead().setRep_message("删除成功");
		}
		return sm;
	}

	private void cleanCategory(Category category) {
		BaseUtil.clearParentCats();
		BaseUtil.clearAllSubCats();
	}

	private void setCats(ModelMap map) {
		// List<Category> list = ServiceFactory.getCategoryService().findAll();
		// map.put("cats", list);
		// map.put("parents", BaseUtil.getParentCates());
		map.put("active", "categorys");
		map.put("collapse", "category");
	}

	@SuppressWarnings("unused")
	private void setOrders(ModelMap map) {
		map.put("active", "orders");
		map.put("collapse", "order");
	}

	@Autowired
	private AccountService accountService;

}
