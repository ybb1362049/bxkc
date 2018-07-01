package QX_01415;

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

public class QX_01415_List2 extends TxtRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	private static Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static Pattern page = Pattern.compile("共(\\d+)页");

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
				Element dive = doc.select("span[id=ctl00_ContentPlaceHolder1_BestNewsListALL_myGV_ctl13_lblPage]").first();
				if (dive != null) {
					m = page.matcher(dive.text());
					if (m.find()) {
						response.pageCount = m.group(1);
					}
					dive.remove();
				}
				Element div = doc.select("table#ctl00_ContentPlaceHolder1_BestNewsListALL_myGV").last();
				if (div != null) {
					Element ul = div.select("tbody").first();
					Elements list = ul.select("tr:has(a)");
					for (int i = 0; i < list.size()-1; i++) {
						Element li = list.get(i);
						Element a = li.select("a").last();
						BranchNew bn = new BranchNew();
						String id = a.attr("href");
						//id = id.replace("?", "&");
						 id = id.substring(id.indexOf("?") + 1);
						m = p.matcher(li.text());
						String date = "";
						while (m.find()) {
							String month = m.group(2).length() == 2 ? m.group(2) : "0" + m.group(2);
							String day = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
							date = m.group(1) + "-" + month + "-" + day;
						}
						bn.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=QX_01415_Detail2&" + id;
						String title = a.text().trim();
						bn.id = id;
						bn.date = date;
						bn.title = title.replaceAll("\\[(.*?)\\]|\\s*", "");
						response.list.add(bn);
					}
				}
				if (doc.getElementById("__VIEWSTATE") != null) {
					String __VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
					String __EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
					String aspnetForm = doc.getElementById("aspnetForm").attr("action");
					aspnetForm = aspnetForm.substring(aspnetForm.indexOf("?") + 1);
					response.nextPage = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=QX_01415_List2&" + aspnetForm + "&__VIEWSTATE=" + __VIEWSTATE
							+ "&__EVENTVALIDATION=" + __EVENTVALIDATION;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
