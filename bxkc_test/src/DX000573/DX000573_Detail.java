package DX000573;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000573_Detail extends TxtRspHandler {

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
				Element titleElement = xml.select("div.title>h2").first();
				String title = titleElement.text().trim();
				response.title = title;
				Element contentElement = xml.select("div.artContent").first();
				contentElement.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				contentElement.select("script").remove();
				
				Element dateElement = xml.select("div.info").first();
				String date = dateElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
				Pattern p = Pattern.compile("\\d+{4}-\\d{1,2}-\\d{1,2}");
				Matcher m;
				m = p.matcher(date);
				if (m.find()) {
					date = m.group();
				}else{
					date=contentElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					p = Pattern.compile("\\d+{4}年\\d{1,2}月\\d{1,2}");
					m = p.matcher(date);
					if (m.find()) {
						date = m.group();
						date=date.replace("年", "-").replace("月", "-");
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				date = sdf.format(sdf.parse(date));
				response.date = date;
				if (contentElement.select("a") != null) {
					Elements as = contentElement.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript") && !hre.contains("http")) {
							hre = "http://www.xytzjt.cn" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (contentElement.select("img").first() != null) {
					Elements img = contentElement.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")) {
							imgss = "http://www.xytzjt.cn" + imgss;
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
	private final static int TIMEOUT_IN_MS = 100000;
	private static String callApi(String path) {
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);// 设置连接主机超时
			conn.setReadTimeout(TIMEOUT_IN_MS);// 设置从主机读取数据超时
			retBytes = IOUtils.toByteArray(conn);
			return new String(retBytes, "UTF-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
