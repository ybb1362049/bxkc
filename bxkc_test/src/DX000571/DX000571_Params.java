package DX000571;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DX000571_Params extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();
	}

	public class Params {

		String __EVENTVALIDATION;
		String __VIEWSTATE;

	}

	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xmlDoc = Jsoup.parse(originTxtRspContent);
				response.params.__VIEWSTATE = xmlDoc.select("#__VIEWSTATE").first().val();
				response.params.__EVENTVALIDATION = xmlDoc.select("#__EVENTVALIDATION").first().val();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
