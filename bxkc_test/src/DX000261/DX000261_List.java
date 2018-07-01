package DX000261;

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

public class DX000261_List extends TxtRspHandler {

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
					if("BorderBlueNoTop Padding10".equals(table_ele.getAttribute("class"))){
						//Element td_ele=(Element)table_ele.getElementsByTagName("td").item(0);
						NodeList lists=table_ele.getElementsByTagName("li");
						for(int j=0;j<lists.getLength();j++){
							Element lis=(Element) lists.item(j);
							Element a_ele=(Element)lis.getElementsByTagName("a").item(0);
							BranchNew branchNew=new BranchNew();
							String title=a_ele.getAttribute("title").trim();
							String href=a_ele.getAttribute("href");
							href=href.substring(href.indexOf("/")+1);
							Element dates=(Element) lis.getElementsByTagName("span").item(1);
							String date=dates.getTextContent().trim();
							branchNew.title=title;
							
							branchNew.date=date;
							branchNew.id=href;
							if(!response.list.contains(title)){
								response.list.add(branchNew);
								//System.out.println(branchNew.toString());
							}
							
							
						}
						NodeList div_list=table_ele.getElementsByTagName("option");
						Element op=(Element) div_list.item(div_list.getLength()-1);
						String page=op.getTextContent().trim();
						response.pageCount=page;
						//System.out.println(page);
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
