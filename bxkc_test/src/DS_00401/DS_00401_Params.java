package DS_00401;

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

public class DS_00401_Params extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		Params params = new Params();
		
	}
	
	public class Params {
		String __VIEWSTATE;
//		String __CSRFTOKEN;
//		String __VIEWSTATEGENERATOR;
//		String __EVENTVALIDATION;
		@Override
		public String toString() {
			return "Params [ __VIEWSTATE=" + __VIEWSTATE 
					+ "]";
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
				
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
			
				response.params.__VIEWSTATE = xmlDoc.getElementById(
						"__VIEWSTATE").getAttribute("value");
//				response.params.__CSRFTOKEN=xmlDoc.getElementById(
//						"__CSRFTOKEN").getAttribute("value");
//				response.params.__VIEWSTATEGENERATOR=xmlDoc.getElementById(
//						"__VIEWSTATEGENERATOR").getAttribute("value");
//				response.params.__EVENTVALIDATION = xmlDoc.getElementById(
//						"__EVENTVALIDATION").getAttribute("value");
				
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
