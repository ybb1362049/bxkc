package DS_01402;

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

public class DS_01402_Params extends TxtRspHandler {
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
				Element dive = doc.select("span[id=ID_ucDefault_ucNewsListPager1_UcPager1_lbPageCount]").first();
				if (dive != null) {
					m = page.matcher(dive.text());
					if (m.find()) {
						response.pageCount = m.group(1);
					}
					dive.remove();
				}
				Element div = doc.select("table[id=ID_ucDefault_ucNewsListPager1_listArt]").last();
				if (div != null) {
					Element ul = div.select("tbody").first();
					Elements list = ul.select("tr:has(a)");
					for (int i = 0; i < list.size(); i++) {
						Element li = list.get(i);
						Element a = li.select("a").last();
						BranchNew bn = new BranchNew();
						String id = a.attr("href");
						// id=id.replace("?", "&");
						id = id.substring(id.indexOf("?") + 1);
						m = p.matcher(li.text());
						String date = "";
						while (m.find()) {
							String month = m.group(2).length() == 2 ? m.group(2) : "0" + m.group(2);
							String day = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
							date = m.group(1) + "-" + month + "-" + day;
						}
						bn.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=DS_01402_Detail&" + id;
						String title = a.text().trim();
						bn.id = id;
						bn.date = date;
						bn.title = title.replaceAll("<font(.*?)font>|\\s*", "");
						response.list.add(bn);
					}
				}
				if(doc.getElementById("__VIEWSTATE")!=null)
				{
					String __VIEWSTATE=doc.getElementById("__VIEWSTATE").attr("value");
					String form1=doc.getElementById("form1").attr("action");
					form1=form1.substring(form1.indexOf("?")+1);
					response.pageCount=super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_01402_List&" + form1+"&__VIEWSTATE="+__VIEWSTATE;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
