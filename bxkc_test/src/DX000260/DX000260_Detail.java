package DX000260;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DX000260_Detail extends TxtReqRspHandler {
	public final String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
	public String REQUEST_PATH;
	public String ORIGIN_URL;

	public class Response extends TxtBaseResponse {
		String title;
		String content;
//		String date;
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
				String contentString = "";
				response.content = originTxtRspContent;
				Document xml = Jsoup.parse(originTxtRspContent);

				Element titleEL = xml.select("div.ui-item-detail-title").first();
				if (titleEL != null) {
					response.title = titleEL.text().trim();
				}
				Element content = xml.select("div.ui-item-detail-edit").last();
				Element conteElement = filter(content);
				
				Element detailCon = xml.select("div[class=ui-bidding-information]").first();
				Element imgTag = detailCon.select("div[class=STYLE1]:has(img)").last();
				if(imgTag != null){
					imgTag.remove();
				}
				conteElement.append(detailCon.outerHtml());
				
				String addURL = "<a href= " + ORIGIN_URL + ">" + "原网站地址</a>";
				conteElement.append(addURL);

				contentString = conteElement.outerHtml();
				response.content = contentString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public Element filter(Element content) {
		Elements alinks = content.select("a");
		Elements imgs = content.select("img");
		content.select("script").remove();
		if (REQUEST_PATH == null) {
			REQUEST_PATH = getRequestPath();
		}
		String alink = "";
		String imgSrc = "";
		if (alinks != null) {
			for (Element aElement : alinks) {
				if (aElement.id().equals("downLoadWl")) {
					alink = "/hxbiz/nbid/notice/" + aElement.attr("href");
				} else {
					alink = aElement.attr("href");
				}
				alink = urlFilter(alink);
				if (!alink.contains("@") && !"".equals(alink) && !alink.contains("javascript")
						&& !alink.contains("http")) {
					alink = REQUEST_PATH + alink;
					aElement.attr("href", alink);
				}
			}
		}

		if (imgs != null) {
			for (Element imgElement : imgs) {
				imgSrc = imgElement.attr("src");
				imgSrc = urlFilter(imgSrc);
				if (!imgSrc.contains("@") && !"".equals(imgSrc) && !imgSrc.contains("javascript")
						&& !imgSrc.contains("http")) {
					imgSrc = REQUEST_PATH + imgSrc;
					imgElement.attr("src", imgSrc);
				}
			}
		}

		Element span = content.select("span.temp_contractTerms").first();
		if (span != null) {
			Element input = span.select("input").first();
			if (input != null) {
				alink = input.text();
				if (alink != null && "".equals(alink)) {
					alink.replace("[", "").replace("]", "");
					try {
						JSONObject json = new JSONObject(alink);
						alink = json.getString("path");
						String originalName = json.getString("originalName");
						alink = "<a href=" + alink + ">" + originalName + "</a>";
						alink = alink.replace("&amp;", "&");
						span.append(alink);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		content.select("input").remove();
		return content;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest request) {
		IwResponse response = null;
		WebClient webclient = new WebClient(BrowserVersion.CHROME);
		try {
			String url = "http://" + request.getHost() + request.getRequestPath() + "?" + "bidId="
					+ request.getRequestPathParam("bidId");
			ORIGIN_URL = url;
			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(true);
			webclient.getOptions().setTimeout(10000);
			HtmlPage htmlpage = webclient.getPage(url);
			webclient.waitForBackgroundJavaScript(1000 * 2);
			String content = htmlpage.asXml();
			response = new DefaultIwResponse(null, content.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webclient.closeAllWindows();
		}

		return response;
	}

	private String urlFilter(String url) {
		if (url.contains("../")) {
			url = url.replace("../", "");
		} else if (url.contains("./")) {
			url = url.replace("./", "");
		}
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		return url;
	}

	private String getRequestPath() {
		String regex = "(http(s)?://)([\\w-]+\\.)+[\\w-]+(?<=/?)";
		Pattern p = Pattern.compile(regex);
		String requestPath = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
		if (requestPath == null) {
			return "null";
		}
		Matcher m = p.matcher(requestPath);
		if (m.find()) {
			requestPath = m.group();
		}
		return requestPath;
	}

	public String getDate(Element dateElement) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String regex = "(?<year>\\d{4})[\\年|\\-|\\/|\\.](?<month>\\d{1,2})[\\月|\\/|\\-|\\.](?<day>\\d{1,2})[\\日|\\-|\\/|\\.]?";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(dateElement.text());
		String dateString = null;
		if (m.find()) {
			if (!m.group(0).startsWith("20")) {
				dateString = "20" + m.group("year") + "-" + m.group("month") + "-" + m.group("day");
			} else {
				dateString = m.group("year") + "-" + m.group("month") + "-" + m.group("day");
			}
			dateString = sdf.format(sdf.parse(dateString));
		}
		return dateString;
	}

}
