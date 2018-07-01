package DX000031_1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000031_1_Detail1 extends TxtRspHandler {

	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				String path = "http://ec.ceec.net.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				Element div_title = xml.getElementById("zwPanel").select("td").first();
				String title = div_title.text().trim();
				response.title = title;
				
				//取正文内容
				Element div_con = xml.getElementById("panelZW2");
				//去掉不必要的script标签，避免与官网的js冲突
				Elements scripts = div_con.select("script");
				for(Element script : scripts){
					script.remove();
				}
				Elements fonts = div_con.select("font");
				for(Element fonte : fonts){
					fonte.remove();
				}
				//补全附件的链接
				Elements as = div_con.select("a");
				for(Element ae : as){
					String href = ae.attr("href");
					if(!href.contains("http") && href.length() > 0){
						if(href.startsWith("/")){
							href = path + href;
						} else {
							href = path + "/" + href;
						}
						ae.attr("href", href);
					}
				}
				
				Elements imgs = div_con.select("img");
				for(Element imge : imgs){
					String src = imge.attr("src");
					if(!src.contains("http")){
						if(src.startsWith("/")){
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						imge.attr("src", src);
					}
				}
				
				String content = "<div>" + title + "</div>" + div_con.outerHtml();
				
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}



}
