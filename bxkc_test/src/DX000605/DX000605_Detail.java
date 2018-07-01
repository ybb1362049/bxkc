package DX000605;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000605_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String title;
		String date;
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
//				System.out.println(originTxtRspContent);
				Document xml = Jsoup.parse(originTxtRspContent);
				Element contentElements = xml.select("div.pb40").first();
				Element titleElement = contentElements.select("div.mt45>h2").first();
				String title = titleElement.text().trim();
				response.title = title;
				contentElements.select("div.mt45").first().remove();
				Element contentElement = contentElements.select("div.mt45").first();
				contentElement.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				contentElement.select("div[style=margin-bottom:16px]").first().remove();
				contentElement.select("script").remove();
				if (contentElement.select("a").first() != null) {
					Elements as = contentElement.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript")
								&& !hre.contains("http")) {
							hre = "http://120.77.218.152" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (contentElement.select("img").first() != null) {
					Elements img = contentElement.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							imgss = "http://120.77.218.152" + imgss;
							imgs.attr("src", imgss);
						}
					}
				}
				response.content = titleElement.outerHtml() + contentElement.outerHtml();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
