package DX000281;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000281_List extends TxtRspHandler {
	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id +", url=" + url + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		try {
			if (rspState == RspState.Login) {
				Element xmlDoc = Jsoup.parse(originTxtRspContent);
				Elements lis = xmlDoc.select("div.news_box ul.list > li");
				List<BranchNew> list = new ArrayList<BranchNew>();
				for (int i = 0; i < lis.size(); i++) {
					BranchNew branchNew = new BranchNew();
					Element aElement = lis.get(i).select("a").first();
					branchNew.title = aElement.attr("title");
					branchNew.id = aElement.attr("href");
					String string[]=branchNew.id.split("\\?");
					branchNew.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=DX000281_Detail&iw_ir_1=" + string[0]+"&iw_ir_3="+string[1];
					list.add(branchNew);
				}
				response.list = list;
				if (xmlDoc.select("span.page-bottom").first() != null) {
					response.pageCount = xmlDoc.select("span.page-bottom").first().text().replaceAll(".*?共(\\d+)页。*", "$1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

}
