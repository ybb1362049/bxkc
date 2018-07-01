package DS_00444;

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

public class DS_00444_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
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
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				Element tablee = xmlDoc.getElementById("MoreInfoList1_DataGrid1");
				NodeList trs = tablee.getElementsByTagName("tr");
				if(trs != null){
					for(int i = 0 ; i < trs.getLength();i++){
						Element tre = (Element) trs.item(i);
						BranchNew bn = new BranchNew();
						Element ae = (Element) tre.getElementsByTagName("a").item(0);
						String title = ae.getAttribute("title");
						String id = ae.getAttribute("href");
						id = id.substring(id.indexOf("?")+1,id.length());
						String date = tre.getElementsByTagName("td").item(2).getTextContent().trim();
						bn.date = date;
						bn.id = id;
						bn.title = title;
						response.list.add(bn);
//						//System.out.println("title="+title+" id="+id+" date="+date);
					}
				}
				NodeList as = xmlDoc.getElementsByTagName("a");
				if(as != null){
					for(int i = 0 ; i < as.getLength();i++){
						Element ae = (Element) as.item(i);
						if("margin-right:5px;".equals(ae.getAttribute("style"))){
							String href = ae.getAttribute("href");
							href = href.substring(href.indexOf(",")+2,href.indexOf(")")-1);
							response.pageCount = href;
						}
					}
				}
				if(response.pageCount == null){
					response.pageCount = "1";
				}
//				//System.out.println(response.pageCount);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
