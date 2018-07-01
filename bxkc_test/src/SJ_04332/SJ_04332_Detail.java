package SJ_04332;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class SJ_04332_Detail extends TxtRspHandler {
	private SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd");

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
				Element detailDivEle = document.select("td[style=PADDING-BOTTOM: 10px; PADDING-LEFT: 10px; PADDING-RIGHT: 10px; PADDING-TOP: 10px]").first();
				detailDivEle.select("script").remove();
				detailDivEle.select("li").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8) + 1);
				for (Element aEle : detailDivEle.select("a[href]")) {
					String href = aEle.attr("href");
					if (!href.startsWith("http")) {
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
				String date = null;
				date = document.select("p").first().text().trim();
				document.select("p").first().remove();
				date = date.replaceAll(".*(\\d{4})[\\年|\\-|\\/|\\.](\\d{1,2})[\\月|\\/|\\-|\\.](\\d{1,2})[\\日|\\-|\\/|\\.]?.*", "$1-$2-$3");
				if (date.length()>20) {
					date = newsdf.format(new Date());
				}else {
					date = newsdf.format(newsdf.parse(date));	
				}
				response.content = "<div>\n";
				response.date = date;
				response.content += detailDivEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
