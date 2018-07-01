package SJ_00048;

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

public class SJ_00048_List extends TxtRspHandler {

	public static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=/)\\d+");
	private static Matcher m;
	
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
				Document xml = Jsoup.parse(originTxtRspContent);
				Element dive = xml.select("div[class=yahoo]").first();
				Elements divs = dive.select("div[class=xxei]:has(a)");
				for(Element div : divs){
					Element ae = div.select("a").first();
					Element spane = div.select("span[class=sjej]").first();
					String id = ae.attr("onclick");
					id = id.substring(id.indexOf("xwbh"),id.indexOf("';return"));
					String title = ae.text().trim();
					String date = spane.text().trim();
					if(title != null && title.length() > 0){
						BranchNew bn = new BranchNew();
						bn.date = date;
						bn.id = id;
						String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
						+ "&iw-cmd=SJ_00048_Detail&" + id;
						bn.url=url;
						bn.title = title;
						response.list.add(bn);
					}
				}
				String str = xml.select("div[class=list-page-detail]").first().text();
				m = p.matcher(str);
				if(m.find()){
					String page = m.group();
					response.pageCount = page;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
