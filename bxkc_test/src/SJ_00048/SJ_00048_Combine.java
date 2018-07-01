package SJ_00048;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.IO;

import com.google.gson.Gson;

public class SJ_00048_Combine extends TxtReqRspHandler {

	private static RspState rsp = RspState.Login;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Pattern p = Pattern.compile("(?<=/)\\d+");
//	private static Pattern p2 = Pattern.compile("\\d+(?=/\\d+)");
	private static Pattern p2 = Pattern.compile("(?<=articleId=)\\d+");
	private static Pattern p1 = Pattern.compile("\\d{4}(-|年)\\d{1,2}(-|月)\\d{1,2}");
	private static Matcher m;
	private static String cookie = null;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
        String url;
		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwRsp = null;
		try {
			if(cookie == null){
				String paramUrl = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=SJ_00048_Params";
				getContent(paramUrl);
				cookie = super.getContextInfo(Context.API_SESSION);
			}
			// TODO: 取参数
			String path = iwRequest.getRequestPath();
			String lbbh = iwRequest.getRequestContentParam("lbbh");
			String pageNo = iwRequest.getRequestContentParam("xwzsPage.pageNo");
			String LBBH = iwRequest.getRequestContentParam("xwzsPage.LBBH");
//			String lbbh = iwRequest.getRequestContentParam("lbbh");
			String listUrl = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=SJ_00048_List&iw_ir_1=" + path
							+ "&lbbh=" + lbbh + "&xwzsPage.LBBH=" + LBBH
							+ "&xwzsPage.pageNo=" + pageNo;
			String str = getContent(listUrl);
			iwRsp = new DefaultIwResponse(null, str.getBytes("UTF-8"), null,0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;
	}
	
	public static String getContent(String path){
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("Content-type", "text/html; charset=utf-8");
		httpGet.setHeader("Accept", "text/html");
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
	
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent,Response.class);
			response.list = result.list;
			response.pageCount=result.pageCount;
		}
		return response;
	}

}
