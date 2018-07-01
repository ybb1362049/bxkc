package GJ_03525;
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
public class GJ_03525_List extends TxtRspHandler {

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
			try{
			    Document xmlDoc=Utils.getDocByContent(originTxtRspContent);//获取文档内容
				Element td_ele=xmlDoc.getElementById("MoreInfoList1_tdcontent");
			//	//System.out.println(Utils.getNodeHtml(td_ele));
				NodeList tr_list = td_ele.getElementsByTagName("tr");
				if(tr_list != null){
				    for(int i = 0;i < tr_list.getLength();i ++){
					    NodeList td_list =((Element) tr_list.item(i)).getElementsByTagName("td");
						if(td_list != null){
						    BranchNew branchNew=new BranchNew();
							String idate = td_list.item(2).getTextContent();
							idate = idate.trim();
							branchNew.date = idate;
							
							NodeList a_ele = ((Element)td_list.item(1)).getElementsByTagName("a");
							String href = ((Element) a_ele.item(0)).getAttribute("href");
						    branchNew.id = href.substring(href.indexOf("=")+1,href.indexOf("&"));
							
							branchNew.title= ((Element) a_ele.item(0)).getAttribute("title");
							
							response.list.add(branchNew);
				//		    //System.out.println(branchNew.toString());
						
						}								
					}
				}

				Element div_ele = xmlDoc.getElementById("MoreInfoList1_Pager");
				NodeList fon_list = div_ele.getElementsByTagName("font");
				if(fon_list != null){
				    String pageCount =fon_list.item(1).getTextContent();
					response.pageCount=pageCount;
		//			//System.out.println(pageCount);
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
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}

