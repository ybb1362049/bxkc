package SJ_04335;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_04335_Detail extends TxtRspHandler {

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
//				response.content = originTxtRspContent;
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				Element pe = xml.select("div[id=sub_scon]").first();
				Element h1s = xml.select("div[class=sbtit]").first();
//				pe.select("div[class=sicon]").first().remove();
				pe.select("script").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8) + 1);
				for (Element aEle : pe.select("a[href]")) {
					String href = aEle.attr("href");
					if (!href.startsWith("http")) {
						href = path + href;
					}
					aEle.attr("href", href);
				}
				for (Element srcEle : pe.select("*[src]")) {
					String src = srcEle.attr("src");
					if (!src.startsWith("http")) {
						src = path + src;
					}
					srcEle.attr("src", src);
				}
				response.title = h1s.text().trim();
				String content = pe.outerHtml();
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
