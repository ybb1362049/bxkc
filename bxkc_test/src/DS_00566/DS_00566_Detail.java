package DS_00566;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_00566_Detail extends TxtRspHandler {

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
				String path = "http://ggzy.tlzw.gov.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				String title = "";
				Element titleTag = xml.select("div[class=bhd]").first().select("h2").first();
				if(titleTag != null){
					title = titleTag.text().trim();
					response.title = title;
				}
				
				Element conTag = xml.getElementById("tempContent");
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
					if (!"#".equals(href) && !href.contains("http") && href.length() > 0 && !href.contains("HTTP")) {
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
					if (!src.contains("http") && !src.contains("HTTP") && !src.startsWith("data")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						imge.attr("src", src);
					}
				}
				
//				Element uselessTag = conTag.select("span[class=ewb-dates l]").first();
//				if(uselessTag != null){
//					uselessTag.remove();
//				}
//				Element uselessTag1 = conTag.select("p[class=news-article-info]").first();
//				if(uselessTag1 != null){
//					uselessTag1.remove();
//				}
				String content = "<div>" + title + "</div>" + conTag.html();
//				String content = conTag.outerHtml();
				content = StringEscapeUtils.unescapeHtml(content);
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
