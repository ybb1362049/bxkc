package DX000605;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000605_List extends TxtRspHandler {

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
				Element boxElement = null;
				Elements listElement = null;
				if (xml.select(
						"body > div.m-warp > div.m-body > div.m-container.pb40 > div.g-ohide.m-listpage > div.g-right.w740 > table > tbody")
						.first() != null) {
					boxElement = xml
							.select("body > div.m-warp > div.m-body > div.m-container.pb40 > div.g-ohide.m-listpage > div.g-right.w740 > table > tbody")
							.first();
					listElement = boxElement.select("tr:has(a)");
				} else {
					boxElement = xml.select("ul.m-newslist").first();
					listElement = boxElement.select("li:has(a)");
				}

				for (Element list : listElement) {
					BranchNew bn = new BranchNew();
					Element hrefElement = list.select("a").first();
					String dateString = list.text().replaceAll(
							"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					Pattern p = Pattern.compile("\\d+{4}-\\d{1,2}-\\d{1,2}");
					Matcher m;
					m = p.matcher(dateString);
					if (m.find()) {
						dateString = m.group();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
					dateString = sdf.format(sdf.parse(dateString));
					String id = hrefElement.attr("href");
					id = id.substring(id.indexOf("2/") + 2).replace("?", "&");
					String title = hrefElement.text().replaceAll(
							"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					bn.title = title;
					bn.id = id;
					bn.date = dateString;
					response.list.add(bn);
				}

				Element pageElement = xml.select("select").last();
				Element pages = pageElement.select("option").last();
				String page = pages.text().trim();
				response.pageCount = page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}