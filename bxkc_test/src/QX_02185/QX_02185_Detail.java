package QX_02185;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_02185_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
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
				Document document=Jsoup.parse(originTxtRspContent);
				Element detailEle=document.select("div[class=wz_content]").first();
				Element titleEle=document.select("div[class=wztit]").first();
				detailEle.select("script").remove();

				for (Element aElement : detailEle.select("a[href]")) {
					String href = aElement.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.huaishang.gov.cn" + href;
					}
					aElement.attr("href", href);
				}
				for (Element srcElement : detailEle.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "http://www.huaishang.gov.cn" + src;
					}
					srcElement.attr("src", src);
				}
				response.content = "<div>\n"+titleEle.outerHtml();
				response.content += detailEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
