package DS_01418;

import java.text.SimpleDateFormat;
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

public class DS_01418_Detail extends TxtRspHandler {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

	public class Response extends TxtBaseResponse {
		String title;
		String content;
		String date;

		@Override
		public String toString() {
			return "Response [title=" + title + ", content=" + content + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		if (arg0 == RspState.Login) {
			try {
				Document xml = Jsoup.parse(arg1);
				response.title = xml.select("div.zone> h3").first().text().trim();
				String str = xml.select("div.info").first().text();
				String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(str);
				if (matcher.find()) {
					String date = matcher.group();
					response.date = sdf.format(sdf.parse(date));
				}
				Element content = xml.select("div.newscontent").first();
				content.select("table.prev_next").remove();
				content.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				content.select("script").remove();
				if (content.html().contains("<img")) {
					Elements imgs = content.select("img");
					for (Element img : imgs) {
						img.attr("src", "http://www.ncagri.gov.cn" + img.attr("src"));
					}
				}
				if (content.html().contains("</a>")) {
					Elements as = content.select("a");
					for (Element a : as) {
						a.attr("href", "http://www.ncagri.gov.cn/" + a.attr("href"));
					}
				}
				response.content = content.outerHtml();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
