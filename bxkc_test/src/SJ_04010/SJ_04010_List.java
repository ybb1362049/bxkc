package SJ_04010;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_04010_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";]";
		}

	}

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
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				//System.out.println(originTxtRspContent);
				NodeList table_list=xmlDoc.getElementsByTagName("div");
				for(int i=0;i<table_list.getLength();i++){
					Element table_ele=(Element)table_list.item(i);
					if("ej_list".equals(table_ele.getAttribute("class"))){
						Element tbody=(Element) table_ele.getElementsByTagName("ul").item(0);
						//Element td_ele=(Element)table_ele.getElementsByTagName("td").item(0);
						NodeList lists=tbody.getElementsByTagName("li");
						for(int j=0;j<lists.getLength();j++){
							Element lis=(Element) lists.item(j);
							Element a_ele=(Element)lis.getElementsByTagName("a").item(0);
							BranchNew branchNew=new BranchNew();
							String title=a_ele.getTextContent().trim();
							String href=a_ele.getAttribute("href");
							href=href.substring(href.indexOf("/")+1);
							Element dates=(Element) lis.getElementsByTagName("b").item(0);
							String date=dates.getTextContent().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
							date=date.substring(date.indexOf("[")+1,date.lastIndexOf("]"));
							branchNew.title=title;
							
							branchNew.date=date;
							branchNew.id=href;
							if(!response.list.contains(title)){
								response.list.add(branchNew);
								//System.out.println(branchNew.toString());
							}
							
							
						}
						NodeList div_list=xmlDoc.getElementsByTagName("div");
						for(int k=0;k<div_list.getLength();k++){
							Element divp=(Element) div_list.item(k);
							if(divp.getAttribute("class").equals("f_page")){
								NodeList as=divp.getElementsByTagName("a");
								Element ah=(Element) as.item(as.getLength()-1);
								String page=ah.getAttribute("href");
								page=page.substring(page.lastIndexOf("index")+5,page.lastIndexOf("."));
								response.pageCount=page;
								//System.out.println(page);
								
							}
						}
						
					}
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
