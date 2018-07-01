package DS_00420;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00420_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String title;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login; // 默认登录成功状态
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);
				Element detailEle = document.select("table[class=about_news]").first();
				detailEle.select("script").remove();
				if(!detailEle.select("td[class=S2]").isEmpty()){
					detailEle.select("td[align=center]").remove();
				}

				for (Element aElement : detailEle.select("a[href]")) {
					String href = aElement.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.zzbidding.com" + href;
					}
					aElement.attr("href", href);
				}
				for (Element srcElement : detailEle.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "http://www.zzbidding.com" + src;
					}
					srcElement.attr("src", src);
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
