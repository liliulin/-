package com.mjitech.qa.test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import com.mjitech.qa.service.BaseService;
import com.mjitech.qa.util.DBConnection;
import junit.framework.Assert;
import net.sf.json.JSONObject;
/**
/**
 *订单支付流程（已取货）订单支付完成，可售库存-1；实际库存不变，货道中商品数量-1；
 *@author gangwang@mjitech.com
 *@date 2018-06-20
 * **/
public class AddSkuToCart {
	private static Logger logger = Logger.getLogger(AddSkuToCart.class);
	BaseService service = new BaseService();
	JSONObject json = JSONObject.fromObject("{}");
	
	String orderNumber = "" ;//订单号
	String takingNumber = "" ;
	String takeGoodsNumber = "" ;//取货码
	String outBatchSkuId="";//批次商品id
	String skus="";//获取批次商品id
	//可售
	int quantity = 0 ;
	//货道中商品数量
	int mt_new_sku_pass_real_quantity = 0 ;
	//实际库存值
	int real_quantity = 0 ;
	
	@BeforeTest
	public void beforerMethod() {
		String shwoQuantity = "select quantity from mt_inventory where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
		String show_mt_new_sku_pass_real_quantity = "select real_quantity from mt_inventory where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
		String show_real_quantity = "select real_quantity from mt_new_sku_pass where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
		DBConnection db = new DBConnection("test");
		ResultSet rs = null ;
		try {
			Statement  stmt  = db.conn.createStatement();
			String sql="" ;
			for(int i=1;i<=3;i++) {
				if(i==1){
					sql = shwoQuantity;
					rs = (ResultSet)stmt.executeQuery(sql);
					while(rs.next()) {
						quantity = rs.getInt("quantity");
						System.out.println("quantity="+quantity);
					}
				} else if(i==2) {
					sql = show_mt_new_sku_pass_real_quantity;
					rs = (ResultSet)stmt.executeQuery(sql);
					while(rs.next()) {
						mt_new_sku_pass_real_quantity = rs.getInt("real_quantity");
						System.out.println("mt_new_sku_pass_real_quantity"+mt_new_sku_pass_real_quantity);
					}					
				} else {
					sql = show_real_quantity;
					rs = (ResultSet)stmt.executeQuery(sql);
					while(rs.next()) {
						real_quantity = rs.getInt("real_quantity");
					}
					System.out.println("real_quantity="+real_quantity);			
					db.close();
				}
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.close();
		}
		
		db.close();
	}
	
	
	/**
	 *  获得当前购物车接口
	 *  @author gangwang
	 *  
	 * */
//	@Test
	public void get_cart() {
		String url = "http://test.mjitech.com/web/machine_api/get_cart.action" ;
//		String url = "http://www.mjitech.com/web/machine_api/get_cart.action" ;
		json.put("storeId", 17);
		try {
			JSONObject getCarResult = service.httppostCartReturnJson(url, service.postParameter(json));
			String is_succ = getCarResult.getString("is_succ");
			Assert.assertEquals(is_succ,"true");
			logger.info("getCarResult is"+getCarResult);
			System.out.println("getCarResult is:"+getCarResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	//String S0489317067
	//@Test
	public void get_sku_detail() {
		String url = "http://test.mjitech.com/web/machine_api/get_sku_detail.action" ;
//		String url = "http://localhost:8080/maxbox/web/machine_api/get_sku_detail.action" ;
		json.put("storeId", "17");
		json.put("skuNumber", "582");
		try {
			JSONObject get_sku_detail_result = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("getSkuDetail result is:"+get_sku_detail_result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 02)机器端将商品添加到购物车
	 * **/
    @Test
	public void add_sku_to_cart() {
//		String url = "http://www.mjitech.com/web/machine_api/add_sku_to_cart.action" ;
		String url = "http://test.mjitech.com/web/machine_api/add_sku_to_cart.action" ;
		json.put("storeId","17");//门店号
		json.put("skuId", "679");//商品SKU
		json.put("count", "1") ;
		try {
			JSONObject add_sku_to_cart_result = service.httppostCartReturnJson(url,service.postParameter(json));
			//{"currentCount":1,"is_succ":true}
			String is_succ = add_sku_to_cart_result.getString("is_succ");
			Assert.assertEquals(is_succ,"true");
			System.out.println("add_sku_to_cart result is:"+add_sku_to_cart_result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//http://localhost:8080/maxbox/web/machine_api/submit_cart.action
	//{"is_succ":true,"storeId":12,"order":{"newTotalPrice":0,"leftPrice":0.2,"skus":[],"orderNumber":"S0857802930","originalPrice":20,"totalPrice":20,"payTime":"","cancelUserId":0,"source":2,"buyerId":0,"type":0,"wxpayUrl":"","takenUserId":0,"sellerId":0,"refundOrderNumber":"","statusName":"已下单","takeGoodsNumber":"","from":0,"id":14247,"sellTime":1528336962205,"originalSellOrderId":0,"isParent":2,"parentId":0,"initialOrderId":0,"wxprepayId":"","expireTime":1528337862205,"warehouseId":12,"cancelTime":"","payStatusName":"未付款","to":0,"payStatus":1,"refundUserId":0,"realPrice":0,"status":1}}
	@Test(dependsOnMethods = "add_sku_to_cart")
	public void submitCart() {
//		String url = "http://www.mjitech.com/web/machine_api/submit_cart.action" ;
		String url = "http://test.mjitech.com/web/machine_api/submit_cart.action" ;
		json.put("storeId","17");
		try {
			JSONObject submitResult = service.httppostCartReturnJson(url, service.postParameter(json));
			JSONObject order = submitResult.getJSONObject("order");
			orderNumber  = order.getString("orderNumber");
			System.out.println("orderNumber:"+orderNumber);
			System.out.println("submitResult is "+submitResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取支付URL
	 * */
	@Test(dependsOnMethods = "submitCart")
	public void getPayUrl() {
//		String url = "http://www.mjitech.com/web/machine_api/get_pay_url.action" ;
		String url = "http://test.mjitech.com/web/machine_api/get_pay_url.action" ;
		json.put("storeId","17") ;
		json.put("orderNumber",orderNumber);
		try {
			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("获取二维码连接："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付回调接JSONObject口。
	 * */
	@Test(dependsOnMethods = "getPayUrl")
	public void wxpay_callback_test() {
//		String url="http://www.mjitech.com/web/weixinpay_callback_test.action" ;
		String url="http://test.mjitech.com/web/weixinpay_callback_test.action" ;
		String body = "{\"return_code\":\"SUCCESSTEST\",\"openid\": \"oj4sH0qtPm0x0-ggPk0AQZGQR9xs\",\"out_trade_no\":\""+orderNumber+"\"}";
		try {
			JSONObject  result  = service.httppostPayCall(url, body);			
			System.out.println("支付结果："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	//接口地址: http://test.mjitech.com/web/machine_api/get_order_detail.action
	/**
	 * 获取订单详情接口
	 * */
	@Test(dependsOnMethods = "wxpay_callback_test")
	public void get_order_detail() {
//		String url = "http://www.mjitech.com/web/machine_api/get_order_detail.action" ;
		String url = "http://test.mjitech.com/web/machine_api/get_order_detail.action" ;
		json.put("storeId","17") ;
		json.put("orderNumber",orderNumber);
		try {
			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
			String is_succ = result.getString("is_succ");
			Assert.assertEquals("true", is_succ);
			JSONObject order = result.getJSONObject("order");
			if(is_succ.equals("true")) {
				if(order!=null) {
					takeGoodsNumber = order.getString("takeGoodsNumber");
				}		
			}		
			System.out.println("获取订单详情："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	/**
	 * 扫码取货
	 * 
	 * */
	@Test(dependsOnMethods="get_order_detail")
	public void get_order_detail_by_takingnumber() {
//		String url = "http://www.mjitech.com/web/machine_api/get_order_detail_by_takingnumber.action" ;
		String url = "http://test.mjitech.com/web/machine_api/get_order_detail_by_takingnumber.action" ;
	//	String body = "{\"storeId\":\"15\",\"takingNumber\":\""+takeGoodsNumber+"\"}";
		json.put("storeId", "17");
		json.put("takingNumber", takeGoodsNumber) ;
		try {
			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
			String is_succ = result.getString("is_succ");
			Assert.assertEquals("true", is_succ);

			JSONObject order = result.getJSONObject("order");
//			JSONArray batches= order.getJSONArray("batches");
		
//			String batches =order.getString("batches");   
	
			
			
//				if (is_succ.equals("true")) {getString
//					if (order != null) {
//					 outBatchSkuId=order.getString("batches");
//					}
//			}
//				System.out.println("扫码取货结果1："+batches);
//				System.out.println("扫码取货结果2："+skus);
		      System.out.println("扫码取货结果："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
//	/**
//	 * 更新outbatchsku（批次商品id）状态
//	  */
//	@Test(dependsOnMethods = "get_order_detail_by_takingnumber")
//	public void set_outbatchsku_status() {
//		String url = "http://test.mjitech.com/web/machine_api/set_outbatchsku_status.action";
//		json.put("storeId", "17");
//		json.put("outBatchSkuId", outBatchSkuId);
//		// 1未出 2正在出- 3已出- 4已出货- 5出货失败退款中 6出货失败退款成功- 7出货失败退款失败 8出货失败换货-
//		json.put("status", "2");
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("更新outbatchsku状态：" + result);    
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	
	
	
	
	
	
//	/**
//	 * 更新订单接口
//	 * 备注：该接口设置的就是4，和5
//	 * 1, "初始订单" 3, "未取货" 4, "正在取货" 5, "已取货"6, "取货失败"8, "已退款(全部退款)"9, "退款失败"10, "部分退款" 91, "已取消"
//	 * */
//	@Test(dependsOnMethods="get_order_detail_by_takingnumber")
//	public void update_order_status() {
////		String url = "http://www.mjitech.com/web/machine_api/update_order_status.action";
//		String url = "http://test.mjitech.com/web/machine_api/update_order_status.action";
//		json.put("storeId", "17");
//		json.put("orderNumber", orderNumber) ;
//		//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
//		json.put("status", "5");	  
//		try {
//		  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//		  System.out.println("更新订单："+result);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	/**
//	 * 更新每个订单的状态
//	 * */
////	@Test(dependsOnMethods="update_order_status")
//	public void set_outbatchsku_status() {
//		String url  = "http://test.mjitech.com/web/machine_api/set_outbatchsku_status.action" ;
//		json.put("storeId","17") ;
//		json.put("outBatchSkuId","");
//		json.put("status", "2");
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("批量更新接口："+result);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	//@Test(dependsOnMethods="")
//	public void upload_error() {
//		String url = "http://test.mjitech.com/web/machine_api/upload_error.action" ;
//		json.put("shouldRefund","true");
//		json.put("outBatchId", "");
//		json.put("storeId","15") ;
//		json.put("errorType","13");
//		json.put("errorInfo", "");		
//	}
//	
//	
//	@AfterTest()
//	public void end() {
//		int quantity_actual=0;
//		int mt_new_sku_pass_real_quantity_actual=0;
//		int real_quantity_actual=0;
//		System.out.println("AfterTest.....................................................");
//		String shwoQuantity = "select quantity from mt_inventory where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
//		String show_mt_new_sku_pass_real_quantity = "select real_quantity from mt_inventory where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
//		String show_real_quantity = "select real_quantity from mt_new_sku_pass where warehouse_id=17 and sku_id=679 ORDER BY id desc" ;
//		DBConnection db = new DBConnection("online");
//		ResultSet rs = null ;
//		try {
//			Statement  stmt  = db.conn.createStatement();
//			String sql="" ;
//			for(int i=1;i<=3;i++) {
//				if(i==1){
//					sql = shwoQuantity;
//					rs = (ResultSet)stmt.executeQuery(sql);
//					while(rs.next()) {
//					    quantity_actual = rs.getInt("quantity");
//						System.out.println("quantity="+quantity_actual);
//					}
//				} else if(i==2) {
//					sql = show_mt_new_sku_pass_real_quantity;
//					rs = (ResultSet)stmt.executeQuery(sql);
//					while(rs.next()) {
//						mt_new_sku_pass_real_quantity_actual = rs.getInt("real_quantity");
//						System.out.println("mt_new_sku_pass_real_quantity"+mt_new_sku_pass_real_quantity_actual);
//					}					
//				} else {
//					sql = show_real_quantity;
//					rs = (ResultSet)stmt.executeQuery(sql);
//					while(rs.next()) {
//						real_quantity_actual = rs.getInt("real_quantity");
//					}
//					System.out.println("real_quantity="+real_quantity_actual);			
//					db.close();
//				}
//			}		
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			db.close();
//		}
//		
//		db.close();
//		Assert.assertEquals(mt_new_sku_pass_real_quantity-1, mt_new_sku_pass_real_quantity_actual);//断言货道中商品数量
//		Assert.assertEquals(real_quantity-1, real_quantity_actual);//断言实际库存
//		Assert.assertEquals(quantity-1, quantity_actual);//断言可售库存
//	}
//	
		
	
}
