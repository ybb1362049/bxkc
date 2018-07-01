package DS_00238;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.reqrsp.utils.IwReqRspHelper;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.IO;

public class DS_00238_List extends TxtReqRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", url=" + url + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Document xml = Jsoup.parse(originTxtRspContent);
//				Element totalTag = xml.select("table[width=94%]").first();
				Elements eachTags = xml.select("li[class=title]:has(a)");
				for(Element eachTag : eachTags){
					Element ae = eachTag.select("a").first();
					String title = ae.attr("title").trim();
					if(title.length() < 1){
						title = ae.text().trim();
					}
					title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
					title = title.replaceAll("\\[[^\\]]+]", "");
					title = title.replaceAll("【.*】", "");
					String str = eachTag.text().trim();
					str = str.replaceAll("[.|/|年|月]", "-");
					String date = "";
					Matcher m = p.matcher(str);
					if(m.find()){
						date = sdf.format(sdf.parse(m.group()));
					}
					String id = ae.attr("href").trim();
					id = id.replace("?", "&");
//					id = id.substring(id.indexOf(".com/") + 4 , id.length());
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=DS_00238_Detail&iw_ir_1=" + id;
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.title = title;
					bn.url = url;
					response.list.add(bn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	// 只要标题含有str中的一些东西就定义为true
	public boolean any(String title, String str) {
		boolean flag = false;
		if (str.contains("、")) {
			String[] s = str.split("、");
			for (int i = 0; i < s.length; i++) {
				if (title.contains(s[i])) {
					flag = true;
					break;
				}
			}
		} else {
			if (title.contains("str")) {
				flag = true;
			}
		}
		return flag;
	}


	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String host = iwRequest.getHost();
			String path = iwRequest.getRequestPath();
			String pageIndex = iwRequest.getRequestPathParam("pageIndex");
			String pageSizes = iwRequest.getRequestPathParam("pageSizes");
			String siteItem = iwRequest.getRequestPathParam("siteItem");
			String infoType = iwRequest.getRequestPathParam("infoType");
			String query = iwRequest.getRequestPathParam("query");
			String param = "pageIndex=" + pageIndex + "&pageSizes=" + pageSizes 
					+ "&siteItem=" + siteItem + "&infoType=" + infoType + "&query=" + query;
//			String url = "http://" + host + path;
			String str = sendPost(host, path, param);
			iwr = new DefaultIwResponse(null, str.getBytes("utf-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwr;
	}

	private String sendPost(String host,String path,String param) {
		String respStr = "";
		try {
			HttpClient httpClient = IwReqRspHelper.getAApacheHttpClientThatTrustAllSSL();
			HttpPost postRequest = new HttpPost("http://" + host + path);
			postRequest.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			postRequest.setHeader("Accept-Encoding", "gzip, deflate");
			postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			postRequest.setHeader("Connection", "keep-alive");
			postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			postRequest.setHeader("Host", host);
			postRequest.setHeader("Origin", "http://" + host);
			postRequest.setHeader("Referer", "http://" + host + path);
			postRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36");
			postRequest.setHeader("X-Requested-With", "XMLHttpRequest");
			postRequest.setEntity(new StringEntity(param,"UTF-8"));
			HttpResponse response = httpClient.execute(postRequest);
			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
			respStr = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
}
