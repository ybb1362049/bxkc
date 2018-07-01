package DS_00812;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00812_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
				Element detailEle = document.select("table[id=tblInfo]").first();
				detailEle.select("script").remove();
				detailEle.select("font[class=webfont]").remove();
				detailEle.select("td[style=padding-top:20px]").remove();

				for (Element aElement : detailEle.select("a[href]")) {
					String href = aElement.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.ordosggzyjy.org.cn" + href;
					}
					aElement.attr("href", href);
				}
				for (Element srcElement : detailEle.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "http://www.ordosggzyjy.org.cn" + src;
					}
					srcElement.attr("src", src);
				}
				if (document.select("#tdTitle > font").first()!=null) {
					response.title=document.select("#tdTitle > font").first().text();
				}
				response.content = "<div>\n";
				response.content += detailEle.outerHtml();
				response.content += "\n</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
