package SJ_04335;

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

public class SJ_04335_List extends TxtRspHandler {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Pattern p = Pattern.compile("'/共(\\d+)页'");
	Matcher m;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", url=" + url + "]";
		}

	}

	@Override
	public RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xml = Jsoup.parse(originTxtRspContent);
				Element table = xml.select("ul[class=snew]").first();
				Elements trs = table.select("li:has(a)");
				for (Element tre : trs) {
					Element ae = tre.select("a").first();
					String href = ae.attr("href");
					href = href.substring(href.indexOf("?") + 1);
					// String = href.substring(href.indexOf("?") +
					// 1,href.length());
					// String title = ae.text().trim().replace("...", "");
					String title = ae.text().trim().replace("...", "");
					String date = tre.select("span[style=color:#AAA;font-size:12px;]").first().text().trim();
					date = sdf.format(sdf.parse(date));
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = href;
					bn.title = title;
					response.list.add(bn);
				}
				Element str = xml.select("*td[class=font999]").first();
				m = p.matcher(str.text());
				if (m.find()) {
					String pageCount = m.group(1);
					response.pageCount = pageCount;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
