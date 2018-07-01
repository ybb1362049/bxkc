package DS_00812;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DS_00812_ListCombine2 extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {

		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + "]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {

		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent, new TypeToken<Response>() {
			}.getType());
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
			conn.setConnectTimeout(TIMEOUT_IN_MS);// 设置连接主机超时
			conn.setReadTimeout(TIMEOUT_IN_MS);// 设置从主机读取数据超时
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
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT");
			String CategoryNum = iwRequest.getRequestPathParam("CategoryNum").trim();
			String sdParamsUrl = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00812_Params2" + "&CategoryNum=" + CategoryNum;
			String sdParamsList = callApi(sdParamsUrl);
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			String __VIEWSTATE = params.getString("__VIEWSTATE");
			String __EVENTVALIDATION = params.getString("__EVENTVALIDATION");
			String __VIEWSTATEGENERATOR = params.getString("__VIEWSTATEGENERATOR");
			String jxListCombine = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00812_List2" + "&CategoryNum=" + CategoryNum + "&__VIEWSTATE=" + __VIEWSTATE
					+ "&__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&__EVENTVALIDATION=" + __EVENTVALIDATION
					+ "&__EVENTARGUMENT=" + __EVENTARGUMENT;
			String result = callApi(jxListCombine);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;
	}

}
