package XX0946;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class XX0946_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	String url = "http://www.cqtbi.edu.cn";
	Pattern p = Pattern.compile("_jsq_\\((.*?),'/content.jsp',(.*?),");
	Matcher m;

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
				Element div = doc.select("form[name=_newscontent_fromname]").first();
				if (div != null) {
					div.select("script").remove();
					div.select("table[width=864]").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
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
								href = url + "/" + href;
								a.attr("href", href);
							}
						}
					}
					Elements iframeList = div.select("iframe");
					for (Element iframe : iframeList) {
						iframe.remove();
					}
					Elements imgList = div.select("IMG");
					for (Element img : imgList) {
						String href = img.attr("src");
						if(href.contains("fileTypeImages"))
						{
							img.remove();
							continue;
						}
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
						}
					}
					String con = div.outerHtml();
					con = con.replaceAll("已下载(.*?)次", "");
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
