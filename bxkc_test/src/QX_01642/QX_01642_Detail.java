package QX_01642;

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

public class QX_01642_Detail extends TxtRspHandler {

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {

				response.content = originTxtRspContent;
				String remove = "";
				boolean getdate = false;
				String dateString = "";

				Document xml = Jsoup.parse(originTxtRspContent);
				Element titElement = xml.select("h3").first();
				Element content = xml.select("div.detail_main_content").first();
				removeChild(content, remove);
				Element conteElement = filter(content);

				if (titElement != null) {
					response.title = titElement.text().replaceAll(SPACE, "");
					if ("".equals(response.title) || response.title.length() < 1) {
						response.title = null;
					}
					response.content = titElement.outerHtml() + conteElement.outerHtml();

				} else {
					response.content = conteElement.outerHtml();

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
		if (url == null || "".equals("url")) {
			return "";
		}

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
		String[] methods = null;
		String method1 = null;
		String method2 = null;
		for (String child : children) {
			methods = child.split(",");
			method1 = methods[0];
			if (methods.length == 2) {
				method2 = methods[1];
				int index = Integer.valueOf(method2);
				if (index >= 0) {
					content.select(method1).get(index).remove();
				} else {
					index = content.select(method1).size() + index;
					content.select(method1).get(index).remove();
				}
			} else if (methods.length == 1) {
				content.select(method1).remove();
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
