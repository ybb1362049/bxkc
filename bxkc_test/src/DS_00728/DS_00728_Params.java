package DS_00728;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00728_Params extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();
	}

	public class Params {
		String __VIEWSTATE;
		String __EVENTTARGET;

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtResContent) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtResContent) {
		// TODO Auto-generated method stub
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xmlDoc = Jsoup.parse(originTxtResContent);
				String __VIEWSTATE = xmlDoc.select("input[name=__VIEWSTATE]").first().attr("value");
				String __EVENTTARGET = xmlDoc.select("input[name=__EVENTTARGET]").first().attr("value");
				response.params.__VIEWSTATE = __VIEWSTATE;
				response.params.__EVENTTARGET = __EVENTTARGET;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
