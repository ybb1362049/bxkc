package SJ_00171;

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

public class SJ_00171_9_List extends TxtRspHandler {
	public class BranchNew {
		String title;
		String id;
		String url;
		String date;
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		Pattern p = Pattern.compile("([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))");
		Pattern p1 = Pattern.compile("\\d+");
		if (arg0 == RspState.Login) {
			try {
				Document doc = Jsoup.parse(arg1);
				Elements li_list = doc.select("div.PageList > ul > li");
				for (Element li : li_list) {
					BranchNew n = new BranchNew();
					String id = li.select("p > a").get(0).attr("href");
					String title = li.select("p > a").get(0).html().replace(".", "").trim();
					String date = li.html();
					Matcher m = p.matcher(date);
					if (m.find()) {
						date = m.group();
						n.date = date;
					}
					n.id = id;
					n.title = title;
					response.list.add(n);
				}
				if (doc.select("a.shouy").size() > 0) {
					String href = doc.select("a.shouy").get(1).attr("href");
					Matcher m = p1.matcher(href);
					if (m.find()) {
						href = m.group();
						response.pageCount = href;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}