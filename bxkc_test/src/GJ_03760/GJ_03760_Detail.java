package GJ_03760;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class GJ_03760_Detail extends TxtReqRspHandler {

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			String requestPath = iwRequest.getRequestPath();
			String iw_ir_2 = requestPath.substring(requestPath.lastIndexOf("/") + 1);
			
			String url = "http://www.chinabidding.com/bidDetail/" + iw_ir_2;
			String paramStr = "";
			String resp = sendGet(url, paramStr);

			iwResponse = new DefaultIwResponse(null, resp.getBytes("UTF-8"), Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
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
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);
				Element detailDivEle = document.select("div.content").first();
				detailDivEle.select("script").remove();
				detailDivEle.select("em:contains(中国国际招标网)").remove();

				for (Element aEle : detailDivEle.select("a[onclick]")) {
					String onclick = aEle.attr("onclick").replaceAll("\\s+", "");
					Matcher attachmentMatcher = attachmentPattern.matcher(onclick);
					if (attachmentMatcher.find()) {
						String md5 = attachmentMatcher.group("md5");
						String userId = attachmentMatcher.group("userId");
						aEle.attr("href", "http://updownload.cloud.ebnew.com/updownload-app/download/download.bid?md5=" + md5 + "&userId=" + userId);
					}
					aEle.removeAttr("onclick");
				}
				
				Element titleH1Ele = detailDivEle.select("active.title").first().select("h1").first();
				titleH1Ele.attr("style", "text-align:center; font-size:26px;");
				detailDivEle.attr("style", "margin-left: 250px; margin-right: 250px; padding-top:10px; padding-bottom:20px; ");
				Element detailTableEle = detailDivEle.select("table.MsoNormalTable").first();
				if (detailTableEle != null) {
					detailTableEle.attr("style", detailTableEle.attr("style") + "margin-left: auto; margin-right: auto; ");
				}
				String title = titleH1Ele.text().trim();
				if(title.endsWith("..")){
					title = title.replaceAll("[.]", "");
				}
				response.title = title;
				response.content = "<div>\n";
				response.content += detailDivEle.outerHtml();
				response.content += "\n</div>";
				
				/*String title = titleH3Ele.text().trim();
				if (title.endsWith("..")) {
					Element articleBodyDivEle = titleH3Ele.parent().select("div.as-article-body table-article").first();
					Elements chineseEles = articleBodyDivEle.select("*:matches([\u4e00-\u9fa5]+)");
					for (Element chineseEle : chineseEles) {
						String text = chineseEle.text().trim().replace("\\s+", "");
					}
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	/**
	 * 利用HttpClient发送GET请求
	 * @param url
	 * @param paramStr，格式："name1=value1&name2=value2&name3=value3"
	 * @return 
	 * @return
	 */
	public static String sendGet(String url, String paramStr) {
		String resp = "";
		CloseableHttpClient httpClient = null;
		
		try {
			httpClient = HttpClients.createDefault();
			url = paramStr != null && !paramStr.isEmpty() ? url + "?" + paramStr : url;
			HttpGet request = new HttpGet(url);
			
			int retryTimes = 3 + randomGenerator.nextInt(3);
			do {
				CloseableHttpResponse httpResponse = httpClient.execute(request);
				resp = EntityUtils.toString(httpResponse.getEntity());
				
				int httpStatus = httpResponse.getStatusLine().getStatusCode();
				// System.out.println("status: " + httpStatus);
				if (httpStatus == HttpStatus.SC_OK) {
					break;
				} else {
					try {
						Thread.sleep((randomGenerator.nextInt(5) + 1) * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} while (--retryTimes > 0);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return resp;
	}
	
	private static Pattern attachmentPattern = Pattern.compile("downloadf\\(\"(?<md5>[0-9A-Za-z]+)\",\"(?<userId>[0-9A-Za-z]+)\"\\)", Pattern.CASE_INSENSITIVE);
	private static Random randomGenerator = new Random();
	
	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
	}
	
}
