package SJ_00149;
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

public class SJ_00149_List extends TxtRspHandler {

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
				NodeList list=doc.getElementsByTagName("div");
				for (int j = 0; j < list.getLength(); j++) {
					Element  ele1=(Element) list.item(j);
					if(ele1.getAttribute("class").equals("mtop pages"))
					{
						
						String pageString=ele1.getTextContent();
						pageString = pageString.substring(pageString.indexOf("/")+1,pageString.indexOf("页")).trim();
						response.pageCount=pageString;
						//System.out.println(pageString);
					}
					if(ele1.getAttribute("class").equals("main"))
					{
						NodeList   ele_tr=ele1.getElementsByTagName("li");

						if(ele_tr!=null&&ele_tr.getLength()>0)
						{
							for(int i=1;i<ele_tr.getLength()-1;i++)
							{

								Element  element=(Element) ele_tr.item(i);
								if(!element .getAttribute("class").equals("label_dashed"))
								{
									Element eleA=(Element) element.getElementsByTagName("a").item(0);
									String hrefString =eleA.getAttribute("href");
									String id=hrefString.substring(hrefString.indexOf("html")+5);
									String title=eleA.getAttribute("title");
									Element eletd=(Element) element.getElementsByTagName("span").item(0);
									String  dateString=null;
									dateString=eletd.getTextContent().trim();
									dateString=dateString.replace("[", "").replace("]", "");
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