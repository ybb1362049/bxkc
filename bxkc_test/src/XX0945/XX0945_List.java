package XX0945;

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

public class XX0945_List extends TxtRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	Pattern page = Pattern.compile("(\\s*)(\\d+)(\\s*)条");
	Matcher m;

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
				Document doc = Jsoup.parse(originTxtRspContent);
				Element dive = doc.select("div[id=pages]").last();
				if (dive != null) {
					m = page.matcher(dive.text());
					if (m.find()) {
						String count = m.group(2);
						int p = Integer.parseInt(count);
						response.pageCount = (p - 1) / 24 + 1 + "";
					}
				}
				Element ul = doc.select("ul[class=list lh24 f14]").first();
				if (ul != null) {
					Elements list = ul.select("li:has(a)");
					for (Element li : list) {
						BranchNew bn = new BranchNew();
						Element a = li.select("a").last();
						String id = a.attr("href");
						id = id.substring(id.indexOf("?") + 1);
						String title = a.attr("title").trim();
						title = title.replaceAll("\\s*|\u0026\u0023\u0031\u0038\u0033\u003b", "");
						m = p.matcher(li.text());
						String date = "";
						if (m.find()) {
							String month = m.group(2).length() == 2 ? m.group(2) : "0" + m.group(2);
							String day = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
							date = m.group(1) + "-" + month + "-" + day;
						}
						bn.id = id;
						bn.title = title;
						bn.date = date;
						response.list.add(bn);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
