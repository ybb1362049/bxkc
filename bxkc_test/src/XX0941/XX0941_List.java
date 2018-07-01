package XX0941;

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

public class XX0941_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", url=" + url + "]";
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Document xml = Jsoup.parse(originTxtRspContent);
//				Element totalTag = xml.select("div[class=list-right-list clearfix]").first();
				Elements eachTags = xml.select("div.articleList:has(a)");
				for(Element eachTag : eachTags){
					Element ae = eachTag.select("a").first();
					String title = ae.attr("title").trim();
					if(title.length() < 1){
						title = ae.text().trim();
					}
					title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
					title = title.replaceAll("\\[[^\\]]+]", "");
					if(title.length() < 2){
						continue;
					}
					String str = eachTag.text().trim();
					str = str.replaceAll("[.|/|年|月]", "-");
					String date = "";
					Matcher m = p.matcher(str);
					if(m.find()){
						date = sdf.format(sdf.parse(m.group()));
					}
					String id = ae.attr("href").trim();
//					if(id.startsWith("../")){
//						id = "/" + id.replace("../", "");
//					}
//					id = id.substring(id.indexOf("?") + 1 , id.length());
//					id = id.replace("?", "&");
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=XX0681_Detail&iw_ir_1=" + id;
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.title = title;
					bn.url = url;
					response.list.add(bn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
