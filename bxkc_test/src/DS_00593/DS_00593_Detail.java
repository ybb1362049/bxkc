package DS_00593;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.Utils;

public class DS_00593_Detail extends TxtReqRspHandler {

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
				Element detailDivEle = document.select("div.page_contect.bai_bg").first();
				Element titleDivEle = document.select("h4.detail_t").first();
				detailDivEle.select("div.bshare-custom").remove();
				detailDivEle.select("div.prev_next").remove();
				detailDivEle.select("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]").remove();
				detailDivEle.select("script").remove();
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
				path = path.substring(0, path.indexOf("/", 8));
				for (Element aEle : detailDivEle.select("a[href]")) {
					String href = aEle.attr("href");
					String lowerCaseHref = href.toLowerCase(Locale.ENGLISH);
					if (lowerCaseHref.startsWith("javascript")) {
						href = href.substring(href.indexOf("'") + 1, href.lastIndexOf("'"));
						href = "/JyWeb/XXGK/GetVW_XXGK_FJ_List?guid=" + href;
					}
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
				response.content += "<div>跳转原网下载附件：<a href='" + this.path + "'>" + this.path + "</a></div>";
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	private String path = null;

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			String guid = iwRequest.getRequestPathParam("guid");
			this.path = "http://" + iwRequest.getHost() + ":" + iwRequest.getPort() + iwRequest.getRequestPath()
					+ "?guid=" + guid;
			String result = Utils.callApi(this.path);
			iwResponse = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 200, "OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
	}

}
