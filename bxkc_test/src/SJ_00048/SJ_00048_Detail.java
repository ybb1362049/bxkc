package SJ_00048;

import java.text.SimpleDateFormat;
import java.util.Date;
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

public class SJ_00048_Detail extends TxtRspHandler {

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
				String title = xml.select("p[class=mtt_01]").first().text().trim();
				if(title != null && title.length() > 0){
					response.title = title;
				}
				String str = xml.select("div[class=mtt]").first().text();
				m = p.matcher(str);
				if(m.find()){
					String d = m.group();
					Date da = sdf.parse(d);
					String date = sdf.format(da);
					response.date = date;
				}
				Element dive = xml.select("div[class=xxej]").first();
				Element div = dive.select("div[class=mtt]").first();
				if(div != null){
					div.remove();
				}
				String content = title + dive.outerHtml();
				response.content = content;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return response;
	}

}
