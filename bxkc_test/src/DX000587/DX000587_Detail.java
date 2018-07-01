package DX000587;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DX000587_Detail extends TxtRspHandler {


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
				Element detailDivEle = document.select("div.con1").first();
				Element titleDivEle = document.select("div.bt1 h2").first();
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
				Pattern pattern = Pattern.compile("\\D(\\d{4})\\D(\\d{1,2})\\D(\\d{1,2})\\D");
				Matcher matcher = pattern.matcher(StringUtils.deleteWhitespace(detailDivEle.text()));
				SimpleDateFormat originsdf = new SimpleDateFormat("yyyy-M-d");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				while (matcher.find()) {
					Date date = originsdf.parse(matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3));
					if (date.getTime() < new Date().getTime())
						response.date = sdf.format(date);
				}
				if (response.date == null) {
					response.date = sdf.format(new Date());
				}
				if (sdf.parse(response.date).getTime()<1451577600000l) {
					response.title="不需要的标题，因为拿不到时间。时间都没有还敢说数据是有用的？";
					response.date = sdf.format(new Date());
				}
				response.content = "<div>\n";
				if (titleDivEle != null) {
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
