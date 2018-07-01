package SJ_00108;

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

public class SJ_00108_List extends TxtRspHandler {

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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
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
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if (divs != null) {
					for (int i = 0; i < divs.getLength(); i++) {
						Element dive = (Element) divs.item(i);
						if ("xnrx".equals(dive.getAttribute("class"))) {
							NodeList lis = dive.getElementsByTagName("li");
							if (lis != null) {
								for (int j = 0; j < lis.getLength(); j++) {
									Element lie = (Element) lis.item(j);
									BranchNew bn = new BranchNew();
									String date = lie.getElementsByTagName("span").item(0).getTextContent()
											.replace(".", "-").trim();
									Element a_ele = (Element) lie.getElementsByTagName("a").item(0);
									String id = a_ele.getAttribute("href");
									String title = a_ele.getTextContent().trim().replace("..", "");
									bn.title = title;
									bn.id = id;
									bn.date = date;
									response.list.add(bn);
									// //System.out.println("title="+title+"
									// id="+id+" date="+date);
								}
							}
						}
						if ("page".equals(dive.getAttribute("class"))) {
							String count = dive.getTextContent().trim();
							count = count.substring(count.indexOf("共") + 1, count.length() - 1).trim();
							int p = Integer.parseInt(count);
							int yushu = p % 15;
							if (yushu == 0) {
								int pa = p / 15;
								String page = String.valueOf(pa);
								response.pageCount = page;
							} else {
								int pa = p / 15 + 1;
								String page = String.valueOf(pa);
								response.pageCount = page;
							}
						}
					}
				}
				// //System.out.println(response.pageCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
