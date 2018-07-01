package GJ_03525;


import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;


public class GJ_03525_Params extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();

	}

	public class Params {
		String __VIEWSTATEGENERATOR;
		String __VIEWSTATE;
		String __EVENTTARGET;
		String __EVENTARGUMENT;

		@Override
		public String toString() {
			return "Params [__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR 
					+";__VIEWSTATE=" + __VIEWSTATE 
					+ ";__EVENTTARGET=" + __EVENTTARGET 
					+ ";__EVENTARGUMENT=" + __EVENTARGUMENT+ "]";
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
				response.params.__EVENTTARGET = xmlDoc.getElementById(
						"__EVENTTARGET").getAttribute("value");
				response.params.__EVENTARGUMENT = xmlDoc.getElementById(
						"__EVENTARGUMENT").getAttribute("value");
				//System.out.println(response.params.toString());
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		////System.out.println(response.params.toString());
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
