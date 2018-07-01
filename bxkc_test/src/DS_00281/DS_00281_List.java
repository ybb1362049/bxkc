package DS_00281;

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

public class DS_00281_List extends TxtRspHandler {

	private Pattern p;
	private Matcher m;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		
		String id;
		String title;
		String date;

		@Override
		public String toString() {
			return "BranchNew [id=" + id + ", title=" + title + ", date=" + date + "]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {

		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState respState, String originTxtRspContent) {
		Response response = new Response();
		if (respState == RspState.Login) {
			try {
				Document xmlDoc = Jsoup.parse(originTxtRspContent);
				Element ul = xmlDoc.select("ul[class=list-ul]").first();
				Element tdPageCount = xmlDoc.select("td[class=huifont]").first();
				Elements liList = ul.select("li");
				String id = "";
				String title = "";
				String date = "";
				String pageCount = "";
				String content = tdPageCount.text().trim();
				p = p.compile("(\\d+)/(\\d+)");
				m = p.matcher(content);
				if (m.find()) {
						pageCount = m.group(2);
				}
				response.pageCount = pageCount;
				for (Element li : liList) {
					Element span = li.select("span").get(1);
					Element a = li.select("a").get(1);

					date = span.text().trim();
					title = a.attr("title").trim();
					String href = a.attr("href");
					id=href.substring(href.lastIndexOf("?")+1);
					BranchNew branchnew = new BranchNew();
					branchnew.id = id;
					branchnew.title = title;
					branchnew.date = date;
					response.list.add(branchnew);
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			}

		}

		return response;

	}


}
