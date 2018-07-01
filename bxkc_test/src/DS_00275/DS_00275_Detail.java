package DS_00275;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00275_Detail extends TxtRspHandler {

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				
				String remove = "";
				boolean getdate = false;
				String dateString = "";

				Document xml = Jsoup.parse(originTxtRspContent);
				Element titElement = xml.select("h1.content_title").first();
				Element content = xml.select("div.content_text").first();
				removeChild(content, remove);
				Element conteElement = filter(content);

				if (titElement != null) {
					response.title = titElement.text().replaceAll(SPACE, "");
					response.content = titElement.outerHtml() + conteElement.outerHtml();
					;
				} else {
					response.content = conteElement.outerHtml();
					;
				}
				if (getdate) {
					if (!"".equals(dateString) && dateString != null) {
						Element dateElement = xml.select(dateString).first();
						response.date = getDate(dateElement);
					} else {
						response.date = getDate(content);
					}
				}
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
		if (alinks != null)
			for (Element aElement : alinks) {
				alink = aElement.attr("href");
				alink = urlFilter(alink);
				if (!alink.contains("@") && !"".equals(alink) && !alink.contains("javascript")
						&& !alink.contains("http")) {
					alink = REQUEST_PATH + alink;
					aElement.attr("href", alink);
				}
			}
		if (imgs != null)
			for (Element imgElement : imgs) {
				imgSrc = imgElement.attr("src");
				imgSrc = urlFilter(imgSrc);
				if (!imgSrc.contains("@") && !"".equals(imgSrc) && !imgSrc.contains("javascript")
						&& !imgSrc.contains("http")) {
					imgSrc = REQUEST_PATH + imgSrc;
					imgElement.attr("src", imgSrc);
				}
			}
		return content;
	}

	private String urlFilter(String url) {

		if (url.contains("../")) {
			url = url.replace("../", "");
		} else if (url.contains("./")) {
			url = url.replace("./", "");
		}
		if (!url.startsWith("/") && !url.equals("")) {
			url = "/" + url;
		}
		return url;
	}

	public void removeChild(Element content, String remove) {
		if ("".equals(remove) || remove == null) {
			return;
		}
		String[] children = remove.split(" ");
		for (String child : children) {
			String method = child.substring(child.indexOf(",") + 1);
			if (method.matches("//d")) {
				int index = Integer.valueOf(method);
				if (index > 0) {
					content.select(child).get(index).remove();
				} else {
					index = content.select(child).size() + index;
					content.select(child).get(index).remove();
				}
			}
		}
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
		String regex = "(?<year>20\\d{2}|\\d{2})(\\D)(?<month>\\d{1,2})(\\D)(?<day>\\d{1,2})";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(dateElement.text());
		String dateString = null;
		if (m.find()) {
			if (!m.group("year").startsWith("20")) {
				dateString = "20" + m.group("year") + "-" + m.group("month") + "-" + m.group("day");
			} else {
				dateString = m.group("year") + "-" + m.group("month") + "-" + m.group("day");
			}
			dateString = sdf.format(sdf.parse(dateString));
		}
		return dateString;
	}

	public final String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
	public String REQUEST_PATH = null;

	public class Response extends TxtBaseResponse {
		String title;
		String content;
		String date;
	}

}
