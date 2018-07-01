package GJ_03525;


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
import cn.internetware.utils.Utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class GJ_03525_Combine extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {

		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";url=" + url+ "]";
		}

	}


	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {

		return RspState.Login;
		
	}



	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent,
					new TypeToken<Response>() {
					}.getType());

			response.list = result.list;
			response.pageCount=result.pageCount;
			//response.currentPage = result.currentPage;
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
			
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT").trim();
			
			String sdParamsUrl = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()  + "&iw-cmd=GJ_03525_Params"+ "&iw_ir_2="+CategoryNum +"&CategoryNum="+CategoryNum;
			
			String sdParamsList = Utils.callApi(sdParamsUrl);
			// //System.out.println(sdParamsList + "*******************");
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
		
			// //System.out.println(params.get("__VIEWSTATEGENERATOR"));
			String __VIEWSTATEGENERATOR=params.getString("__VIEWSTATEGENERATOR");
			String __VIEWSTATE=params.getString("__VIEWSTATE");
			//__VIEWSTATEGENERATOR = params.getString("__VIEWSTATEGENERATOR");
			//__EVENTTARGET = params.getString("__EVENTTARGET");
			//__EVENTARGUMENT = params.getString("__EVENTARGUMENT");
			// pageToken = params.getString("__VIEWSTATEGENERATOR");
			String jxListCombine = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()
					+ "&iw-cmd=GJ_03525_List"
					+ "&__VIEWSTATE=" + __VIEWSTATE
					+ "&__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR
					+"&_EVENTTARGET=MoreInfoList1$Pager"
					+"&__EVENTARGUMENT="+ __EVENTARGUMENT
					+ "&iw_ir_2="+CategoryNum;
			////System.out.println(jxListCombine);
			String result = Utils.callApi(jxListCombine);
			// //System.out.println(result);
			iwRsp = new DefaultIwResponse(null, result.getBytes("GB2312"), null,
					0, "ok");

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
