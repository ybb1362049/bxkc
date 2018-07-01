package SJ_00081;
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

public class SJ_00081_List extends TxtRspHandler {

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

			return "BranchNew [ title=" + title + ", date=" + date + ",id="
					+ id + "]"+"\n";
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
			try  {
				Document doc = Utils.getDocByContent(originTxtRspContent);
				NodeList list=doc.getElementsByTagName("td");
				for (int j = 0; j < list.getLength(); j++) {
					Element  ele1=(Element) list.item(j);
					if(ele1.getAttribute("class").equals("left")&&ele1.getAttribute("width").equals("745"))
					{
						NodeList   ele_tr=ele1.getElementsByTagName("table");

						if(ele_tr!=null&&ele_tr.getLength()>0)
						{
							Element  elementpage=(Element) ele_tr.item(0);
							Element script=(Element) elementpage.getElementsByTagName("script").item(0);
							String pageString=script.getTextContent();
							pageString = pageString.substring(pageString.indexOf("(")+1,pageString.indexOf(",")).trim();
							response.pageCount=pageString;
							//System.out.println(pageString);
							
							
							for(int i=2;i<ele_tr.getLength()-2;i++)
							{
								Element  element=(Element) ele_tr.item(i);
								Element eleA=(Element) element.getElementsByTagName("a").item(0);
								String hrefString =eleA.getAttribute("href");
								String id=hrefString.substring(hrefString.indexOf("/")+1);
								String title=eleA.getAttribute("title");
								Element eletd=(Element) element.getElementsByTagName("script").item(0);
								String  dateString=null;
								dateString=eletd.getTextContent().trim();
								dateString=dateString.substring(dateString.indexOf(";")+1);
								dateString=dateString.substring(dateString.indexOf("=")+1,dateString.indexOf(";"));
								dateString=dateString.replace("\"", "");
								dateString="20"+dateString;
								BranchNew branchNew = new BranchNew();
								branchNew.title = title;
								branchNew.id = id;
								branchNew.date = dateString;
								response.list.add(branchNew);
								//System.out.println(branchNew);
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