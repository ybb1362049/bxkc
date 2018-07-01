package DX000277;

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

public class DX000277_List extends TxtRspHandler {

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
				Elements divs = xml.select("div[class=c1-bline]");
				for(Element dive : divs){
					Element ae = dive.select("a").first();
					String id = ae.attr("href");
					id = id.substring(id.indexOf(".com") + 4,id.length());
					String title = ae.attr("title");
					String date = dive.select("div[class=f-right]").first().text().trim();
					if(title != null && title.length() > 0){
						BranchNew bn = new BranchNew();
						bn.id = id;
						bn.date = date;
						bn.title = title;
						response.list.add(bn);
//						System.out.println(bn.toString());
					}
				}
				String str = xml.select("span[class=total]").first().text();
				m = p.matcher(str);
				if(m.find()){
					response.pageCount = m.group();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
