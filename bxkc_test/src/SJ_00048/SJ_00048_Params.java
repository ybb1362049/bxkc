package SJ_00048;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_00048_Params extends TxtRspHandler {

	public static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=/)\\d+");
	private static Matcher m;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String content;
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
		return rsp;
	}

//	@Override
//	public IwResponse sendIwRequest(IwRequest iwRequest) {
//		IwResponse iwRsp = null;
//		try {
//			String path = iwRequest.getRequestContentParam("cur");
//			String url = "http://www.lnjzxy.com/web/guest/home?p_p_id=optionGroup_INSTANCE_dlcV&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_optionGroup_INSTANCE_dlcV_struts_action=%2Fext%2FoptionGroup%2Fshowjournal&_optionGroup_INSTANCE_dlcV_timer=1441440572071&_optionGroup_INSTANCE_dlcV_advancedSearch=false&_optionGroup_INSTANCE_dlcV_andOperator=true&_optionGroup_INSTANCE_dlcV_groupId=14&_optionGroup_INSTANCE_dlcV_version=0.0&_optionGroup_INSTANCE_dlcV_status=approved&_optionGroup_INSTANCE_dlcV_organizationId=0&delta=20&cur=" + path;
//			String str = getContent(url);
//			iwRsp = new DefaultIwResponse(null, str.getBytes("UTF-8"), null,0, "ok");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return iwRsp;
//	}
	
	public static String getContent(String path){
		String respStr = "";
		HttpClient client = new DefaultHttpClient();
		HttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("Content-type", "text/html; charset=utf-8");
		httpGet.setHeader("Accept", "text/html");
		try {
			res = client.execute(httpGet);
			int statusCode = res.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				respStr = EntityUtils.toString(res.getEntity());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				response.content = originTxtRspContent;
//				Document xml = Jsoup.parse(originTxtRspContent);
//				Element dive = xml.select("div[class=yahoo]").first();
//				Elements divs = dive.select("div[class=xxei]:has(a)");
//				for(Element div : divs){
//					Element ae = div.select("a").first();
//					Element spane = div.select("span[class=sjej]").first();
//					String id = ae.attr("onclick");
//					id = id.substring(id.indexOf("xwbh"),id.indexOf("';return"));
//					String title = ae.text().trim();
//					String date = spane.text().trim();
//					if(title != null && title.length() > 0){
//						BranchNew bn = new BranchNew();
//						bn.date = date;
//						bn.id = id;
//						bn.title = title;
//						response.list.add(bn);
//					}
//				}
//				String str = xml.select("div[class=list-page-detail]").first().text();
//				m = p.matcher(str);
//				if(m.find()){
//					String page = m.group();
//					response.pageCount = page;
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
