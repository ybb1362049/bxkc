package DS_02836;

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

public class DS_02836_List extends TxtRspHandler {
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
		try {
			if (rspState == RspState.Login) {
				Element xmlDoc = Jsoup.parse(originTxtRspContent);
				Elements lis = xmlDoc.select(" ul > li");
				List<BranchNew> list = new ArrayList<BranchNew>();
				for (int i = 0; i < lis.size(); i++) {
					BranchNew branchNew = new BranchNew();
					Element aElement = lis.get(i).select("a").first();
					branchNew.title = aElement.text();
					branchNew.id = aElement.attr("href").substring(aElement.attr("href").indexOf("?")+1,aElement.attr("href").length()-2);
					branchNew.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=DS_02836_Detail&" + branchNew.id;
					branchNew.date = lis.get(i).select("span").last().text().split(" ")[0];
					list.add(branchNew);
				}
				response.list = list;
				if (xmlDoc.select("a[onclick~=goPage\\('last'\\)]").first() != null) {
					response.pageCount = xmlDoc.select("a[onclick~=goPage\\('last'\\)]").first().text();
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
