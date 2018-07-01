package DX000269_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
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

public class DX000269_1_ListCombine extends TxtReqRspHandler {

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

	@Override
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
			// 得到Params的跳转页码
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT");
			String c_kind = iwRequest.getRequestPathParam("c_kind");
			String c_kind2 = iwRequest.getRequestPathParam("c_kind2");
			String c_kind3 = iwRequest.getRequestPathParam("c_kind3");
			String sdParamsUrl = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr() + "&iw-cmd=DX000269_1_Params"
					+ "&c_kind2=" + c_kind2 + "&c_kind=" + c_kind + "&c_kind3=" + c_kind3;
			String sdParamsList = getContent(sdParamsUrl);// 访问Params初始页
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");// 从0069-1_Params得到并转换json格式的数据
			String __VIEWSTATE = params.getString("__VIEWSTATE");
			String __EVENTVALIDATION = params.getString("__EVENTVALIDATION");
			String jxListCombine = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
								+ "&iw-cmd=DX000269_1_List" + "&__VIEWSTATE=" + __VIEWSTATE
								+ "&__EVENTARGUMENT=" + __EVENTARGUMENT
								+ "&__EVENTVALIDATION=" + __EVENTVALIDATION
								+ "&c_kind=" + c_kind
								+ "&c_kind2=" + c_kind2 + "&c_kind3=" + c_kind3;
			String result = getContent(jxListCombine);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
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
	
	public static String getContent(String path){
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=utf-8");
		httpGet.addHeader("Accept", "text/html");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
		httpGet.setConfig(requestConfig);
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
		} catch(Exception e) {
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
}
