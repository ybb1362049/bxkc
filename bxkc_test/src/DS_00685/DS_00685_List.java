package DS_00685;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.Utils;

public class DS_00685_List extends TxtRspHandler {

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

			return "BranchNew [ title=" + title + ", date=" + date + ",id=" + id + "]" + "\n";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				originTxtRspContent = originTxtRspContent.replace("<![CDATA[", "").replace("]]>", "");

				Document doc = Utils.getDocByContent(originTxtRspContent);

				Element datestore = (Element) doc.getElementsByTagName("datastore").item(0);
				Element totalpage = (Element) datestore.getElementsByTagName("totalpage").item(0);
				String pageCount = totalpage.getTextContent();
				response.pageCount = pageCount;
				// System.out.println(pageCount);

				Element recordset = (Element) datestore.getElementsByTagName("recordset").item(0);
				NodeList recordList = recordset.getElementsByTagName("record");
				for (int i = 0; i < recordList.getLength(); i++) {
					Element record = (Element) recordList.item(i);
					Element eleA = (Element) record.getElementsByTagName("a").item(0);
					String hrefString = eleA.getAttribute("href");
					String id = hrefString.substring(hrefString.indexOf("art") + 4);
					String title = eleA.getAttribute("title");
					Element eletd = (Element) record.getElementsByTagName("span").item(0);
					String dateString = null;
					dateString = eletd.getTextContent().trim();
					int year = Integer.parseInt(dateString.substring(0, 4));
					if (year < 2016) {
						continue;
					}
					if (title != null && title.length() > 0) {
						BranchNew branchNew = new BranchNew();
						branchNew.title = title.replaceAll("<(.*?)>|\\s+", "");
						branchNew.id = id;
						branchNew.date = dateString;
						response.list.add(branchNew);
					}
					// System.out.println(branchNew);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}