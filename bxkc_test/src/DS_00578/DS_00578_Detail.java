package DS_00578;

import java.text.SimpleDateFormat;

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

public class DS_00578_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://www.ncfdj.gov.cn";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
				Element div = doc.select("div[class=xy-box]").first();
				if (div != null) {
					div.select("script").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
						if (href.equals("#")) {
							a.remove();
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
