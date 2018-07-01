package DS_00700;

import java.io.IOException;

import java.nio.charset.Charset;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class DS_00700_Detail extends TxtReqRspHandler {

	private static String baseUrl = "http://www.fzztb.gov.cn";

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				String content = "";
				Document doc = Jsoup.parse(originRspContent);
				Element titleElement = doc.select("span[class=news_title]").first();
				String title = titleElement.text().trim();
				content = titleElement.outerHtml();

				Element contentElement = doc.select("div[class=TRS_Editor]").first();
				if (contentElement == null) {
					contentElement = doc.select("div[class=TRS_PreAppend]").first();
					if (contentElement != null) {
						content = titleElement.outerHtml() + contentElement.outerHtml();
					}
				}
				contentElement.select("script").remove();
				contentElement.select("[style=display:none]").remove();
				contentElement.select("input").remove();

				Elements imgs = contentElement.select("img");
				for (Element img : imgs) {
					String src = img.attr("src");
					if (src.indexOf("/") == 0) {
						img.attr("src", baseUrl + src);
					}
				}
				Element fileElement = doc.select("td[class=redfont]").last();
				if (fileElement != null) {
					Elements as = fileElement.select("a");
					if (as != null && as.size() > 0) {
						for (Element a : as) {
							String href = a.attr("href");
							if (href.indexOf("./") == 0) {
								baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
								href = href.substring(href.indexOf("./") + 1);
								a.attr("href", baseUrl + href);
							}
						}
					}
					content = titleElement.outerHtml() + contentElement.outerHtml() + fileElement.outerHtml();
				}
				response.content = content;
				response.title = title;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			String path = "http://" + iwRequest.getHost() + iwRequest.getRequestPath();
			baseUrl = path;
			String result = getContent(path);
			iwResponse = new DefaultIwResponse(null, result.getBytes("UTF-8"), Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
	}

	public String getContent(String path) {
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html");
		httpGet.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respStr;
	}

}
