package SJ_03616;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


public class SJ_03616_List extends TxtRspHandler {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=找到)\\d+");
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
				Elements tables = xml.select("table[class=xhx][height=28]");
				for(Element table : tables){
					Element ae = table.select("a").first();
					Element tde = table.select("td[width=20%]").first();
					String title = ae.text().trim();
					String id = ae.attr("href");
					id = id.substring(id.indexOf("?") + 1,id.length());
					String da = tde.text().trim().replace("/", "-").replace("/", "-").replace("日", "");
					Date d = sdf.parse(da);
					String date = sdf.format(d);
					BranchNew bn = new BranchNew();
					if(title != null && date != null && title.length() > 0){
						bn.date = date;
						bn.id = id;
						bn.title = title;
						response.list.add(bn);
					}
				}
				Element selecte = xml.select("select[class=inpud]").last();
				String page = selecte.select("option").last().text().trim();
				response.pageCount = page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
