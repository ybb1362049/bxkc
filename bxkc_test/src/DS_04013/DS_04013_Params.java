package DS_04013;

import org.w3c.dom.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_04013_Params extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();

	}

	public class Params {

		String __EVENTVALIDATION;
		// String __EVENTARGUMENT;
		String __VIEWSTATE;

		// String __EVENTTARGET;

		@Override
		public String toString() {
			return "Params [__EVENTVALIDATION=" + __EVENTVALIDATION + ", __VIEWSTATE=" + __VIEWSTATE + "]";
		}
	}

	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {

				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				response.params.__EVENTVALIDATION = xmlDoc.getElementById("__EVENTVALIDATION").getAttribute("value");
				response.params.__VIEWSTATE = xmlDoc.getElementById("__VIEWSTATE").getAttribute("value");
//				response.params.__EVENTARGUMENT = xmlDoc.getElementById("__EVENTARGUMENT").getAttribute("value");
				/*
				 * response.params.__EVENTTARGET = xmlDoc.getElementById(
				 * "__EVENTTARGET").getAttribute("value");
				 */

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(response.params.toString());
		return response;
	}

}
