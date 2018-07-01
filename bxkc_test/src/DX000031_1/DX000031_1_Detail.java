package DX000031_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class DX000031_1_Detail extends TxtReqRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");
	private String url = "";

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
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String host = iwRequest.getHost();
			String path = iwRequest.getRequestPath();
			String threadID = iwRequest.getRequestPathParam("threadID");
			url = "http://" + host + path + "?threadID=" + threadID;
			System.out.println("url=" + url);
			String str = getContent(url);
			iwr = new DefaultIwResponse(null, str.getBytes("GBK"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwr;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				String path = "http://ec.ceec.net.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				//取正文内容
				Element div_con = xml.select("div[align=center]").first();
				//去掉不必要的script标签，避免与官网的js冲突
				Elements scripts = div_con.select("script");
				for(Element script : scripts){
					script.remove();
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
				
				Elements uselessTags = div_con.select("td[height=22px]");
				for(Element tde : uselessTags){
					if(tde.text().contains("郑重声明")){
						tde.parent().remove();
					}
				}
				
				String content = div_con.outerHtml() + "<div>网站来源：<a href=" + url + ">" + url + "</a></div>";;
				response.content = content;
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String str = div_con.text().trim().replace("/", "-");
				Matcher m = p.matcher(str);
				if(m.find()){
					String date = sdf.format(sdf.parse(m.group()));
					response.date = date;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String getContent(String url) {
		String str = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=GBK");
		httpGet.addHeader("Accept", "text/html");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
		httpGet.setConfig(requestConfig);
		
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "GBK"));
				StringBuilder sb = new StringBuilder();
				while ((str = reader.readLine()) != null) {
					sb.append(str);
				}
				str = sb.toString();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

}
