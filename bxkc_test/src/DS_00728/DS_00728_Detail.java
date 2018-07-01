package DS_00728;

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

public class DS_00728_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://zbcg.mas.gov.cn";

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document doc = Jsoup.parse(originTxtRspContent);
				String content = "";
				Element dive = doc.select("td[id=tdTitle]").first();
				if (dive != null) {
					content = dive.select("font").first().outerHtml();
				}
				Element div = doc.select("td[id=TDContent]").first();
				if (div != null) {
					div.select("script").remove();
					div.select("[style=display:none]").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
						if (href.contains("mailto")) {
							continue;
						}
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("/") == 0) {
								href = url + href;
								a.attr("href", href);
							} else if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								a.attr("href", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								a.attr("href", href);
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								a.attr("href", href);
							}
						}
					}
					Elements imgList = div.select("IMG");
					for (Element img : imgList) {
						String href = img.attr("src");
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								img.attr("src", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								img.attr("src", href);
							} else if (href.indexOf("/") == 0) {
								href = url + href;
								img.attr("src", href);
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								img.attr("src", href);
							}
						}
					}
					String con = div.outerHtml();
					con = con.replaceAll("上一篇：|下一篇：", "");
					content += con;
				}
				response.content = content;
				// response.content=originTxtRspContent;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
