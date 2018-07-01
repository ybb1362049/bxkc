package DS_00444;

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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class DS_00444_Combine extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {

		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent,
					new TypeToken<Response>() {
					}.getType());

			response.list = result.list;
			response.pageCount=result.pageCount;
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
			String CategoryNum = iwRequest.getRequestPathParam("CategoryNum").trim();
			String __EVENTARGUMENT=iwRequest.getRequestContentParam("__EVENTARGUMENT").trim();
			String type = iwRequest.getRequestPath();
			type = type.substring(type.indexOf("ztbzx/")+6,type.indexOf("/MoreInfo"));
			String sdParamsUrl = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()  + "&iw-cmd=DS_00444_Params"
					+ "&iw_ir_2="+type + "&CategoryNum="+CategoryNum;
			String sdParamsList = callApi(sdParamsUrl);
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			sdParamsList = getJson(sdParamsList, "params");
			String __VIEWSTATE = params.getString("__VIEWSTATE");
			String ChangZhouListCombine = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00444_List&__VIEWSTATE=" + __VIEWSTATE
					+ "&__EVENTARGUMENT=" + __EVENTARGUMENT
					+ "&iw_ir_2=" + type
					+ "&CategoryNum=" + CategoryNum;
			String result = callApi(ChangZhouListCombine);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null,0, "ok");
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
			e.printStackTrace();
		}
		return resultStr;
	}
}
