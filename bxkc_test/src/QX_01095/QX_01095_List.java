package QX_01095;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_01095_List extends TxtRspHandler {

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				JSONObject json = new JSONObject(originTxtRspContent);
				String main = json.get("showinfo").toString();
				Document xml = Jsoup.parse(main);
				String remove = "";

				Element content = xml;
				removeChild(content, remove);
				Elements list = content.select("li:has(a)");

				for (Element value : list) {

					Element aElement = value.select("a").first();
					Element dateElement = value.select("span").last();

					BranchNew bn = new BranchNew();
					bn.title = getTitle(aElement);
					bn.date = getDate(dateElement);
					bn.id = getHrefString(aElement, "?");
					response.list.add(bn);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
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
		return hrefString;
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
		for (String child : children) {
			String method = child.substring(child.indexOf(",") + 1);
			if (method.matches("//d")) {
				int index = Integer.valueOf(method);
				if (index >= 0) {
					content.select(child).get(index).remove();
				} else {
					index = content.select(child).size() + index;
					content.select(child).get(index).remove();
				}
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
