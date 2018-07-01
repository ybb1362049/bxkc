package DX000574;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000574_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String path = "http://www.scgngs.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				//取正文内容
				Element conTag = xml.select("div[class=NewsShow]").first();
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
				
				Element uselessTag = conTag.select("p").get(1);
				if(uselessTag != null){
					String str = uselessTag.text().trim();
					if(str.contains("发布日期")){
						Matcher m = p.matcher(str);
						str = str.replaceAll("[.|/|年|月]", "-");
						if(m.find()){
							String date = sdf.format(sdf.parse(m.group()));
							response.date = date;
						}
						uselessTag.remove();
					}
				}
				Element uselessTag1 = conTag.select("a[onclick=javascript:window.close();]").last();
				if(uselessTag1 != null){
					uselessTag1.parent().remove();
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
