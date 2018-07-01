package SJ_01390;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_01390_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
//	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

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
				String path = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				String path = "http://www.scgs.com.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				Element dive = xml.select("div[class=wy_dis_main]").first();
				
				Element titleTag = dive.select("h1[class=blue fontNb]").first();
				String title = titleTag.text().trim();
				response.title = title;
				
				//取正文内容
				Element conTag = dive.select("div[class=wy_dis_main]").first();
				//去掉不必要的script标签，避免与官网的js冲突
				Elements scripts = conTag.select("script");
				for(Element script : scripts){
					script.remove();
				}
				
				//补全附件的链接
				Elements as = conTag.select("a");
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
				
				Elements imgs = conTag.select("img");
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
				
				Element uselessTag1 = conTag.select("div[class=time f12 fontSt]").first();
				if(uselessTag1 != null){
					uselessTag1.remove();
				}
				Element uselessTag2 = conTag.select("div[class=fontSt f12]").first();
				if(uselessTag2 != null){
					uselessTag2.remove();
				}
				
				String content = conTag.outerHtml();
				response.content = content;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}



}
