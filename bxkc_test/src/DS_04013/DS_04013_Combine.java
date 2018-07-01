package DS_04013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

public class DS_04013_Combine extends TxtReqRspHandler {

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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
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

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwRsp = null;
		try {
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT").trim();
			String __EVENTTARGET = iwRequest.getRequestContentParam("__EVENTTARGET").trim();
			String ctl00$ddlsearch = iwRequest.getRequestContentParam("ctl00$ddlsearch").trim();
			String ctl00$ContentPlaceContent$Pager_input = iwRequest.getRequestContentParam("ctl00$ContentPlaceContent$Pager_input").trim();
			String Type = iwRequest.getRequestPathParam("Type");
			String GNW = iwRequest.getRequestPathParam("GNW");
			String sdParamsUrl = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
							+ "&iw-cmd=DS_04013_Params" + "&Type=" + Type + "&GNW=" + GNW;

			String sdParamsList = getContent(sdParamsUrl);
//			 System.out.println(sdParamsList + "*******************");
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			sdParamsList = getJson(sdParamsList, "params");
			String __VIEWSTATE = params.getString("__VIEWSTATE");
			String __EVENTVALIDATION = params.getString("__EVENTVALIDATION").trim();
			String ChangZhouListCombine = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
									+ "&iw-cmd=DS_04013_List"
									+ "&Type=" + Type + "&GNW=" + GNW
									+ "&__VIEWSTATE=" + __VIEWSTATE
									+ "&__EVENTVALIDATION=" + __EVENTVALIDATION
									+ "&ctl00$ddlsearch=" + ctl00$ddlsearch
									+ "&ctl00$ContentPlaceContent$Pager_input=" + ctl00$ContentPlaceContent$Pager_input
									+ "&__EVENTTARGET=" + __EVENTTARGET
									+ "&__EVENTARGUMENT=" + __EVENTARGUMENT;

			String result = getContent(ChangZhouListCombine);

			iwRsp = new DefaultIwResponse(null, result.getBytes("utf-8"), null, 0, "ok");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;

	}

	public static String getContent(String path) {
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=utf-8");
		httpGet.addHeader("Accept", "text/html");
		try {
			res = client.execute(httpGet);
			int statusCode = res.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = res.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
				StringBuilder sb = new StringBuilder();
				while ((respStr = reader.readLine()) != null) {
					sb.append(respStr);
				}
				respStr = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respStr;
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
