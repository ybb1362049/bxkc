package SJ_00171;

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

public class SJ_00171_10_List extends TxtRspHandler {
	public class BranchNew {
		String title;
		String id;
		String url;
		String date;
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		Pattern p  =Pattern.compile("\\d+");
		if (arg0 == RspState.Login) {
			try {
				Document doc = Jsoup.parse(arg1);
				Elements li_list = doc.select("ul.newsList > li");
				for (Element li : li_list) {
					if (li.select("span").size() > 0) {
						BranchNew n = new BranchNew();
						String date = li.select("span.date").html().trim();
						String title = li.select("span >  a").html().trim();
						String id = li.select("span >  a").attr("href");
						n.id = id;
						n.title = title;
						n.date = date;
						response.list.add(n);
					}
				}
				if(doc.select("span.pagecss").size()>0){
					String page =doc.select("span.pagecss").html();
					Matcher m =p.matcher(page);
					if(m.find()){
						page=m.group();
						page=page.substring(0,page.length()-1);
						response.pageCount=page;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}