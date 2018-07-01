package DX000273;

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

public class DX000273_List extends TxtRspHandler {

	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", url=" + url + ", date=" + date + "]";
		}

	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;

	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {

		Response response = new Response();
		if (arg0 == RspState.Login) {
			try {
				Document doc = Jsoup.parse(arg1);
				Elements tr_list = doc.select("tr[height=25]");
				for (Element tr : tr_list) {
					BranchNew n = new BranchNew();
					String title = tr.select("td a font").html();
					String href = tr.select("td a").get(0).attr("href");
					String id = href.substring(href.indexOf("=") + 1);
					tr.select("td > a").remove();
					String date = tr.select("td").html();
					String year = "20" + date.substring(12, 14);
					String mon = date.substring(15, 17);
					String day = date.substring(18, 20);
					date = year + "-" + mon + "-" + day;

					n.date = date;
					n.title = title;
					n.id = id;
					response.list.add(n);
				}

				String hrml = doc.select("div[align=right]").get(0).html();
				Pattern p = Pattern.compile("共\\d+页");
				Matcher m = p.matcher(hrml);
				while (m.find()) {

					String pagecount = m.group();
					pagecount = pagecount.substring(1, pagecount.length() - 1);

					response.pageCount = pagecount;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
