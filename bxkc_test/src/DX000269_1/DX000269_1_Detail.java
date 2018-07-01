package DX000269_1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000269_1_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}
	
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xml = Jsoup.parse(originTxtRspContent);
				Elements scripts = xml.select("script");
				for(Element script : scripts){
					script.remove();
				}
				String title = xml.getElementById("MsgTitle").text().trim();
				response.title = title;
				Element dive = xml.getElementById("Label1");
				response.content = dive.outerHtml();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}

