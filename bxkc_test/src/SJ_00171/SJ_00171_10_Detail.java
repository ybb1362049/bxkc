package SJ_00171;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_00171_10_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		try {
			Document doc = Jsoup.parse(arg1);
			doc.select("script").remove();
			doc.select("div.property2 > span").remove();
			Element content =doc.select("div.articleCon").get(0);
			Elements a_list =content.select("a[href]");
			for(int i=0;i<a_list.size();i++){
				if(a_list.get(i).attr("href").contains("pdf")||
						a_list.get(i).attr("href").contains("doc")||
						a_list.get(i).attr("href").contains("rar")||
						a_list.get(i).attr("href").contains("zzip")){
					String href =a_list.get(i).attr("href");
					content.select("a[href]").get(i).attr("href","http://www.gszfcg.gansu.gov.cn/"+href);
				}
			}
			response.title = doc.select("h2.title").text().trim();
			response.content=content.html();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}