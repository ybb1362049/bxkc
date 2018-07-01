package DS_03887;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_03887_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
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
		if (rspState == RspState.Login) {
			try {
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				if(path.contains("portal")){
					path = path.substring(0,path.indexOf("portal"));
				}
				//http://czj.dg.gov.cn/dggp/portal//uploads//201806/20180608.1528442363838.docx	
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				Elements scripts = xml.select("script");
				for (Element script : scripts) {
					script.remove();
				}
				Element conTag = xml.select("td[bgcolor=#ffffff]").first();
				Elements as = conTag.select("a");
				for (Element ae : as) {
					String href = ae.attr("href");
					if (href.contains("../")) {
						href = href.substring(href.indexOf("../") + 3);
					}
					if (!"#".equals(href) && !href.contains("http") && href.length() > 0 && !href.contains("HTTP")) {
						if (href.startsWith("/")) {
							href = path + href;
						} else {
							href = path + "/" + href;
						}
						ae.attr("href", href);
					}
				}

				Elements imgs = conTag.select("img");
				for (Element imge : imgs) {
					String src = imge.attr("src");
					if (!src.contains("http") && !src.contains("HTTP") && !src.startsWith("data")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						imge.attr("src", src);
					}
				}

				Elements inputs = conTag.select("input");
				for(Element input : inputs){
					String value = input.attr("value");
					input.after(value);
					input.remove();
				}
				
				String title;
				Element d = xml.select("h2").last();
				if (d != null) {
					title = d.text().trim();
					response.title = title;
				} else {
					title = xml.select("td[class=title_cn]").first().text().trim();
					response.title = title;
				}
				String content = conTag.outerHtml();
				content = content + "<br><div><a href=\"http://czj.dg.gov.cn/dggp/\">公告来源</a></div>";
				response.content = "<div>" + content + "</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
