package SJ_04333;

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

public class SJ_04333_Params extends TxtRspHandler {
	private SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Pattern p_page = Pattern.compile("共\\s*(\\d+)\\s*页");
	private Matcher m;
	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				Element list_ee = document.select("ul[class=newslist]").last();
				Elements li_ee = list_ee.select("li");
				String id = null, title = null, date = null;
				for(int i =0;i<li_ee.size();i++){
					Element li =li_ee.get(i);
					Element a_ee = li.select("a").first();
					id = a_ee.attr("href");
					id = id.substring(id.lastIndexOf("?")+1);
					title = a_ee.text().trim();
					date = li.text().trim();
					date = date.replaceAll(".*(\\d{4})[\\年|\\-|\\/|\\.](\\d{1,2})[\\月|\\/|\\-|\\.](\\d{1,2})[\\日|\\-|\\/|\\.]?.*", "$1-$2-$3");
					date = newsdf.format(newsdf.parse(date));	
					BranchNew bNew = new BranchNew();
					bNew.id = id;
					bNew.title = title;
					bNew.date = date;
					response.list.add(bNew);
				}
				
				String __VIEWSTATE = document.select("#__VIEWSTATE").first().attr("value");
				String __VIEWSTATEGENERATOR = document.select("#__VIEWSTATEGENERATOR").first().attr("value");
				String __EVENTVALIDATION = document.select("#__EVENTVALIDATION").first().attr("value");
				
				
				response.nextPage=super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=SJ_04333_List&__VIEWSTATE="+__VIEWSTATE+"&__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR+"&__EVENTVALIDATION="+__EVENTVALIDATION;
				
				response.pageCount = "1";
				Element page_ee = document.select("div[id=page2]").first();
				if (page_ee != null) {
					m = p_page.matcher(page_ee.text());
					if (m.find()) {
						response.pageCount = m.group(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
