package XX1297;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class XX1297_List extends TxtRspHandler {

	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				Element list_ee = document.select("td.right_bj table[width=96%]").first();
				Elements li_ee = list_ee.select("tr:has(a)");
				String id = null, title = null, date = null, url = null;
				for (Element li : li_ee) {
					Element a_ee = li.select("a").first();
					id = a_ee.attr("href");
					id = id.substring(id.indexOf("?") + 1);
					// title = a_ee.attr("title").trim().replaceAll("[
					// |\u3000]", "");
					title = a_ee.text().trim().replaceAll("[ |\u3000]", "");
					title = title.replaceAll("\\[.*?\\]", "");
					url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=XX1297_Detail&"
							+ id;
					BranchNew bNew = new BranchNew();
					bNew.id = id;
					bNew.title = title;
					bNew.date = date;
					bNew.url = url;
					response.list.add(bNew);
				}
				response.pageCount = "1";
				Element page_ee = document.select("div.flickr a").last();
				if (page_ee != null) {
					if (page_ee.text().trim().equals("下一页»")) {
						page_ee = page_ee.previousElementSibling();
					} else {
						page_ee = page_ee.nextElementSibling();
					}
					response.pageCount = page_ee.text().trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
