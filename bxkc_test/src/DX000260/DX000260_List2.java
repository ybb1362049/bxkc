package DX000260;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DX000260_List2 extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
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

				Element div = xml.select("table.ui-table").first();
				Elements trsElements = div.select("tr:has(a)");
				String titString = null;
				String hrefString = null;
				for (Element tr : trsElements) {

					Element aElement = tr.select("a").first();
					Element dateElement = tr.select("td").get(1);
					hrefString = aElement.attr("href");
					hrefString = hrefString.replace("?", "&");
					titString = aElement.attr("title").replaceAll(SPACE, "");
					if (titString == null || "".equals(titString)) {
						titString = aElement.text().replaceAll(SPACE, "");
					}
					String date = getDate(dateElement);
					if (date == null || date.equals("")) {
						date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					}
					BranchNew bn = new BranchNew();
					bn.title = titString;
					bn.date = date;
					bn.id = hrefString;
					response.list.add(bn);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
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

	public final String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
}
