package SJ_00792;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_00792_Detail extends TxtRspHandler {
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
				Element content = xml.select("div.tLayer-2").first();
				response.title = content.select("span.tTit.pwfs").first().text();
				String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(content.text());
				if (matcher.find()) {
					response.date = sdf.format(sdf.parse(matcher.group()));
				}
				content.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				content.select("script").remove();
				response.content = content.outerHtml().replace("&amp;", "&").replace("&nbsp;", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
