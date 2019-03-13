package com.mjitech.qa.service;

/**
 * 
 * 自动化测试报告模版
 * @author android
 * @date 2018-05-16
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.mjitech.qa.util.DateUtil;

public class YrdHtmlReport {

	String date = DateUtil.formatDate(DateUtil.getCurrentDate(), "yyMMdd");
	static String fileName = null;
	FileWriter fw = null;

	// 生成报告名字
	public String reportName(String caseClassName) {
		return "D:\\yrdReport\\" + date + "\\" + caseClassName + ".html";
	}

	// 创建报告的前半部分
	public void createHead(String caseClassName) {
		try {
			// 获取报告名字
			fileName = reportName(caseClassName);
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			fw = new FileWriter(fileName);
			fw.write("<!DOCTYPE html>");
			fw.write("<html>");
			fw.write("<head>");
			fw.write("<meta charset=\"UTF-8\">");
			fw.write("<title>" + caseClassName + " reprot</title>");
			fw.write("<style type=\"text/css\">");
			fw.write("*{ padding:0px;margin:0px;}" + "body{color:#333;}ul,li{list-style:none;}"
					+ ".warp{ width:90%;margin:0 auto;line-height:24px;padding:20px 0;}"
					+ ".list{font-size:14px;padding:10px 0;width:300px;margin:0 auto;}"
					+ ".list strong{padding-right:10px;}"
					+ "table{width:100%;cellpadding:0;cellspacing:0;border-collapse:collapse;border-spacing:0;font-size:12px;margin:0;padding:0;}"
					+ "table th{text-align:center;border:1px #e8e8e8 solid; background:#efefef;padding:5px 20px;}"
					+ "table td{text-align:center;padding:5px 20px;border:1px #e8e8e8 solid;}");

			fw.write("</style>");
			fw.write("</head>");
			fw.write("<body>");
			fw.write("<div class=\"warp\">	" + "<ul class=\"list\">" + "    	<li><strong>接口名称：</strong><span>"
					+ caseClassName + "</span></li>" + "     <li><strong>执行时间：</strong><span>" + date + "</span></li>"
					+ "     <li><strong>Test:</strong><span>" + date + "</span></li>" + "    </ul>");
			fw.write("<table>");
			fw.write("<tr>");
			fw.write("<th>用例编号</th><th>用例说明</th><th>输入参数</th>");
			fw.write("<th>接口状态码(预期/实际)</th><th>接口返回码(预期/实际)</th><th>返回码说明(预期/实际)</th><th>用例执行结果</th>");
			fw.write("</tr>");

			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("创建报告文件报错");
			e.printStackTrace();
		}

	}

	// 添加报告内容 传入参数，用例编号、传入参数、
	public void addReport(String caseData) {
		try {

			fw = new FileWriter(fileName, true);
			fw.write("<tr>");

			for (int i = 0; i < caseData.split("#").length; i++) {
				fw.write("<td>" + caseData.split("#")[i] + "</td>");

			}
			fw.write("</tr>");
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
	}

	// 关闭报告
	public void fileClose() {
		try {
			fw = new FileWriter(fileName, true);
			fw.write("</table>");
			fw.write("</div></body>");
			fw.write("</html>");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
