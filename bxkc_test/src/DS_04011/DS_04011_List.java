package DS_04011;

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

public class DS_04011_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=/)\\d+(?=页)");
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
				Element ule = xml.select("ul[class=z_list3]").first();
				Elements lis = ule.select("li:has(a[href~=.*.cn/\\w+/\\d+.*])");
				for(Element lie : lis){
					Element ae = lie.select("a").first();
					String id = ae.attr("href");
					id = id.substring(id.indexOf(".cn/") + 3,id.length());
					String title = ae.attr("title");
					String date = lie.select("span").first().text().trim();
					if(title != null && title.length() > 0){
						BranchNew bn = new BranchNew();
						bn.id = id;
						bn.date = date;
						bn.title = title;
						response.list.add(bn);
//						System.out.println(bn.toString());
					}
				}
				String str = xml.select("table[class=noBorder]").first().text().trim();
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
