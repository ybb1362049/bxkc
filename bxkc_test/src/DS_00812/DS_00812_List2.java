package DS_00812;

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

public class DS_00812_List2 extends TxtRspHandler {

	public static Pattern pattern = Pattern.compile(" 总页数：(?<totalPage>\\d+)");
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
				Element divEle = document.select("table[id=DataGrid1]").first();
				Elements liEle = divEle.select("tr[class=csscalss]");
				for (int i = 0; i < liEle.size(); i++) {
					Element listEle = liEle.get(i);
					Element titleEle = listEle.select("a[href]").first();
					Element dateEle = listEle.select("td").last();

					String title = titleEle.attr("title");
					if ("".equals(title)) {
						title = titleEle.text().replace("...", "");
					}
					String date = dateEle.text();
					String id = titleEle.attr("href");
					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.date = date;
					branchNew.id = id.replace("?", "&");
					response.list.add(branchNew);
				}
				Element pageInfoEle = document.select("div[id=Pager]").first();
				String pageInfo = pageInfoEle.text();
				matcher = pattern.matcher(pageInfo);
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
