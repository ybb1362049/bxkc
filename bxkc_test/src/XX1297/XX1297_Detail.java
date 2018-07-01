package XX1297;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class XX1297_Detail extends TxtRspHandler {

	private static SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd");

	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;

		@Override
		public String toString() {
			return "Response [title=" + title + ", date=" + date + "]";
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
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);
				Element detailDivEle = document.select("td.entry").first();
				Element titleDivEle = document.select("td.right_t i").first();
				Element dateDivEle = document.select("span.time").first();
				detailDivEle.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				detailDivEle.select("script").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8));
				for (Element aEle : detailDivEle.select("a[href]")) {
					String href = aEle.attr("href");
					String lowerCaseHref = href.toLowerCase(Locale.ENGLISH);
					if (!lowerCaseHref.startsWith("http") && !lowerCaseHref.startsWith("mailto")) {
						href = path + href;
					}
					aEle.attr("href", href);
				}
				for (Element srcEle : detailDivEle.select("*[src]")) {
					String src = srcEle.attr("src");
					if (!src.startsWith("http")) {
						src = path + src;
					}
					srcEle.attr("src", src);
				}
				String date = dateDivEle.text().replaceAll(
						".*(\\d{4})[\\年|\\-|\\/|\\.](\\d{1,2})[\\月|\\/|\\-|\\.](\\d{1,2})[\\日|\\-|\\/|\\.]?.*",
						"$1-$2-$3");
				date = newsdf.format(newsdf.parse(date));
				response.date = date;
				response.content = "<div>\n";
				if (titleDivEle != null) {
					// response.title = titleDivEle.text().trim();
					response.content += titleDivEle.outerHtml();
				}
				response.content += detailDivEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
