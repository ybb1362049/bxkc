package SJ_04332;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_04332_List extends TxtRspHandler {
	

	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				Elements li_ee = document.select("td[style=PADDING-BOTTOM: 10px; PADDING-LEFT: 10px; PADDING-RIGHT: 10px; PADDING-TOP: 10px] a");
				String id = null, title = null;
				for(int i =1;i<li_ee.size();i=i+2){
					Element li =li_ee.get(i);
					id = li.attr("href");
					title = li.attr("title");
					id = id.substring(id.indexOf("?")+1);
					BranchNew bNew = new BranchNew();
					bNew.id = id;
					bNew.title = title;
					response.list.add(bNew);
				}
				response.pageCount = "1";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
