package QX_02185;

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

public class QX_02185_List extends TxtRspHandler {

	public static Pattern pageInfoPattern = Pattern.compile("共 (?<totalPage>\\d+) 页");
	public static Matcher m;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
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
				Document document = Jsoup.parse(originTxtRspContent);
				Element divEle = document.select("div[class=nav]").first();
				Element ulEle = divEle.select("ul").first();
				Elements trEle = ulEle.select("li");
				for (int i = 0; i < trEle.size(); i++) {
					Element listEle = trEle.get(i);
					Element titleEle = listEle.select("a[href]").first();
					Element dateEle = listEle.select("font[color=#999999]").first();

					String title = titleEle.attr("title");
					String[] dates = dateEle.text().substring(0).split("-");
					String date = dates[0];
					if (Integer.parseInt(dates[1]) < 10) {
						date += "-0" + dates[1];
					} else {
						date += "-" + dates[1];
					}
					if (Integer.parseInt(dates[2]) < 10) {
						date += "-0" + dates[2];
					} else {
						date += "-" + dates[2];
					}
					String href = titleEle.attr("href");
					if (href.contains("?"))
						continue;
					String id = href.substring(href.indexOf("/1/") + "/1/".length());

					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.date = date;
					branchNew.id = id.replace(".html", "");
					response.list.add(branchNew);
				}
				Element pageInfoEle = divEle.select("div").first();
				String pageInfo = pageInfoEle.text();
				m = pageInfoPattern.matcher(pageInfo);
				if (m.find()) {
					response.pageCount = m.group("totalPage");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
