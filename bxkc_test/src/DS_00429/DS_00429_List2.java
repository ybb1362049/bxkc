package DS_00429;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_00429_List2 extends TxtRspHandler {
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

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Element xmlDoc = Jsoup.parse(originTxtRspContent);
			Elements lis = xmlDoc.select("ul.ewb-palte-item > li");
			List<BranchNew> list = new ArrayList<BranchNew>();
			for (int i = 0; i < lis.size(); i++) {
				BranchNew branchNew = new BranchNew();
				Element aElement = lis.get(i).select("a").first();
				branchNew.title = aElement.attr("title").replaceAll("(.*?)\\[.*?]$", "$1").replaceAll("(\\[.*\\])|(【.*】)(.*)", "$3");
				branchNew.id = aElement.attr("href").replaceAll("\\?", "&").replace("InfoID", "infoid").replace("InfoDetail", "infodetail");
				branchNew.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=DS_00429_Detail&iw_ir_1=" + branchNew.id;
				branchNew.date = lis.get(i).select("span").last().text();
				list.add(branchNew);
			}
			response.list = list;
			if (xmlDoc.select("div.pagemargin").first() != null) {
				String page = xmlDoc.select("div.pagemargin").first().text();
				Pattern pattern = Pattern.compile("\\d+/(\\d+)");
				Matcher matcher = pattern.matcher(page);
				if (matcher.find()) {
					response.pageCount = matcher.group(1);
				}
			}
		}
		return response;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

}
