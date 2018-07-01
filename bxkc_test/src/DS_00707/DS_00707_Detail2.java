package DS_00707;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00707_Detail2 extends TxtRspHandler {
	
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
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				originTxtRspContent = "<div>" + originTxtRspContent
						+ "</div>";
//				////System.out.println(originTxtRspContent);
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList a_List=xmlDoc.getElementsByTagName("A");//改附件
				if(a_List.getLength()>0){
					for(int j=0;j<a_List.getLength();j++){
						Element a_ele=(Element) a_List.item(j);
						String href=a_ele.getAttribute("href");
						if(!href.equals("")){
						String a=href.substring(0, 1);
						if(a.equals(".")){
							href="http://zbtb.jingdezhen.gov.cn"+href.substring(2,href.length());
							a_ele.setAttribute("href", href);
						}
						}
					}
				}
				NodeList a_List1=xmlDoc.getElementsByTagName("IMG");//改附件
				if(a_List1.getLength()>0){
					for(int j=0;j<a_List1.getLength();j++){
						Element a_ele=(Element) a_List1.item(j);
						String href=a_ele.getAttribute("src");
						if(!href.equals("")){
						String a=href.substring(0, 1);
						if(a.equals(".")){
							href="http://zbtb.jingdezhen.gov.cn"+href.substring(2,href.length());
							a_ele.setAttribute("src", href);
						}
						}
					}
				}
//				String content2="";
//				String date="";
//				NodeList td_List1=xmlDoc.getElementsByTagName("DIV");
//				for(int i=0;i<td_List1.getLength();i++){
//					Element td_ele=(Element) td_List1.item(i);
//					if("article_extinfo".equals(td_ele.getAttribute("id"))){
//						content2=td_ele.getTextContent();
//						date=content2.substring(content2.lastIndexOf("时间：")+3,content2.lastIndexOf("日"));
//						date=date.replaceAll("年", "-");
//						date=date.replaceAll("月", "-");
//						if(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")).length()<2){
//							date=date.substring(0,4)+"-"+"0"+date.substring(date.indexOf("-")+1,date.lastIndexOf("-"))+"-"+date.substring(date.lastIndexOf("-")+1);
//						}
//						if(date.substring(date.lastIndexOf("-")+1).length()<2){
//							date=date.substring(0,date.lastIndexOf("-")+1)+"0"+date.substring(date.lastIndexOf("-")+1);
//						}
//					}
//				}
				String content1="";
				NodeList td_List=xmlDoc.getElementsByTagName("TABLE");
				for(int i=0;i<td_List.getLength();i++){
					Element td_ele=(Element) td_List.item(i);
					if("#CCCCCC".equals(td_ele.getAttribute("bgcolor"))){
//						////System.out.println(Utils.getNodeHtml(td_ele).trim());
//						td_ele.getElementsByTagName("TR").item(0).setTextContent("");
//						content1=Utils.getNodeHtml(td_ele).trim();
//						Element td_ele1=(Element) td_List.item(i+1);
						content1=Utils.getNodeHtml(td_ele).trim();
//						break;
					}
				}
				
//					NodeList td_List1=xmlDoc.getElementsByTagName("DIV");
//					for(int i=0;i<td_List1.getLength();i++){
//						Element td_ele=(Element) td_List1.item(i);
//						if("more2_3".equals(td_ele.getAttribute("id"))){
//							content1=Utils.getNodeHtml(td_ele).trim();
//						}
//					}

				response.content =content1;
//				response.date =date;
				////System.out.println(response.content);
//				////System.out.println(response.date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}

