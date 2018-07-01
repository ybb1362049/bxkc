package GJ_03524;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class GJ_03524_Detail extends TxtRspHandler {
	// 构建请求
	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login; // 默认登录成功状态
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				// 处理文件内容
				// 转换为DOM节点
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				// 删除 script 标签
				NodeList scrList = xmlDoc.getElementsByTagName("script");
				if (scrList != null && scrList.getLength() > 0) {
					for (int i = 0; i < scrList.getLength(); i++) {
						Node scrNode = scrList.item(i);
						Node scr_parNode = scrNode.getParentNode();
						scr_parNode.removeChild(scrNode);
					}
				}
				NodeList titleFist = xmlDoc.getElementsByTagName("p");
				if (titleFist != null) {
					for (int j = 0, length = titleFist.getLength(); j < length; j++) {
						Element title_Ele = (Element) titleFist.item(j);
						if("cfcpn_news_title".equals(title_Ele.getAttribute("class"))){
							
							String title = title_Ele.getTextContent().trim();
							response.title = title;
							
						}}}
				// 获取Detail页的内容
				String content = null; // 储存页面内容
				NodeList listFist = xmlDoc.getElementsByTagName("div");
				if (listFist != null) {
					for (int j = 0, length = listFist.getLength(); j < length; j++) {
						Element ele1 = (Element) listFist.item(j);
						if("col-lg-9 cfcpn_news_mian".equals(ele1.getAttribute("class"))){
						 //<1>删除不需要部分
							NodeList font = xmlDoc.getElementsByTagName("p");
							for (int k = 0; k < font.getLength(); k++) {
								Element font_ele = (Element) font.item(k);
								if ("cfcpn_news_date".equals(font_ele.getAttribute("class"))) {
									font_ele.setTextContent("");
								}
							}
							NodeList buttonList = xmlDoc.getElementsByTagName("h4");
							if(buttonList!=null){
							for (int k = 0; k < buttonList.getLength(); k++) {
								Element button_ele = (Element) buttonList.item(0);
									button_ele.setTextContent("");
							}}
							
						// <2>补全附件链接【存在就补全】
							NodeList ul_list=(NodeList) xmlDoc.getElementsByTagName("A");			
							if (ul_list!=null) {				
								for (int i = 0; i < ul_list.getLength(); i++) {
									Element a_ele=(Element)ul_list.item(i);
									String href=a_ele.getAttribute("href");
									if(href.length()>1){
										String h=href.substring(0,1);
										if(!h.equals("h")){
											href="http://www.cfcpn.com"+href;
											a_ele.setAttribute("href", href);
									}
									}
								}
						// <3>补全图片链接【存在就补全】
								NodeList IMG_list=xmlDoc.getElementsByTagName("img");	
								if (IMG_list!=null) {
									for (int i = 0; i <IMG_list.getLength(); i++) {
										Element IMG_ele=(Element)IMG_list.item(i);
											String  src=IMG_ele.getAttribute("src");
											if(src.indexOf("www")==-1){
											src = src.replaceFirst("/", "http://www.cfcpn.com/" );
											 }
											IMG_ele.setAttribute("src", src);
										}}
						// <4>储存内容【将字符串转换成HTML结构】
						
						content = Utils.getNodeHtml(ele1);
						response.content = content;
						
					}
				 }
				}}
				// 测试输出
				// System.out.println(response.content);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
