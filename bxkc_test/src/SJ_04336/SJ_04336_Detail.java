package SJ_04336;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_04336_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	
	public class Response extends TxtBaseResponse {
		String title;
		String content;
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
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);
				Element detailDivEle = document.select("div[class=article]").first();
				Element titleDivEle = document.select("h1").first();
				detailDivEle.select("script").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8) + 1);
				for (Element aEle : detailDivEle.select("a[href]")) {
					String href = aEle.attr("href");
					if (!href.startsWith("http")) {
						href = path + href;
					}
					aEle.attr("href", href);
				}
				for (Element srcEle : detailDivEle.select("*[src]")) {
					String src = srcEle.attr("src");
					if (!src.startsWith("http")) {
						src = path + src;
					}
					srcEle.attr("src", src);
				}
				response.title = titleDivEle.text().trim();
				response.content = "<div>\n";
				response.content += titleDivEle.outerHtml() + detailDivEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
