package DX000600;

import java.text.SimpleDateFormat;
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
import cn.internetware.utils.IO;

public class DX000600_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
		// String nextPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + ";]";
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
				Document xml = Jsoup.parse(originTxtRspContent);
				Element boxElement = xml.select("ul.news-list").first();
				Elements listElement = boxElement.select("li:has(a)");
				for (Element list : listElement) {
					BranchNew bn = new BranchNew();
					Element hrefElement = list.select("a").first();
					Element dateElement = list.select("span.date").last();
					String dateString = dateElement.text().replaceAll(
							"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
					dateString = sdf.format(sdf.parse(dateString));
					String id = hrefElement.attr("href");
					id = id.substring(id.indexOf("?") + 1);
					String title = hrefElement.attr("title").replaceAll(
							"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					bn.title = title;
					bn.id = id;
					bn.date = dateString;
					response.list.add(bn);
				}

				Element pageElement = xml.select("div.pages").last();
				Element pages = pageElement.select("a").last();
				String page = pages.attr("href");
				page = page.substring(page.lastIndexOf("=") + 1);
				response.pageCount = page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}