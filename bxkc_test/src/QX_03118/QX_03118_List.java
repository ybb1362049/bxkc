package QX_03118;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_03118_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				Document xml = Jsoup.parse(originTxtRspContent);
				Element conTag = xml.select("div[class=news_list]").first();
				Elements spans = conTag.select("li:has(a)");
				for (Element spane : spans) {
					String title = spane.select("a").attr("title");
					if (title.contains("：")) {
						title = title.substring(title.indexOf("：") + 1);
					}
					title = title.replaceAll(
							"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");

					String str = spane.select("p[class=demo]").last().text().trim();
					str = str.replaceAll("[.|/|年|月]", "-");
					String date = "";
					Matcher m = p.matcher(str);
					if (m.find()) {
						date = sdf.format(sdf.parse(m.group()));
					}

					Element a = spane.select("h2").first();
					String id = a.select("a").first().attr("href");
					// id = id.substring(id.indexOf("?") + 1);
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=QX_03118_Detail&iw_ir_1=" + id;
					BranchNew bn = new BranchNew();
					bn.id = id;
					bn.date = date;
					bn.url = url;
					bn.title = title;
					response.list.add(bn);
				}
				String page = xml.select("span[id=lblCount]").text().trim();
				// page = page.substring(page.indexOf("共") + 1,
				// page.indexOf("页"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
