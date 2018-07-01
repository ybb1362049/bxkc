package DS_00812;
import org.w3c.dom.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.Utils;

public class DS_00812_Params2 extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();
	}
	public class Params {
		String __VIEWSTATE;
		String __VIEWSTATEGENERATOR;
		String __EVENTVALIDATION;
		@Override
		public String toString() {
			return "Params [__VIEWSTATE=" + __VIEWSTATE + ", __VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR
					+ ", __EVENTVALIDATION=" + __EVENTVALIDATION + "]";
		}
	}

	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				originTxtRspContent = "<table>" + originTxtRspContent
						+ "</table>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				response.params.__VIEWSTATEGENERATOR = xmlDoc.getElementById(
						"__VIEWSTATEGENERATOR").getAttribute("value");
				response.params.__VIEWSTATE = xmlDoc.getElementById(
						"__VIEWSTATE").getAttribute("value");
				response.params.__EVENTVALIDATION = xmlDoc.getElementById(
						"__EVENTVALIDATION").getAttribute("value");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
