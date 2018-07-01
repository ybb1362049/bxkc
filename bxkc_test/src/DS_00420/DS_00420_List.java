package DS_00420;

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

public class DS_00420_List extends TxtRspHandler {

	public static Pattern pattern = Pattern.compile("共(?<totalPage>\\d+)条");
	public static Matcher matcher;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String all;
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
		return RspState.Login; // 默认成功登录状态
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				Element divEle = document.select("div[id=resources]").first();
				Elements liEle = divEle.select("table");
				for (int i = 0; i < liEle.size(); i++) {
					Element listEle = liEle.get(i);
					Element titleEle = listEle.select("a[href]").last();
					Element dateEle = listEle.select("td[width=81]").first();

					String title = titleEle.hasAttr("title") ? titleEle.attr("title").trim() : titleEle.text().trim();
					String date = dateEle.text();
					String href = titleEle.attr("href");
					String id = href.substring(href.indexOf("/cms/") + 5);
					if (id.endsWith(".body")) {
						continue;
					}

					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.date = date.substring(1, 11);
					branchNew.id = id.replace("?", "&");
					response.list.add(branchNew);
				}
				Element pageInfoEle = document.select("div[class=pageStyle]").last();
				String pageInfo = pageInfoEle.text();
				matcher = pattern.matcher(pageInfo);
				if (matcher.find()) {
					response.pageCount = Integer.parseInt(matcher.group("totalPage")) / 15 + 1 + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
