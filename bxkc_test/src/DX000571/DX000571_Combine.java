package DX000571;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

import com.google.gson.Gson;

public class DX000571_Combine extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {

		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + ";url=" + url + "]";
		}

	}

	protected RspState checkTxtRspContentState(String originTxtRspContent) {

		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent, Response.class);
			response.list = result.list;
			response.pageCount = result.pageCount;
		}

		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);

			return new String(retBytes, "UTF-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwRsp = null;
		try {

			String Code = iwRequest.getRequestPathParam("Code").trim();

			String sdParamsUrl = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr() + "&iw-cmd=DX000571_Params"
					+ "&Code=" + Code;
			String sdParamsList = callApi(sdParamsUrl);
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			sdParamsList = getJson(sdParamsList, "params");

			String __VIEWSTATE = params.getString("__VIEWSTATE");// 静态参数
			String __EVENTVALIDATION = params.getString("__EVENTVALIDATION");// 静态参数

			String listPage = iwRequest.getRequestContentParam("listPage").trim();// 动态参数
			String ChangZhouListCombine = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
					+ "&iw-cmd=DX000571_List" + "&Code=" + Code + "&__VIEWSTATE=" + __VIEWSTATE
			+ "&__EVENTVALIDATION=" + __EVENTVALIDATION
					+ "&listPage=" + listPage;
			String result = callApi(ChangZhouListCombine);

			iwRsp = new DefaultIwResponse(null, result.getBytes("GBK"), null, 0, "ok");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;
	}

	public String getJson(String Content, String ID) {
		String resultStr = "";
		try {
			if (Content.substring(0, 1) != "[") {
				Content = "[" + Content + "]";
			}

			JSONArray jsonArray = new JSONArray(Content);
			resultStr = jsonArray.getJSONObject(0).toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultStr;
	}
}
