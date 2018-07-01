package DX000587;

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

public class DX000587_List extends TxtRspHandler {

	private static Pattern p_page = Pattern.compile("\\共\\s*(\\d+)\\s*\\页");
	private Matcher m;

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
				Element list_ee = document.select("div.articleList ul").first();
				Elements li_ee = list_ee.select("li:has(a)");
				String id = null, title = null, date = null, url = null;
				for (Element li : li_ee) {
					Element a_ee = li.select("a").first();
					id = a_ee.attr("href");
					id = id.substring(id.indexOf("?") + 1);
					// title = a_ee.attr("title").trim().replaceAll("[
					// |\u3000]", "");
					title = a_ee.text().trim().replaceAll("[ |\u3000]", "");
					title = title.replaceAll("\\[.*?\\]", "");
					url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=DX000587_Detail&" + id;
					BranchNew bNew = new BranchNew();
					bNew.id = id;
					bNew.title = title;
					bNew.date = date;
					bNew.url = url;
					response.list.add(bNew);
				}
				response.pageCount = "1";
				Element page_ee = document.select("div.dede_pages").first();
				if (page_ee != null) {
					m = p_page.matcher(page_ee.text());
					if (m.find()) {
						String pageCount = m.group(1);
						// pageCount = (int)
						// Math.ceil(Integer.parseInt(pageCount) / 50.0) + "";
						response.pageCount = pageCount;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
