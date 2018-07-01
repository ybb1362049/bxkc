package SJ_03616;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_03616_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	
	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
				Elements scripts = xml.select("script");
				for(Element script : scripts){
					script.remove();
				}
				String title = xml.select("td[class=Color_04]").first().text().trim();
				if(title != null && title.length() > 0){
					response.title = title;
				}
				Element tde = xml.select("td[class=Color_05]").first();
				String content = response.title + tde.outerHtml();
				response.content = content;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return response;
	}

}
