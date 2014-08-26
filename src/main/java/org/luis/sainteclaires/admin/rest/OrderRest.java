package org.luis.sainteclaires.admin.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.DatatableBean;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.sainteclaires.base.bean.Order;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
public class OrderRest {
	private Logger logger = Logger.getLogger(OrderRest.class);
	/**
	 * 查询未发货订单
	 * 跳转到【未处理订单】页面
	 * @param map
	 * @return
	 */
	@RequestMapping("unsettledOrders")
	public String unsettledOrders(ModelMap map) {
//		FilterAttributes fa = FilterAttributes.blank().add("status", 1);
//		List<Order> orders = ServiceFactory.getOrderService().findByAttributes(
//				fa);
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
//		map.put("orders", orders);
		return "admin/unsettledOrders";
	}
	/**
	 * 查找所有【未处理的订单】
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "unsettledOrders/find", method = RequestMethod.GET)
	@ResponseBody
	public DatatableBean<Order> findUnsettleOrders(HttpServletRequest req , ModelMap map){
		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		FilterAttributes fa = FilterAttributes.blank().add("status", Order.STATUS_UNDEAL);
//		String sql = "select * from cust_order where status = " + Order.STATUS_UNDEAL + " limit " + start +","+length;
		//分页
		List<Order> orders = ServiceFactory.getOrderService().findPaginationByAttributes(fa, start, length);
//		List<Order> orders = (List<Order>)ServiceFactory.getOrderService().findBySql(sql);
		//查找得到所有
		int count = ServiceFactory.getOrderService().findCountByAttributes(fa);
		DatatableBean<Order> tb = new DatatableBean<Order>();
		tb.setData(orders);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(count);
		tb.setRecordsTotal(count);
		logger.debug("OrderRest 查找未处理数据成功!");
		return tb;
	}
	/**
	 * 处理【未处理的订单】
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "unsettledOrders/send", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<?> dealUnsettleOrders(HttpServletRequest req, ModelMap map){
		try {
			long id = 0;
			id = Long.parseLong(req.getParameter("id"));
			Order order = ServiceFactory.getOrderService().get(id);
			order.setStatus(Order.STATUS_SENDED);
			ServiceFactory.getOrderService().update(order);
			return new SimpleMessage<Object>(new SimpleMessageHead(SimpleMessageHead.REP_OK, "修改状态为已发送"));
		} catch (NumberFormatException e) {
			logger.debug(e.getMessage());
			return new SimpleMessage<Object>(new SimpleMessageHead(SimpleMessageHead.REP_SERVICE_ERROR, "修改状态为已发送"));
		}
		
	}

	/**
	 * 查询订单
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("orders")
	public String orders(ModelMap map) {
//		FilterAttributes fa = FilterAttributes.blank().add("status", 1);
//		List<Order> orders = ServiceFactory.getOrderService().findByAttributes(
//				fa);
		map.put("active", "orders");
		map.put("collapse", "order");
//		map.put("orders", orders);
		return "admin/orders";
	}
	/**
	 * 查找得到【所有的订单】
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping(value="orders/find", method = RequestMethod.POST)
	@ResponseBody
	public DatatableBean<Order> findOrders(String orderNo,String customerNo , HttpServletRequest req , ModelMap map){
//		JSONObject jo = JSONObject.fromObject(data);
//		String orderNo = jo.getString("orderNo");
//		String customerNo = jo.getString("customerNo");
//		int s = jo.getInt("start");
//		int l = jo.getInt("length");
//		int d = jo.getInt("draw");
		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		String orderNo1 = req.getParameter("orderNo");
		String customerNo1 = req.getParameter("customerNo");
		FilterAttributes fa = FilterAttributes.blank();
		if(orderNo != null && !orderNo.equals("")){
			fa.add("orderNo", orderNo);
		}
		if(customerNo != null && !customerNo.equals("")){
			fa.add("account", customerNo);
		}
		//分页
		List<Order> orders = (List<Order>)ServiceFactory.getOrderService().findPaginationByAttributes(fa, start, length);
		//查找得到所有
		int count = ServiceFactory.getOrderService().findCountByAttributes(fa);
		DatatableBean<Order> tb = new DatatableBean<Order>();
		tb.setData(orders);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(count);
		tb.setRecordsTotal(count);
		logger.debug("OrderRest 查找全部数据成功!");
		return tb;
	}
	/**
	 * 通过【订单号】和【客户号】查找得到订单
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping(value="orders/find/condition" , method = RequestMethod.GET)
	public DatatableBean<Order> findOrderByConditions(HttpServletRequest req , ModelMap map){
		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		String orderNo = req.getParameter("orderNo");
		String customerNo = req.getParameter("customerNo");
		FilterAttributes fa = FilterAttributes.blank().add("orderNo", orderNo).add("account",customerNo);
		//分页
		List<Order> orders = (List<Order>)ServiceFactory.getOrderService().findPaginationByAttributes(fa, start, length);
		//查找得到所有
		int count = ServiceFactory.getOrderService().findCountByAttributes(fa);
		DatatableBean<Order> tb = new DatatableBean<Order>();
		tb.setData(orders);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(count);
		tb.setRecordsTotal(count);
		logger.debug("OrderRest 查找全部数据成功!");
		return tb;
	}
}
