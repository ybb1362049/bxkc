package DS_04011;

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

public class DS_04011_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Pattern p = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
	private static Matcher m;
	
	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
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
				Document xml = Jsoup.parse(originTxtRspContent);
				Elements scripts = xml.select("script");
				for(Element script : scripts){
					script.remove();
				}
				Element h2e = xml.select("div[class=z_topic]").first().select("h2").first();
				String title = h2e.text().trim();
				response.title = title;
				Element div = xml.select("div[class=z_content1]").first();
				Elements as = div.select("a");
				for(Element ae : as){
					String href = ae.attr("href");
					if(!href.contains("http")){
						href = "http://www.zjul.com.cn" + href;
						ae.attr("href", href);
					}
					if(ae.text().contains("关闭页面")){
						ae.parent().remove();
					}
				}
				String content = response.title + div.outerHtml();
				response.content = content;
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
