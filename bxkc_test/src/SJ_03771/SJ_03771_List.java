package SJ_03771;

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

public class SJ_03771_List extends TxtRspHandler {

	public static Pattern pattern = Pattern.compile("/(?<totalPage>\\d+)");
	public static Matcher matcher;

	public class BranchNew {
		String title;
		String id;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
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
				Element ulElement=document.select("ul.article-list2").first();
				Elements liEle = ulElement.select("li");
				for (int i = 0; i < liEle.size(); i++) {
					Element listEle = liEle.get(i);
					Element titleEle = listEle.select("a[href]").first();
					Element dateEle = listEle.select("div.list-times").last();

					String title = titleEle.attr("title");
					String date = dateEle.text();
					String id = titleEle.attr("href");

					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.date = date;
					branchNew.id = id.replace(".html", "");
					response.list.add(branchNew);
				}
				Element pageInfoEle = document.select("div.pagesite").first();
				String pageCount = pageInfoEle.text();
				matcher = pattern.matcher(pageCount);
				if (matcher.find()) {
					response.pageCount = matcher.group("totalPage");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
