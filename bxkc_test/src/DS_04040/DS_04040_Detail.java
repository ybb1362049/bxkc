package DS_04040;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.IO;

public class DS_04040_Detail extends TxtReqRspHandler {

//	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");
	
	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String path = "http://www.lsz.gov.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				String title = "";
				Element titleTag = xml.getElementById("xxh1");
				if(titleTag != null){
					title = titleTag.text().trim();
					title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
					title = title.replaceAll("\\[[^\\]]+]", "");
					title = title.replaceAll("【.*】", "");
					response.title = title;
				}
				
				Element conTag = xml.getElementById("xilan_cont");
//				Element conTag = xml.select("table[width=887]").first();
//				Element totalTag = xml.select("table[class=sj_bt]").first();
				
				Elements scripts = conTag.select("script");
				for (Element script : scripts) {
					script.remove();
				}
				Elements metas = conTag.select("meta");
				for (Element metae : metas) {
					metae.remove();
				}
				
				// 补全附件的链接
				Elements as = conTag.select("a");
				for (Element ae : as) {
					String href = ae.attr("href");
					if (!"#".equals(href) && !href.startsWith("http") && href.length() > 0 && !href.startsWith("HTTP")) {
						if (href.startsWith("/")) {
							href = path + href;
						} else {
							href = path + "/" + href;
						}
						ae.attr("href", href);
					}
				}

				Elements imgs = conTag.select("img");
				for (Element imge : imgs) {
					String src = imge.attr("src");
					if (!src.contains("http") && !src.contains("HTTP")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						imge.attr("src", src);
					}
				}
				Elements iframes = conTag.select("iframe");
				for (Element iframe : iframes) {
					String src = iframe.attr("src");
					if (!src.contains("http") && !src.contains("HTTP")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						iframe.attr("src", src);
					}
				}
				
//				Element uselessTag1 = conTag.select("div[class=left layout3_art_info2]").first();
//				if(uselessTag1 != null){
//					uselessTag1.remove();
//				}
//				Element uselessTag2 = conTag.select("div[class=left article_con_bottom]").first();
//				if(uselessTag2 != null){
//					uselessTag2.remove();
//				}
//				String content = conTag.outerHtml();
				String content = "<div>" + title + "</div>" + conTag.outerHtml();
				response.content = content;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	
	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String host = iwRequest.getHost();
			String path = iwRequest.getRequestPath();
			 
			String url = "http://" + host + path;
			String str = getContent(url);
			iwr = new DefaultIwResponse(null, str.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwr;
	}
	
	public static String getContent(String url) {
		String str = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=utf-8");
		httpGet.addHeader("Accept", "*/*");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
		httpGet.setConfig(requestConfig);
		
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == 404) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "utf-8"));
				StringBuilder sb = new StringBuilder();
				while ((str = reader.readLine()) != null) {
					sb.append(str + "\n");
				}
				str = sb.toString();
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
		return str;
	}

}
