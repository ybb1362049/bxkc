package DS_00401;

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

public class DS_00401_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		
		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;


		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";url="+ url+"]";
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
				Element element=xmlDoc.getElementById("MoreInfoList1_DataGrid1");
							NodeList selected_List_Tr =element.getElementsByTagName("tr");
							if(selected_List_Tr!=null)
							{
								for(int i=0;i<selected_List_Tr.getLength();i++)
								{
									Element selected_Tr=(Element) selected_List_Tr.item(i);
									Element temp_A=(Element) selected_Tr.getElementsByTagName("a").item(0);
									BranchNew branchNew = new BranchNew();
									String title=temp_A.getAttribute("title");

									String href=temp_A.getAttribute("href");
									String id=href.substring(href.indexOf('?')+1);
									branchNew.id=id;
									String dateString=((Element) selected_Tr.getElementsByTagName("td").item(2)).getTextContent().trim();
									
									branchNew.title=title;
									branchNew.date=dateString;
									response.list.add(branchNew);
									//System.out.println(branchNew.toString());
								}
							}
						
				

				Element ele=xmlDoc.getElementById("MoreInfoList1_Pager");
				Element element2=(Element) ele.getElementsByTagName("font").item(1);
				String pageCount=element2.getTextContent();
				response.pageCount=pageCount.trim();
				//System.out.println(pageCount);


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
