package SJ_04016_1;

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

public class SJ_04016_1_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=共)\\d+(?=页)");
	private static Matcher m;
	
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

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
				Document xml = Jsoup.parse(originTxtRspContent);
				Element dive = xml.select("div[class=contenttop right_top]").first();
				Elements trs = xml.select("tr[height=25]");
				for(Element tre : trs){
					Element ae = tre.select("a").first();
					String id = ae.attr("href");
					id = id.substring(id.indexOf("?") + 1,id.length());
					String title = ae.attr("title");
					String date = tre.select("td[width=100]").first().text().trim();
					if(title != null && title.length() > 0){
						BranchNew bn = new BranchNew();
						bn.id = id;
						bn.date = date;
						bn.title = title;
						response.list.add(bn);
//						System.out.println(bn.toString());
					}
				}
				String page = xml.select("td[class=huifont]").first().text();
				page = page.substring(page.indexOf("/") + 1,page.length());
				response.pageCount = page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
