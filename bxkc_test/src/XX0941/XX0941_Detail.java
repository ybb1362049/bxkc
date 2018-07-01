package XX0941;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class XX0941_Detail extends TxtRspHandler {

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
				String path = "http://www.ndgzy.com";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				Element conTag = xml.select("div[class=articleDetail]").first();
//				String title = "";
//				Element titleTag = conTag.select("div[class=f24 fyh tac pb10]").first();
//				if(titleTag != null){
//					title = titleTag.text().trim();
//					response.title = title;
//				}
//				Element conTag = xml.select("div.Contnet").first();
//				Element conTag1 = xml.select("div.Contnet").first().select("div:has(p)").get(1);
				Elements scripts = conTag.select("script");
				for (Element script : scripts) {
					script.remove();
				}
				Elements metas = conTag.select("meta");
				for (Element metae : metas) {
					metae.remove();
				}
				Elements styles = conTag.select("style");
				for (Element style : styles) {
					style.remove();
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
				Element uselessTag = conTag.select("div.subTitle").first();
				if(uselessTag != null){
					uselessTag.remove();
				}
				Element uselessTag1 = conTag.select("div.preOrNextWraper").first();
				if(uselessTag1 != null){
					uselessTag1.remove();
				}
//				Element uselessTag2 = conTag.getElementById("idBoxOpen");
//				if(uselessTag2 != null){
//					uselessTag2.remove();
//				}
				
				String content = conTag.outerHtml();
//				String content = "<div>" + title + "</div>" + conTag.outerHtml();
				response.content = content;
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
//			type = iwRequest.getRequestPathParam("type");
//			String url = "http://" + host + path;
//			String str = Utils.callApi(url);
//			iwr = new DefaultIwResponse(null, str.getBytes("UTF-8"), null, 0, "ok");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return iwr;
//	}

}
