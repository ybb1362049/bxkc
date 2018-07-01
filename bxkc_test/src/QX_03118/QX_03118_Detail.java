package QX_03118;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_03118_Detail extends TxtRspHandler {
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

	public class Response extends TxtBaseResponse {
		String title;
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String path = "http://www.nq.gov.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				Elements scripts = xml.select("script");
				for (Element script : scripts) {
					script.remove();
				}
				String content;
				Element conTag = xml.select("div[class=article]").first();
				Elements as = conTag.select("a");
				for (Element ae : as) {
					String href = ae.attr("href");
					if (href.contains("../")) {
						href = href.substring(href.indexOf("..") + 2);
					}
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
				String title = xml.select("div[class=tit]").text().trim();
				response.title = title;
				content = conTag.outerHtml();
				response.content = "<div>" + title + "</div>" + "<div>" + content + "</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
