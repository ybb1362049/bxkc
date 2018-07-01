package SJ_04254;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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

public class SJ_04254_ListCombine extends TxtReqRspHandler {

	private static final int TIMEOUT_IN_MS = 100000;

	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	private static String callApi(String path) {
		byte[] retBytes = new byte[0];
		String result = "";
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			// 获取调用List和Parmas接口所需的参数
			String type =iwRequest.getRequestPathParam("type");
			// 调用Params接口
			String paramsUrl = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=SJ_04254_Params&type="+type;
			String paramsJsonStr = callApi(paramsUrl);
			// 从Params接口返回的JSON中解析出调用List接口时要用到的参数
			JSONObject paramsJsonObj = new JSONObject(paramsJsonStr);
			JSONObject jsonObj = paramsJsonObj.getJSONObject("params");
			String __VIEWSTATE = jsonObj.getString("__VIEWSTATE");
			String __VIEWSTATEGENERATOR = jsonObj.getString("__VIEWSTATEGENERATOR");
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT");
			// 调用List接口
			String listUrl = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=SJ_04254_List"
					+ "&type=" + type
					+ "&__VIEWSTATE=" + __VIEWSTATE 
					+ "&__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR 
					+ "&__EVENTTARGET=" + "ctl00$ContentPlaceHolder1$AspNetPager1" 
					+ "&__EVENTARGUMENT=" + __EVENTARGUMENT;
			String respStr = callApi(listUrl);
			// 生成返回结果对象
			iwResponse = new DefaultIwResponse(null, respStr.getBytes("UTF-8"), Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response rsp = gson.fromJson(originTxtRspContent, Response.class);
			response.list = rsp.list;
			response.pageCount = rsp.pageCount;
		}
		return response;
	}

}
