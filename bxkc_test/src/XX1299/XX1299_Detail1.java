package XX1299;

import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class XX1299_Detail1 extends TxtRspHandler {
	class Response extends TxtBaseResponse {
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState repState,
			String originTxtRspContent) {
		// TODO Auto-generated method stub
		Response response = new Response();
		String hostName = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
		hostName = hostName.substring(0, hostName.indexOf("/", 8));
		if (repState.equals(RspState.Login)) {
			try {
				Document doc = Jsoup.parse(originTxtRspContent);
				Element content = doc.select("#xw_content").first();
				if (content != null) {
					Elements iframList = content.select("iframe");
					if (iframList.size() > 0) {
						String html = null;
						for (Element ifram : iframList) {
							String src = ifram.attr("src");
							src = completionLinksHostName(src, hostName);
							html = callApi(src, "UTF-8");
							Document doc2 = Jsoup.parse(html);
							ifram.before(doc2.body().html());
							ifram.remove();
						}
					}
					content.select("script").remove();
					content.select(
							"*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]")
							.remove();
					Elements aList = content.select("a[href]");
					if (aList.size() > 0) {
						for (Element a : aList) {
							String href = a.attr("href");
							href = completionLinksHostName(href, hostName);
							a.attr("href", href);
						}
					}
					Elements imgList = content.select("img[src]");
					if (imgList.size() > 0) {
						for (Element img : imgList) {
							String src = img.attr("src");
							src = completionLinksHostName(src, hostName);
							img.attr("src", src);
						}
					}
					if (content.text().startsWith("http")) {
						response.content = "该详情为跳转链接跳过抓取";
					} else {
						Element title = doc.select("#xw_title").first();
						if (title != null) {
							response.content = "<div>" + title.outerHtml()
									+ "</div>" + content.outerHtml();
						} else {
							response.content = content.outerHtml();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public String completionLinksHostName(String link, String hostName) {// hostname
																			// 类似这样的形式
																			// http://www.hostname.com
		String complementedLink = "";
		if (link.indexOf("http") != 0
				&& (link.indexOf("mail") != 0 || link.indexOf("data") != 0)) {// 判断是否需要补全链接
			if (link.indexOf("./") == 0)
				complementedLink = super
						.getContextInfo(Context.REQUEST_CONTEXT_PATH)
						+ link.substring(1);
			else if (link.indexOf("../") == 0) {
				complementedLink = link.substring(2);
				complementedLink = hostName + complementedLink;
			} else if (link.indexOf("/") == 0)
				complementedLink = hostName + link;
			else
				complementedLink = super
						.getContextInfo(Context.REQUEST_CONTEXT_PATH) + link;
			return complementedLink;
		}
		return link;
	}

	private static int TIMEOUT_IN_MS = 5000;

	private static String callApi(String path, String charSet) throws Exception {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, charSet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
