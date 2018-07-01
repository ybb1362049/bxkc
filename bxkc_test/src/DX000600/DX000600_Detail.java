package DX000600;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000600_Detail extends TxtRspHandler {

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
				if (originTxtRspContent.contains("Server Error")) {
					response.content = "由于某些原因该详请无法展示";
					return response;
				}
				Document xml = Jsoup.parse(originTxtRspContent);
				Element titleElement = xml.select("#divContent > div.article-name > h3").first();
				String title = titleElement.text().trim();
				response.title = title;
				Element contentElement = xml.select("section#divContent").last();
				contentElement.select("ul.unlist").remove();
				if(contentElement.select("div.pull-right").first()!=null){
					contentElement.select("div.pull-right").first().remove();
				}
				contentElement.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				contentElement.select("script").remove();
				String link="";
				Pattern p = Pattern.compile("(?=/PurchaseOnline/OnlinePriProForeSupplierQuote/ExportSupQuoteBidExcelTemplateHomePage?)(.*?)(?=\";)");
				Matcher m;
				m = p.matcher(originTxtRspContent);
				while (m.find()) {
					link=m.group();
				}
				if (contentElement.select("a").first() != null) {
					Elements as = contentElement.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (hre.contains("javascript")) {
							hre = "http://zb.tbea.com:8020" + link;
							href.attr("href", hre);
							href.removeAttr("onclick");
						}
					}
				}
				if (contentElement.select("img").first() != null) {
					Elements img = contentElement.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							imgss = "http://zb.tbea.com:8020" + imgss;
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
