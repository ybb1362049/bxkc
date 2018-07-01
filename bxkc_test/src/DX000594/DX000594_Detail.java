package DX000594;

import java.text.SimpleDateFormat;
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

public class DX000594_Detail extends TxtRspHandler {

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
				Element titleElement = xml.select("body > div > table.mtd8 > tbody > tr > td:nth-child(3) > table:nth-child(2) > tbody > tr:nth-child(1) > td > h1").first();
				String title = titleElement.text().trim();
				response.title = title;
				Element contentElement = xml.select("body > div > table.mtd8 > tbody > tr > td:nth-child(3) > table:nth-child(2) > tbody > tr:nth-child(4) > td").first();
				contentElement.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				contentElement.select("script").remove();
				
				Element dateElement = xml.select("body > div > table.mtd8 > tbody > tr > td:nth-child(3) > table:nth-child(2) > tbody > tr:nth-child(2) > td").first();
				String date = dateElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
				Pattern p = Pattern.compile("\\d+{4}/\\d{1,2}/\\d{1,2}");
				Matcher m;
				m = p.matcher(date);
				if (m.find()) {
					date = m.group();
					date=date.replace("/", "-");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				date = sdf.format(sdf.parse(date));
				response.date = date;
				if (contentElement.select("a") != null) {
					Elements as = contentElement.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript") && !hre.contains("http")) {
							hre = "http://www.eebthree.cn" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (contentElement.select("img").first() != null) {
					Elements img = contentElement.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							imgss = "http://www.eebthree.cn" + imgss;
							imgs.attr("src", imgss);

						}
					}
				}
				String contentString = titleElement.outerHtml() + contentElement.outerHtml();
				response.content = contentString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
