package DS_04013;

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

public class DS_04013_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date="+date+ ";]";
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
				 //System.out.println(originTxtRspContent);
				//response.currentPage=originTxtRspContent;
				Document xml=Utils.getDocByContent(originTxtRspContent);
				NodeList uls=xml.getElementsByTagName("table");
				 if(uls!=null)
				 {
					 for(int i=0;i<uls.getLength();i++)
					 {
						 Element uld=(Element) uls.item(i);
						 if("99%".equals(uld.getAttribute("width")))
						 {
							NodeList trs=uld.getElementsByTagName("tr");
							for(int j=1;j<trs.getLength();j++){
                                Element ul=(Element) trs.item(j);
								 Element a = (Element) ul.getElementsByTagName("a").item(0);
									String id = a.getAttribute("href");
									id = id.substring(id.lastIndexOf("=")+1);

									String title = a.getAttribute("title").trim();
                                   NodeList tds=ul.getElementsByTagName("td");
									Element tr = (Element) tds.item(tds.getLength()-1);
									String date = tr.getTextContent().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
									date=date.substring(0,10);
									BranchNew bn = new BranchNew();
									bn.title = title;
									bn.id = id;
									bn.date = date;
									response.list.add(bn);
									//System.out.println(bn);
							 
						 
							}
						 }
					 }
				 }	
								
				 Element span = xml.getElementById("ctl00$ContentPlaceContent$Pager_input");
					if (span != null) {
						NodeList op=span.getElementsByTagName("option");
						Element opp=(Element) op.item(op.getLength()-1);
						String page=opp.getTextContent().trim();
						response.pageCount=page;
						//System.out.println(page);
					}
	
			} catch (Exception e) {
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
			result = new String(retBytes, "gbk");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
