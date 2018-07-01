package DS_00238;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_00238_Detail extends TxtRspHandler {

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
				String path = "http://www.jcggzy.com";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
//				Element totalTag = xml.select("div[class=col-left]").first();
//				String title = "";
//				Element titleTag = totalTag.getElementById("ctl00_ctl00_ContentPlaceHolder1_ContentPlaceHolder2_thetitle");
//				if(titleTag != null){
//					title = titleTag.text().trim();
//					title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
//					title = title.replaceAll("\\[[^\\]]+]", "");
//					title = title.replaceAll("【.*】", "");
//					response.title = title;
//				}
				
				Element conTag = xml.getElementById("content");
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
				
				Element uselessTag = conTag.select("h3[class=navbar]").last();
				if(uselessTag != null){
					uselessTag.remove();
				}
//				Element uselessTag1 = conTag.select("div[class=detail_djl]").first();
//				if(uselessTag1 != null){
//					uselessTag1.remove();
//				}
				String content = conTag.outerHtml();
//				String content = "<div>" + title + "</div>" + conTag.outerHtml();
				response.content = content;
//				String str = totalTag.text().trim();
//				str = str.replaceAll("[.|/|年|月]", "-");
//				Matcher m = p.matcher(str);
//				if(m.find()){
//					String date = sdf.format(sdf.parse(m.group()));
//					response.date = date;
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

//	
//	@Override
//	public IwResponse sendIwRequest(IwRequest iwRequest) {
//		IwResponse iwr = null;
//		try {
//			String host = iwRequest.getHost();
//			String path = iwRequest.getRequestPath();
//			String type = iwRequest.getRequestPathParam("type");
//			 
//			String url = "http://" + host + path + "?" + type;
//			String str = getContent(url);
//			iwr = new DefaultIwResponse(null, str.getBytes("GBK"), null, 0, "ok");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return iwr;
//	}
//	
//	
//	public static String getContent(String url) {
//		String str = "";
//		CloseableHttpClient client = HttpClients.createDefault();
//		HttpResponse response;
//		HttpGet httpGet = new HttpGet(url);
//		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
//		httpGet.addHeader("Content-type", "text/html; charset=GBK");
//		httpGet.addHeader("Accept", "text/html");
//		
//		RequestConfig requestConfig = RequestConfig.custom()
//				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
//		httpGet.setConfig(requestConfig);
//		
//		try {
//			response = client.execute(httpGet);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode == HttpStatus.SC_OK) {
//				HttpEntity resEntity = response.getEntity();
//				InputStream ins = resEntity.getContent();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "GBK"));
//				StringBuilder sb = new StringBuilder();
//				while ((str = reader.readLine()) != null) {
//					sb.append(str + "\n");
//				}
//				str = sb.toString();
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				client.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return str;
//	}
}
