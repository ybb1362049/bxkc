package QX_01642;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class QX_01642_List extends TxtReqRspHandler {

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				// response.pageCount = originTxtRspContent;
				Document xml = Jsoup.parse(originTxtRspContent);
				String remove = "";

				Element content = xml.select("ul.tableCont").first();
				removeChild(content, remove);
				Elements list = content.select("li:has(a)");

				for (Element value : list) {

					Element aElement = value.select("a").first();
					Element dateElement = value.select("span").last();

					BranchNew bn = new BranchNew();
					bn.title = getTitle(aElement);
					bn.date = getDate(dateElement);
					bn.id = getHrefString(aElement, "");
					response.list.add(bn);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public IwResponse sendIwRequest(IwRequest request) {
		IwResponse response = null;
		WebClient webclient = new WebClient(BrowserVersion.CHROME);
		try {
			String LX = request.getRequestPathParam("LX");
			String url = "http://www.gzcsx.gov.cn/xxgk/zfxxgkml/zfcg" + LX + "/index.shtml";
			String page = request.getRequestPathParam("page");

			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(true);
			webclient.getOptions().setTimeout(1000 * 60);
			HtmlPage htmlpage = webclient.getPage(url);
			webclient.waitForBackgroundJavaScript(1000 * 1);
			HtmlSelect select = (HtmlSelect) htmlpage.getElementById("page_num");
			select.setDefaultValue(page);
			HtmlOption option = select.getOptionByValue(page);
			htmlpage = option.click();
			String content = htmlpage.asXml();
			response = new DefaultIwResponse(null, content.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webclient.closeAllWindows();
		}
		return response;
	}

	public String getTitle(Element titElement) {
		String titString = titElement.attr("title").replaceAll(SPACE, "");
		if (titString == null || "".equals(titString)) {
			titString = titElement.text().replaceAll(SPACE, "");
		}
		return titString;
	}

	public String getHrefString(Element aElement, String beginIndex) {
		String hrefString = aElement.attr("href");
		if (!"".equals(beginIndex)) {
			hrefString = hrefString.substring(hrefString.indexOf(beginIndex) + beginIndex.length());
		}
		urlFilter(hrefString);
		return hrefString;
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

	public final String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	}

}
