package DS_01399;

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

public class DS_01399_List extends TxtRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	private static Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static Pattern page = Pattern.compile("/(\\d+)(\\s*)页");

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

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
				Matcher m;
				Document doc = Jsoup.parse(originTxtRspContent);
				Element dive = doc.select("div.pagenum").last();
				if (dive != null) {
					m=page.matcher(dive.text());
					if(m.find())
					{
						response.pageCount=m.group(1);
					}
				}
				Element div = doc.select("div[class=blk01]").last();
				if (div != null) {
					Elements ul = div.select("ul");
					Elements list = ul.select("li:has(a)");
					for (int i = 0; i < list.size(); i++) {
						Element li = list.get(i);
						BranchNew bn = new BranchNew();
						Element a = li.select("a").last();
						String id = a.attr("href");
						id = id.substring(id.indexOf("?") + 1);
						bn.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=DS_01399_Detail&" + id;
						m = p.matcher(li.text());
						String date = "";
						if (m.find()) {
							String month = m.group(2).length() == 2 ? m.group(2) : "0" + m.group(2);
							String day = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
							date = m.group(1) + "-" + month + "-" + day;
						}
						String title = a.select("span").first().attr("title").trim();
						bn.id = id;
						bn.title = title.replaceAll("【(.*?)】|\\s*", "");
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
