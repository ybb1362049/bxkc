package DS_00581;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DS_00581_Detail extends TxtRspHandler{

	public class Response extends TxtBaseResponse {
		String content;
	}
	
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, 
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);
				Element detailDivElement = document.select("div[class=box-container]").first();
				detailDivElement.select("script").remove();
				if(!detailDivElement.select("span[id=_ideConac]").isEmpty()){
				detailDivElement.select("span[id=_ideConac]").parents().first().remove();
				}
				detailDivElement.select("div[class=notice-publish-info]").remove();
				detailDivElement.select("div[class=button-controls]").remove();

				for (Element aElement : detailDivElement.select("a[href]")) {
					String href = aElement.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.zyjyw.cn" + href;
					}
					aElement.attr("href", href);
				}
				for (Element srcElement : detailDivElement.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "http://www.zyjyw.cn" + src;
					}
					srcElement.attr("src", src);
				}
				response.content = "<div>\n";
				response.content += detailDivElement.outerHtml();
				response.content += "\n</div>";
				}
				catch(Exception e){e.printStackTrace();}
		}
		return response;
	}
}
