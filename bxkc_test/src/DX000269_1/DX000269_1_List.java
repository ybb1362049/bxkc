package DX000269_1;

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

public class DX000269_1_List extends TxtRspHandler {

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
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try{
				Document xml = Jsoup.parse(originTxtRspContent);
				Elements tables = xml.select("table[class=newzx3]");
				for(Element table : tables){
					Element ae = table.select("a").first();
					String id = ae.attr("href");
					id = id.substring(id.indexOf("?") + 1,id.length());
					String title = ae.text().trim();
					String date = table.select("span[class=inter4]").first().text().trim();
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.title = title;
					response.list.add(bn);
				}
				m = p.matcher(xml.select("span[class=fanye_tt]").first().text());
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

