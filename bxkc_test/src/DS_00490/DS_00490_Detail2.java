package DS_00490;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00490_Detail2 extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://szzyjy.fwzx.suzhou.gov.cn";

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
				Element dive = doc.select("h2[class=word-title]").first();
				if (dive != null) {
					response.title = dive.text().trim();
					content = dive.outerHtml();
				}
				Element divCon = doc.select("td#TDContent").first();
				if (divCon != null) {
					divCon.select("script").remove();
					divCon.select("[style=display:none]").remove();
					divCon.select("input").remove();
					content += divCon.outerHtml();
				}
				Element div = doc.select("tr#trAttach").first();
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
					con = con.replaceAll("点击：(\\d+)", "");
					content += con;
				}else if(originTxtRspContent.contains("window.location="))
				{
					content="该公告无内容展示，请查看其它公告";
					response.date="2016-01-01";
					response.title="无效公告";
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
