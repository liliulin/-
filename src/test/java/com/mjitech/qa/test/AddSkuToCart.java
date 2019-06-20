package com.mjitech.qa.test;

import static org.testng.Assert.assertEquals;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mjitech.qa.service.BaseService;
import com.mjitech.qa.util.DBConnection;
import junit.framework.Assert;
import net.sf.json.JSONObject;
/**
 *订单支付流程（已取货）订单支付完成，可售库存-1；实际库存不变，货道中商品数量-1；

 *@date 2018-06-20
 * **/
public class AddSkuToCart {
	private static Logger logger = Logger.getLogger(AddSkuToCart.class);
	BaseService service = new BaseService();
	JSONObject json = JSONObject.fromObject("{}");
	
	String orderNumber = "" ;//订单号
	String takingNumber = "" ;
	String  takeGoodsNumber = "" ;//取货码
	String outBatchId_value="";//批次ID37434
	String skus="";//获取批次商品id
	String skuId="";//购买商品id
	String outBatchSkuId= "" ;
	//可售
	int quantity = 0 ;
	//货道中商品数量
	int mt_new_sku_pass_real_quantity = 0 ;
	//实际库存值
	int real_quantity = 0 ;
	
	Map map = new HashMap();
	
	@BeforeTest
	public void beforerMethod() {
		String shwoQuantity = "select quantity from mt_inventory where warehouse_id=17 and sku_id=582 ORDER BY id desc" ;
		String show_mt_new_sku_pass_real_quantity = "select real_quantity from mt_inventory where warehouse_id=17 and sku_id=582 ORDER BY id desc" ;
		String show_real_quantity = "select real_quantity from mt_new_sku_pass where warehouse_id=17 and sku_id=582 ORDER BY id desc" ;
		DBConnection db = new DBConnection("test");//测试数据库
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
	

////	/**
////	 * 1、获取主页数据01
////	 * */
	@Test
	public void get_mainpage_data() {
		String url = "http://test.mjitech.com/web/machine_api/get_mainpage_data.action" ;
		json.put("storeId", "17");
		try {
			JSONObject getMainpageDataResult = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("获取主页数据："+getMainpageDataResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	
	/**
	 * 2、商品详情
	 * */
	@Test(dependsOnMethods = "get_mainpage_data")
	public void get_sku_detail() {
		String url = "http://test.mjitech.com/web/machine_api/get_sku_detail.action?storeId=17&skuNumber=582" ;
		json.put("storeId", "17");
		json.put("skuNumber", "582");
		try {
			JSONObject getMainpageDataResult = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("商品详情："+getMainpageDataResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * 03)机器端将商品添加到购物车
	 * **/
   @Test(dependsOnMethods = "get_sku_detail")
	public void add_sku_to_cart() {
		String url = "http://test.mjitech.com/web/machine_api/add_sku_to_cart.action" ;
		//String url = "http://test.mjitech.com/web/machine_api/add_sku_to_cart.action" ;
		json.put("storeId","17");//门店号
		json.put("skuId", "582");//商品SKU
		json.put("count", "2") ;
		try {
			JSONObject add_sku_to_cart_result = service.httppostCartReturnJson(url,service.postParameter(json));
			//{"currentCount":1,"is_succ":true}
			String is_succ = add_sku_to_cart_result.getString("is_succ");
			Assert.assertEquals(is_succ,"true");
			System.out.println("add_sku_to_cart result is添加到购物车结果:"+add_sku_to_cart_result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

   
	/**
	 * 4)从购物车中减少商品： 
	 * **/
 @Test(dependsOnMethods = "add_sku_to_cart")
	public void remove_sku_from_cart() {
		String url = "http://test.mjitech.com/web/machine_api/remove_sku_from_cart.action" ;
		//String url = "http://test.mjitech.com/web/machine_api/add_sku_to_cart.action" ;
		json.put("storeId","17");//门店号
		json.put("skuId", "582");//商品SKU
		json.put("count", "1") ;
		try {
			JSONObject add_sku_to_cart_result = service.httppostCartReturnJson(url,service.postParameter(json));
			//{"currentCount":1,"is_succ":true}
			String is_succ = add_sku_to_cart_result.getString("is_succ");
			Assert.assertEquals(is_succ,"true");
			System.out.println("add_sku_to_cart result is从购物车中减少商品结果:"+add_sku_to_cart_result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   	
	/**
	 *  5、获得当前购物车接口  02
	 *  
	 *  
	 * */
	@Test(dependsOnMethods = "remove_sku_from_cart")
	public void get_cart() {
		//String url = "http://www.mjitech.com/web/machine_api/get_cart.action" ;
		String url = "http://test.mjitech.com/web/machine_api/get_cart.action" ;
		json.put("storeId", "17");
		try {
			JSONObject getCarResult = service.httppostCartReturnJson(url, service.postParameter(json));
			String is_succ = getCarResult.getString("is_succ");
			Assert.assertEquals(is_succ,"true");
			logger.info("getCarResult is"+getCarResult);
			System.out.println(" 获得当前购物车接口:"+getCarResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	
  /**
	 * 06)提交购物车非加价购页面提交订单
	 * **/
  
	@Test(dependsOnMethods = "get_cart")
	public void submit_cart_new() {
		String url = "https://test.mjitech.com/web/machine_api/submit_cart_new.action" ;
//		String token = map.get("");
		json.put("storeId","17");
		json.put("token",1234567890);
//		json.put("pageSource", 1);
		json.put("pageSource", 0);
		try {
			JSONObject submitResult = service.httppostCartReturnJson(url, service.postParameter(json));
			JSONObject order = submitResult.getJSONObject("order");
			orderNumber  = order.getString("orderNumber");
			System.out.println("订单号orderNumber:"+orderNumber);
			System.out.println("new提交订单结果是submitResult is "+submitResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 *7、获取支付URL
	 * */
	@Test(dependsOnMethods = "submit_cart_new")
	public void getPayUrl() {
		String url = "https://test.mjitech.com/web/machine_api/get_pay_url.action" ;
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
	 * 8支付回调接JSONObject口。
	 * */
	@Test(dependsOnMethods = "getPayUrl")
	public void wxpay_callback_test() {
		String url="https://test.mjitech.com/web/weixinpay_callback_test.action" ;
		String body = "{\"return_code\":\"SUCCESSTEST\",\"openid\": \"oj4sH0qtPm0x0-ggPk0AQZGQR9xs\",\"out_trade_no\":\""+orderNumber+"\"}";
		try {
			JSONObject  result  = service.httppostPayCall(url, body);			
			System.out.println("支付回调接JSONObject口："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * 9、获取订单详情
	 * 
	 * */
	@Test(dependsOnMethods = "wxpay_callback_test")
	public void get_order_detail() {
		String url = "http://test.mjitech.com/web/machine_api/get_order_detail.action" ;
		json.put("storeId", "17");
		json.put("orderNumber", orderNumber);
		try {
			JSONObject get_sku_detail_result = service.httppostCartReturnJson(url, service.postParameter(json));
			JSONObject order = get_sku_detail_result.getJSONObject("order");
			takeGoodsNumber  = order.getString("takeGoodsNumber");
			System.out.println("订单号takeGoodsNumber:"+takeGoodsNumber);
			System.out.println("获取订单详情:"+get_sku_detail_result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	
	



//	
//	/**
//	 * 下单屏  1、解锁库存
//	 * */	
//	
//	@Test
//	public void user_cancel_order() {
//		String url = "https://test.mjitech.com/maxbox_pc/machine_api/user_cancel_order.action" ;
//		json.put("storeId","17") ;
//		json.put("orderNumber",orderNumber);
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("解锁库存："+result);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	 
//	
	
//	/**
//	 *   下单屏  2清空购物车
//	 *  
//	 * */
//	@Test
//	public void clear_cart() {
//		//String url = "http://www.mjitech.com/web/machine_api/get_cart.action" ;
//		String url = "http://test.mjitech.com/web/machine_api/clear_cart.action" ;
//		json.put("storeId", "17");
//		try {
//			JSONObject getCarResult = service.httppostCartReturnJson(url, service.postParameter(json));
//			String is_succ = getCarResult.getString("is_succ");
//			Assert.assertEquals(is_succ,"true");
//			logger.info("getCarResult is"+getCarResult);
//			System.out.println(" 清空购物车:"+getCarResult);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}	
	
//	/**
//	 * 下单屏和7寸屏  1进入维护页面接口
//	 * 1 进入维护页面；2 进入可售页面 3 无此门店（和研发确定暂无此返回状态）
//	 * 备注：取货失败类接不进入维护模式。详见需求197
//	 * */
//	@Test
//	public void enter_maintenance_page() {
//		String url  = "http://test.mjitech.com/web/machine_api/enter_maintenance_page.action" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("进入维护页面接口："+result);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	/**
	 *  下单屏和7寸屏 2、断网与来网发送微信通知接口
	 * 网络状态：1来电来网 2停电断网
	 * 
	 * */
	@Test
	public void wx_push_network_notice() {
		String url  = "http://test.mjitech.com/web/machine_api/wx_push_network_notice.action" ;
		json.put("storeId","17") ;
		json.put("status", "1");
		try {
			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("断网与来网发送微信通知接口："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	
	/**
	 * 7寸屏    1、扫码取货
	 * 
	 * */
    @Test(dependsOnMethods="get_order_detail")
//    @Test
	public void get_order_detail_by_takingnumber() {
		String url = "http://test.mjitech.com/web/machine_api/get_order_detail_by_takingnumber.action" ;
	//	String body = "{\"storeId\":\"15\",\"takingNumber\":\""+takeGoodsNumber+"\"}";
		json.put("storeId", "17");
		json.put("takingNumber", takeGoodsNumber) ;
//		json.put("takingNumber", "9791638154") ;
		
		try {
		      JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
		      JSONObject order = result.getJSONObject("order");
		      String batches = order.getString("batches");
		         batches = batches.substring(batches.indexOf("{", 0), batches.length()-1);
		         JSONObject outBatchId = JSONObject.fromObject(batches);
		         String outBatchId_value = outBatchId.getString("outBatchId");
		         
		         String skus = outBatchId.getString("skus");
		         skus = skus.substring(skus.indexOf("{", 0), skus.length()-1);
		         JSONObject skuss = JSONObject.fromObject(skus);   //此三行代码是将不正确的json格式转化为正确的
		         String outBatchSkuId = skuss.getString("outBatchSkuId");
		         
		         String orderNumber= order.getString("orderNumber"); //获取取货码
		         String skuId= skuss.getString("skuId");//购买商品id
		         
		         
		      System.out.println("扫码取货结果："+result);
		      System.out.println("订单号orderNumber ："+orderNumber);
		      System.out.println("批次id=outBatchId_value："+outBatchId_value);
		      System.out.println("取货码takeGoodsNumber："+takeGoodsNumber);
		      System.out.println("skus："+skus);
		      System.out.println("outBatchId："+outBatchId);
		      System.out.println("购买商品skuId："+skuId);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

    /**
	 * 更新每个订单的状态
	 * */
	@Test(dependsOnMethods="get_order_detail_by_takingnumber")
	public void set_outbatchsku_status() {
		String url  = "http://test.mjitech.com/web/machine_api/set_outbatchsku_status.action" ;
		json.put("storeId","17") ;
		json.put("outBatchSkuId",outBatchSkuId);
		json.put("outBatchId",outBatchId_value);
		json.put("orderNumber",orderNumber );
		json.put("status", "2");//1未出 2正在出 3已出 4已出货
		try {
			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
			System.out.println("批量更新接口："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
	
	
	/**
	 * 更新订单接口
	 * 备注：该接口设置的就是4，和5
	 * 1, "初始订单" 3, "未取货" 4, "正在取货" 5, "已取货"6, "取货失败"8, "已退款(全部退款)"9, "退款失败"10, "部分退款" 91, "已取消"
	 * */
	@Test(dependsOnMethods="set_outbatchsku_status")
	public void update_order_status() {
		String url = "https://test.mjitech.com/web/machine_api/update_order_status.action";
		json.put("storeId", "17");
		json.put("orderNumber", orderNumber) ;
		//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
		json.put("status", "4");	  
		try {
		  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
		  System.out.println("更新订单："+result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	
	
	
	
	
/////**
////* 取货失败-生成损耗：
////* */
@Test(dependsOnMethods="update_order_status")
public void fail_get_sku() {
	String url = "https://test.mjitech.com/web/machine_api/fail_get_sku.action";
	json.put("storeId", "17");
//	json.put("orderNumber", outBatchSkuId) ;
	json.put("outBatchSkuId",outBatchSkuId);
	json.put("outBatchId",outBatchId_value);
	json.put("orderNumber",orderNumber );
	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
	json.put("status", "6");	  
	try {
	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
	  System.out.println("取货失败-生成损耗："+result);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}		


///**
//* 获取换货信息
//* */	
@Test(dependsOnMethods="fail_get_sku")
public void get_exchanged_sku() {
	String url = "https://test.mjitech.com/web/machine_api/get_exchanged_sku.action";
	json.put("storeId", "17");
	json.put("outBatchSkuId",outBatchSkuId);
	json.put("outBatchId",outBatchId_value);
	json.put("orderNumber",orderNumber );
	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
	json.put("skuId", skuId);	  
	try {
	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
	  System.out.println("获取换货信息："+result);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}	

//	
////	/**
////	 * 1、取失败选换货
////	 * */		
//	
//@Test(dependsOnMethods="get_exchanged_sku")
//public void exchange_sku() {
//	String url = "https://test.mjitech.com/web/machine_api/exchange_sku.action";
//	json.put("storeId", "17");
//	json.put("outBatchSkuId",outBatchSkuId);
//	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
//	json.put("skuId", skuId);	  
//	try {
//	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//	  System.out.println("取失败选换货："+result);
//	} catch (ClientProtocolException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}	
	
	
/**
* 2、取货失败-退款
* */		
@Test(dependsOnMethods="get_exchanged_sku")
public void refund_failed_get_sku() {
	String url = "https://test.mjitech.com/web/machine_api/refund_failed_get_sku.action";
	json.put("storeId", "17");
	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
	json.put("skuId", skuId);	  
	try {
	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
	  System.out.println("取货失败-退款："+result);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}	
	
	
/////**
////* 3、机器故障-退款，产损耗
////* */		
//	
//@Test(dependsOnMethods="get_exchanged_sku")
//public void upload_error() {
//	String url = "https://test.mjitech.com/web/machine_api/upload_error.action";
//	json.put("storeId", "17");
//	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
//	json.put("shouldRefund", "true");
//	json.put("outBatchId", outBatchId_value);
//	json.put("errorTyped", "0");
//	json.put("errorInfo", "test");
//
//	try {
//	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//	  System.out.println("机器故障-退款，产损耗："+result);
//	} catch (ClientProtocolException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}	
//
//	
/////**
////* 3-1、机器故障，微信端发送机器异常消息
////* */		
//		
//@Test(dependsOnMethods="upload_error")
//public void wx_push_error_detail() {
//	String url = "https://test.mjitech.com/web/machine_api/wx_push_error_detail.action";
//	json.put("storeId", "17");
//	//1新2已支付3未取4正在取5已取6取货失败7退款申请中8已退款9退款失败10部分退款，退款申请状态查阅RefundOrder状态21机器故障但未退款91已取消
//	json.put("errorInfo", "false");
//	json.put("status", "1");
//
//	try {
//	  JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//	  System.out.println("机器故障，微信端发送机器异常消息"+result);
//	} catch (ClientProtocolException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}	
	
	
	
	
	
	
	

	
	
	

	
//	/**
//	 * 65寸屏 1、获取门店ID的接口
//	 * 
//	 * */	
//	
//	@Test
//	public void getmyid() {
//		String url  = "http://test.mjitech.com/getmyid" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("获取门店ID的接口："+result);
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
//	 * 65寸屏 2、获取门店信息
//	 * 
//	 * */	
//	
//	@Test(dependsOnMethods="getmyid")
//	public void get_store_info() {
//		String url  = "http://test.mjitech.com/maxbox_pc/machine_api/get_store_info.action" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("获取门店信息："+result);
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
//	
//	/**
//	 * 65寸屏 3、65首页数据
//	 * 
//	 * */	
//	
//	@Test(dependsOnMethods="get_store_info")
//	public void media_screen_data_new() {
//		String url  = "http://test.mjitech.com/maxbox_pc/local_api/media_screen_data_new.action" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("65首页数据："+result);
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
//    
//	/**
//	 * 65寸屏 4、65获取机器状态以及取货状态
//	 * 
//	 * */	
//	@Test(dependsOnMethods="media_screen_data_new")
//	public void machinestatus() {
//		String url  = "http://test.mjitech.com/machinestatus" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("65获取机器状态以及取货状态："+result);
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
//	
//	
//	/**
//	 * 65寸屏 5、获取天气
//	 * 
//	 * */	
//	@Test(dependsOnMethods="machinestatus")
//	public void media_screen_weather_data() {
//		String url  = "http://test.mjitech.com/maxbox_pc/local_api/media_screen_weather_data.action" ;
//		json.put("storeId","17") ;
//		try {
//			JSONObject result = service.httppostCartReturnJson(url, service.postParameter(json));
//			System.out.println("获取天气："+result);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	
	
}
