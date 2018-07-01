package DS_02836;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_02836_Detail extends TxtRspHandler {

	class Response extends TxtBaseResponse {
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
		try {
			if (rspState == RspState.Login) {
				Document xmlDoc = Jsoup.parse(originTxtRspContent);
				String path = getContextInfo(Context.REQUEST_CONTEXT_PATH);
				xmlDoc.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				xmlDoc.select("script").remove();
				if (xmlDoc.select("a").first() != null) {
					Elements as = xmlDoc.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript")
								&& !hre.contains("http")) {
							if (hre.startsWith("./")) {
								hre = path + hre.substring(2);
							} else if (hre.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								hre = url + hre.substring(2);
							} else
								hre = "http://www.phcc120.com" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (xmlDoc.select("img").first() != null) {
					Elements img = xmlDoc.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							if (imgss.startsWith("./")) {
								imgss = path + imgss.substring(2);
							} else if (imgss.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								imgss = url + imgss.substring(2);
							} else
								imgss = "http://www.phcc120.com" + imgss;
							imgs.attr("src", imgss);

						}
					}
				}
				Element titleElement = xmlDoc.select("h1").first();
				Element contentElement = xmlDoc.select("div.contentzoom").first();
				if (contentElement.select("p").first()!=null) {
					contentElement.select("p").last().remove();
				}
				response.title = titleElement.text();
				response.content = "<div>" + titleElement.outerHtml() + contentElement.outerHtml() + "</div>";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
