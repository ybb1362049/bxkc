package SJ_04016_1;

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

public class SJ_04016_1_Detail extends TxtRspHandler {

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
				Element td_title = xml.getElementById("tdTitle");
				String title = td_title.select("b").first().text().trim();
				response.title = title;
				Element td_content = xml.getElementById("TDContent");
				Element td_file = xml.getElementById("filedown");
				Elements as = td_file.select("a");
				for(Element ae : as){
					String href = ae.attr("href");
					if(!href.contains("http")){
						href = "http://125.94.35.71" + href;
						ae.attr("href",href);
					}
				}
				String content = response.title + td_content.outerHtml() + td_file.outerHtml();
				response.content = content;
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
