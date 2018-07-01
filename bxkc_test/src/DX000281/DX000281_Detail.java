package DX000281;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

public class DX000281_Detail extends TxtRspHandler {

	class Response extends TxtBaseResponse {
		String title;
		String content;
		String date;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		try {
			if (rspState == RspState.Login) {
				Document xmlDoc = Jsoup.parse(originTxtRspContent);
				String path = getContextInfo(Context.REQUEST_CONTEXT_PATH);
				xmlDoc.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				xmlDoc.select("script").remove();
				if (xmlDoc.select("a").first() != null) {
					Elements as = xmlDoc.select("a");
					for (Element href : as) {
						String hre = href.attr("href");
						if (!hre.contains("@") && !"".equals(hre) && !hre.contains("javascript")
								&& !hre.contains("http")&& !hre.startsWith("//")) {
							if (hre.startsWith("./")) {
								hre = path + hre.substring(2);
							} else if (hre.startsWith("../../")) {
								hre = "http://www.jtsww.com.cn" + hre.replaceAll(".*?(/\\w.*)", "$1");
							} else if (hre.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								hre = url + hre.substring(2);
							} else if (!hre.startsWith("/")) {
								hre = "http://www.jtsww.com.cn" + "/" + hre;
							} else
								hre = "http://www.jtsww.com.cn" + hre;
							href.attr("href", hre);
						}
					}
				}
				if (xmlDoc.select("img").first() != null) {
					Elements img = xmlDoc.select("img");
					for (Element imgs : img) {
						String imgss = imgs.attr("src");
						if (!imgss.contains("javascript") && !"".equals(imgss) && !imgss.contains("http")&& !imgss.startsWith("//")) {
							if (imgss.startsWith("./")) {
								imgss = path + imgss.substring(2);
							} else if (imgss.startsWith("../../")) {
								imgss = "http://www.jtsww.com.cn" + imgss.replaceAll(".*?(/\\w.*)", "$1");
							} else if (imgss.startsWith("../")) {
								String url = path.substring(0, path.length() - 1);
								url = url.substring(0, url.lastIndexOf("/"));
								imgss = url + imgss.substring(2);
							} else if (!imgss.startsWith("/")) {
								imgss = "http://www.jtsww.com.cn" + "/" + imgss;
							} else
								imgss = "http://www.jtsww.com.cn" + imgss;
							imgs.attr("src", imgss);
							if (imgss.startsWith("awp")) {
								imgs.attr("src","http://www.jtsww.com.cn/"+imgss);
							}

						}
					}
				}
				Element titleElement = xmlDoc.select("div.news_content h1").first();
				Element contentElement = xmlDoc.select("div.news_content div.cont").first();
				if (titleElement!=null) {
					response.title = titleElement.text();
					response.content = "<div>" + titleElement.outerHtml() + contentElement.outerHtml() + "</div>";
					Pattern pattern=Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
					Matcher matcher=pattern.matcher(contentElement.text());
					if (matcher.find()) {
						response.date=matcher.group();
					}
				}else {
				    String url=xmlDoc.select("iframe").attr("src");
				    String content=getContent(url);
				    contentElement=Jsoup.parse(content).select("body").first().tagName("div");
				    response.content=contentElement.outerHtml();
				    SimpleDateFormat originsdf=new SimpleDateFormat("yyyy-M-d");
				    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				    Pattern pattern=Pattern.compile("\\D(\\d{4})\\D(\\d{1,2})\\D(\\d{1,2})\\D");
					Matcher matcher=pattern.matcher(StringUtils.deleteWhitespace(contentElement.text()));
					while (matcher.find()) {
						response.date=matcher.group(1)+"-"+matcher.group(2)+"-"+matcher.group(3);
						response.date=sdf.format(originsdf.parse(response.date));
					}
				};
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String getContent(String path) {
		String result=null;
		WebClient webClient = null;
		try {
			webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setTimeout(10 * 1000);
			Page page = webClient.getPage(path);
			WebResponse webResponse = page.getWebResponse();
			result = webResponse.getContentAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(webClient != null){
				webClient.closeAllWindows();
			}
		}
		return result;
	}
}
