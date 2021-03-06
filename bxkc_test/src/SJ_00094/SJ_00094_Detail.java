package SJ_00094;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_00094_Detail extends TxtRspHandler {

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
				Document xml = Jsoup.parse(originTxtRspContent);
				/*
				 * Element titleElement = xml.select("div.newsTex>h1").first();
				 * String title = titleElement.text().trim(); response.title =
				 * title;
				 */
				Element contentElement = xml.select("div#con").last();
				if (contentElement == null)
					contentElement = xml.select("div#left1").last();
				contentElement.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				contentElement.select("script").remove();
				if (contentElement.select("a").first() != null) {
					Elements as = contentElement.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript")
								&& !hre.contains("http")) {
							hre = "http://www.nmggcztb.cn" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (contentElement.select("img").first() != null) {
					Elements img = contentElement.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if(imgss.length()>100){
							imgs.remove();
							continue;
						}
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							imgss = "http://www.nmggcztb.cn" + imgss;
							imgs.attr("src", imgss);

						}
					}
				}
				response.content = contentElement.outerHtml();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
