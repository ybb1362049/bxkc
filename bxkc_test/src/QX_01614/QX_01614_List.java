package QX_01614;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_01614_List extends TxtRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	private static Pattern p = Pattern.compile("\"\\./(.*?)\";");
	private static Pattern page = Pattern.compile("createPageHTML\\((\\d+),");

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
				Element dive = doc.select("div[class=page]").first();
				if (dive != null) {
					m = page.matcher(dive.outerHtml());
					if (m.find()) {
						response.pageCount = m.group(1);
					}
					dive.remove();
				}
				Element div = doc.select("div[class=NewsList]").last();
				if (div != null) {
					m=p.matcher(div.outerHtml());
					while(m.find())
					{
						BranchNew bn = new BranchNew();
						String id=m.group(1);
						bn.id=id;
						bn.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
						+ "&iw-cmd=QX_01614_Detail&iw_ir_2=" + id;
						response.list.add(bn);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
