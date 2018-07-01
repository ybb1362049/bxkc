package QX_03936;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_03936_Detail extends TxtRspHandler {

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
				originTxtRspContent = originTxtRspContent.substring(originTxtRspContent.indexOf("(") + 1,
						originTxtRspContent.length() - 1);
				JSONArray jArray = new JSONArray(originTxtRspContent);
				JSONObject json = jArray.getJSONObject(0);
				String contentText = json.getString("content").replace("↵", "");
				Document document = Jsoup.parse(contentText);
				document.outputSettings().prettyPrint(true);
				Element detailDivEle = document.body();
				Element titleDivEle = document.select("div#tts_title").first();
				detailDivEle.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				detailDivEle.select("script").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8) + 1);
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
