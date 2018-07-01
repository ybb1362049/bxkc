package QX_01415;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_01415_Detail2 extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://ggjy.cnjg.gov.cn";

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
				Element derailTime = doc.getElementById("ctl00_ContentPlaceHolder1_TimeLabel");
				if (derailTime != null) {
					response.date=derailTime.text().trim();
				}
				Element derailTitle = doc.getElementById("ctl00_ContentPlaceHolder1_TitleLabel");
				if (derailTitle != null) {
					content = derailTitle.outerHtml();
				}
				Element div = doc.select("div.newsImage").last();
				if (div != null) {
					div.select("script").remove();
					div.select("[style=display:none]").remove();
					div.select("input").remove();
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
					String conText = "";
					Element iframe = div.getElementById("frmBestWord");
					if (iframe != null) {
						String href = iframe.attr("src");
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
							} else if (href.indexOf("/") == 0) {
								href = url + href;
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
							}
						}
						String conTxt = getContent(href);
						Document xml = Jsoup.parse(conTxt);
						conText += xml.select("body").first().outerHtml();
						iframe.remove();
					}
					String con = div.outerHtml();
					con = con.replaceAll("点击：(\\d+)", "");
					content += con + conText;
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public String getContent(String path) {
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=gb2312");
		httpGet.addHeader("Accept", "text/html");
		try {
			res = client.execute(httpGet);
			try {
				respStr = EntityUtils.toString(res.getEntity(), "gbk");
			} finally {
				res.getEntity().getContent().close();
				res.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
				// res.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respStr;
	}

}
