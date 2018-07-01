package SJ_04254;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_04254_Params extends TxtRspHandler {

	public class Params {
		String __VIEWSTATE;
		String __VIEWSTATEGENERATOR;
	}

	public class Response extends TxtBaseResponse {
		Params params = new Params();
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
				Document document = Jsoup.parse(originTxtRspContent);
				response.params.__VIEWSTATE = document.select("#__VIEWSTATE").first().attr("value");
				response.params.__VIEWSTATEGENERATOR = document.select("#__VIEWSTATEGENERATOR").first().attr("value");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
