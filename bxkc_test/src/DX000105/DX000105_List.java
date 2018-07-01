
package DX000105;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000105_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				String page = xmlDoc.getElementById("selPage").getTextContent().trim();
				page = page.substring(page.indexOf("共")+1, page.indexOf("页"));
				response.pageCount = page;
//				//System.out.println(response.pageCount);
				
				NodeList ulList = xmlDoc.getElementsByTagName("div");
				if (ulList != null && ulList.getLength()>0) {
					for (int i = 0; i < ulList.getLength(); i++) {
						Element ulElement = (Element) ulList.item(i);
						if (ulElement.getAttribute("class").equals("info")) {
							NodeList trs = ulElement.getElementsByTagName("div");	
							if(trs != null&&trs.getLength()>0){
								for (int j = 0; j < trs.getLength()-1; j++) {
									String id = "";
									String title = "";
									String date = "";
									BranchNew branchNew = new BranchNew();
									Element tre = (Element) trs.item(j);								
									Element a_ele = (Element) tre.getElementsByTagName("a").item(0);
									id = a_ele.getAttribute("href");
									id = id.substring(id.lastIndexOf("?")+1);
									branchNew.id = id;
									String news = a_ele.getTextContent().trim();
									title = news.substring(0,news.indexOf("[")).trim();
									branchNew.title = title;									
									date = news.substring(news.lastIndexOf("[")+1,news.lastIndexOf("]")).replace("/", "-");
									branchNew.date = date;
									response.list.add(branchNew);
//									//System.out.println("title="+title+" id="+id+" date="+ date);
								}	
							}
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}

