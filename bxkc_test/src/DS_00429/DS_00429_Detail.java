package DS_00429;

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

public class DS_00429_Detail extends TxtRspHandler {

	class Response extends TxtBaseResponse {
		// String title;
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
			if (originTxtRspContent.contains("正在跳转到目标页面,请等待！")) {
				response.content = "<html>正在跳转到目标页面,请等待！</html>";
				return response;
			}
			Document document = Jsoup.parse(originTxtRspContent);
			document.outputSettings().prettyPrint(true);
			String path = getContextInfo(Context.REQUEST_CONTEXT_PATH);
			document.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
			document.select("script").remove();
			if (path != null) {
				String host = path.replaceAll("(.*?\\..*?)/.*", "$1");
				if (document.select("a").first() != null) {
					Elements as = document.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript")
								&& !hre.contains("http") && !hre.startsWith("//")) {
							if (hre.startsWith("./")) {
								hre = path + hre.substring(2);
							} else if (hre.startsWith("../../")) {
								hre = host + hre.replaceAll(".*?(/\\w.*)", "$1");
							} else if (hre.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								hre = url + hre.substring(2);
							} else if (!hre.startsWith("/")) {
								hre = path + hre;
							} else
								hre = host + hre;
							href.attr("href", hre);
						}
					}
				}
				if (document.select("img").first() != null) {
					Elements img = document.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")
								&& !imgss.startsWith("//")) {
							if (imgss.startsWith("./")) {
								imgss = path + imgss.substring(2);
							} else if (imgss.startsWith("../../")) {
								imgss = host + imgss.replaceAll(".*?(/\\w.*)", "$1");
							} else if (imgss.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								imgss = url + imgss.substring(2);
							} else if (!imgss.startsWith("/")) {
								imgss = path + imgss;
							} else
								imgss = host + imgss;
							imgs.attr("src", imgss);

						}
					}
				}
			}
			Element contentElement = null;
			if (document.select("div.ewb-detail-info").first() != null) {
				contentElement = document.select("div.ewb-detail-info").first();
			} else if (document.select("#zfcg_gzbc2_tblInfo").first() != null) {
				contentElement = document.select("#zfcg_gzbc2_tblInfo").first();
				contentElement.select("#zfcg_gzbc2_tdTitle").remove();
			} else if (document.select("#jsgc_zbgg1_lblTitle").first() != null) {
				contentElement = document.select("#jsgc_zbgg1_tblInfo").first();
				contentElement.select("font.webfont").remove();
			} else if (document.select("#zfcg_jggg1_cont").first() != null) {
				contentElement = document.select("#zfcg_jggg1_cont").first();
				contentElement.select("font.webfont").first().remove();
			} else if (document.select("#jsgc_jggs1_lblTitle").first() != null) {
				contentElement = document.select("#jsgc_jggs1_tblInfo").first();
				contentElement.select("font.webfont").first().remove();
			} else if (document.select("#jsgc_hxgs1_tblInfo").first() != null) {
				contentElement = document.select("#jsgc_hxgs1_tblInfo").first();
			} else if (document.select("div[data-role~=tab.*] table").first() != null) {
				contentElement = document.select("div[data-role~=tab.*] table").get(0);
				if (contentElement.select("font.webfont").first() != null) {
					contentElement.select("font.webfont").first().remove();
				}
			}
			response.content = contentElement.outerHtml();
		}
		return response;
	}

}
