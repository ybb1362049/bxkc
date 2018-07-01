package DS_00401;

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

public class DS_00401_Combine extends TxtReqRspHandler {

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

			return new String(retBytes, "GBK");
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
			String path=iwRequest.getRequestPath();
			String iw_ir_2 =path.substring(6,path.lastIndexOf("/"));// iwRequest.getRequestPathParam("CategoryNum")
			String __VIEWSTATE;
			String __EVENTTARGET=iwRequest.getRequestContentParam("__EVENTTARGET").trim();
			String __EVENTARGUMENT=iwRequest.getRequestContentParam("__EVENTARGUMENT").trim();
			String sdParamsUrl = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()  + "&iw-cmd=DS_00401_Params"
					+"&iw_ir_2="+iw_ir_2
//					+"&iw_ir_4="+iw_ir_4
					+"&CategoryNum="+CategoryNum;
			////System.out.println(sdParamsUrl);
			String sdParamsList = Utils.callApi(sdParamsUrl);
			////System.out.println(sdParamsList + "*******************");
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			// //System.out.println(params.get("__CSRFTOKEN"));
			sdParamsList = getJson(sdParamsList, "params");
			__VIEWSTATE = params.getString("__VIEWSTATE");
			String ChangZhouListCombine = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00401_List"
					//+"&__CSRFTOKEN="+__CSRFTOKEN
					+"&__VIEWSTATE=" + __VIEWSTATE
//					+ "&__EVENTVALIDATION="+__EVENTVALIDATION
//					+"&__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR
//					+ "&ctl00_myTreeView_ExpandState="+ctl00_myTreeView_ExpandState
					+"&__EVENTARGUMENT="+__EVENTARGUMENT
                    +"&__EVENTTARGET"+__EVENTTARGET
                    +"&iw_ir_2="+iw_ir_2
//					+"&iw_ir_4="+iw_ir_4
					+"&CategoryNum="+CategoryNum;
//					+"&ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum="+ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum;
			////System.out.println(ChangZhouListCombine);
			String result = Utils.callApi(ChangZhouListCombine);
			////System.out.println(result);
			iwRsp = new DefaultIwResponse(null, result.getBytes("GBK"), null,
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
