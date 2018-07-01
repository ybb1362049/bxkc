package DX000039;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000039_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String date;
	}

	final static int TIMEOUT_IN_MS = 100000;

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList a_List = xmlDoc.getElementsByTagName("A");// 改附件
				if (a_List.getLength() > 0) {
					for (int j = 0; j < a_List.getLength(); j++) {
						Element a_ele = (Element) a_List.item(j);
						String href = a_ele.getAttribute("href");
						if (!href.equals("")) {
							String a = href.substring(0, 1);
							if (a.equals("/")) {
								href = "http://www.fidc.com.cn" + href;
								a_ele.setAttribute("href", href);
							}
						}
					}
				}
				NodeList a_List1 = xmlDoc.getElementsByTagName("IMG");// 改附件
				if (a_List1.getLength() > 0) {
					for (int j = 0; j < a_List1.getLength(); j++) {
						Element a_ele = (Element) a_List1.item(j);
						String href = a_ele.getAttribute("src");
						if (!href.equals("")) {
							String a = href.substring(0, 1);
							if (a.equals("/")) {
								href = "http://www.fidc.com.cn" + href;
								a_ele.setAttribute("src", href);
							}
						}
					}
				}
				// String content2="";
				// String date="";
				// NodeList td_List1=xmlDoc.getElementsByTagName("DIV");
				// for(int i=0;i<td_List1.getLength();i++){
				// Element td_ele=(Element) td_List1.item(i);
				// if("article_extinfo".equals(td_ele.getAttribute("id"))){
				// content2=td_ele.getTextContent();
				// date=content2.substring(content2.lastIndexOf("时间：")+3,content2.lastIndexOf("日"));
				// date=date.replaceAll("年", "-");
				// date=date.replaceAll("月", "-");
				// if(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")).length()<2){
				// date=date.substring(0,4)+"-"+"0"+date.substring(date.indexOf("-")+1,date.lastIndexOf("-"))+"-"+date.substring(date.lastIndexOf("-")+1);
				// }
				// if(date.substring(date.lastIndexOf("-")+1).length()<2){
				// date=date.substring(0,date.lastIndexOf("-")+1)+"0"+date.substring(date.lastIndexOf("-")+1);
				// }
				// }
				// }
				String content1 = "";
				NodeList td_List = xmlDoc.getElementsByTagName("TD");
				for (int i = 0; i < td_List.getLength(); i++) {
					Element td_ele = (Element) td_List.item(i);
					if ("t14".equals(td_ele.getAttribute("class")) && "30".equals(td_ele.getAttribute("height"))
							&& "center".equals(td_ele.getAttribute("align"))) {
						td_ele.setTextContent("");
						Element td_ele2 = (Element) td_List.item(i + 1);
						td_ele2.setTextContent("");
					}
				}

				NodeList td_List1 = xmlDoc.getElementsByTagName("TD");
				for (int i = 0; i < td_List1.getLength(); i++) {
					Element td_ele = (Element) td_List1.item(i);
					if ("pad_top_8 pad_bot_8".equals(td_ele.getAttribute("class"))) {
						content1 = Utils.getNodeHtml(td_ele).trim();

					}
				}
				if (content1.equals("") && originTxtRspContent.contains("This object may be found")) {
					content1 = "该信息需要登录才能查看";
					response.date = "2016-01-01";
				}
				response.content = content1;
				// response.date =date;
				//// System.out.printlnn(response.content);
				// ////////System.out.printlnn(response.date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
