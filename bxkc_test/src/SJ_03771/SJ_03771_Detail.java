package SJ_03771;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_03771_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
				Document document = Jsoup.parse(originTxtRspContent);
				Element detailEle = document.select("div.div-content").first();
				Element titleEle = detailEle.select("div.div-title").first();
				detailEle.select("script").remove();
				detailEle.select("div.div-title2").remove();

				for (Element aElement : detailEle.select("a[href]")) {
					String href = aElement.attr("href");
					String lowerCaseHref = href.toLowerCase(Locale.ENGLISH);
					if (!lowerCaseHref.startsWith("http") && !lowerCaseHref.startsWith("mailto")) {
						href = "https://www.bjggzyfw.gov.cn" + href;
					}
					aElement.attr("href", href);
				}
				for (Element srcElement : detailEle.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "https://www.bjggzyfw.gov.cn" + src;
					}
					srcElement.attr("src", src);
				}

				response.title = titleEle.text();
				response.content = "<div>\n";
				response.content += detailEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
