package XX0942;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class XX0942_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	String url = "http://www.cdut.edu.cn";
	Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	Matcher m;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document doc = Jsoup.parse(originTxtRspContent);
				String content = "";
				Element span=doc.select("span.pubtime").first();
				String date="";
				if(span!=null)
				{
					m=p.matcher(span.text());
					if(m.find())
					{
						date=m.group();
					}
				}
				if(date.length()<10)
				{
					date=sdf.format(new Date());
				}
				response.date=date;
				Element title=doc.select("div.title").first();
				if(title!=null)
				{
					response.title=title.select("h3").first().text().replaceAll("\\s*", "");
					content=title.select("h3").first().outerHtml();
				}
				Element div = doc.select("div[id=xwcontentdisplay]").first();
				if (div != null) {
					div.select("script").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("/") == 0) {
								href = url + href;
								a.attr("href", href);
							} else if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								a.attr("href", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								a.attr("href", href);
							} else {
								href = url + "/" + href;
								a.attr("href", href);
							}
						}
					}
					Elements imgList = div.select("IMG");
					for (Element img : imgList) {
						String href = img.attr("src");
						if (href.indexOf("../") == 0) {
							href = href.replace("../", "");
							href = url + "/" + href;
							img.attr("src", href);
						} else if (href.indexOf("/") == 0) {
							href = url + href;
							img.attr("src", href);
						} else {
							href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
							img.attr("src", href);
						}
					}
					String con = div.outerHtml();
					con = con.replaceAll("已下载(.*?)次", "");
					content += con;
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
