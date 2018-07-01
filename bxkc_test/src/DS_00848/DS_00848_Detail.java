package DS_00848;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

public class DS_00848_Detail extends TxtReqRspHandler {

	private static RspState rsp = RspState.Login;
	private String type = "";
	
	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
			type = iwRequest.getRequestPathParam("type");
			String url = "http://" + host + path;
			System.out.println("url=" + url);
			String str = getContent(url, "utf-8");
			iwr = new DefaultIwResponse(null, str.getBytes("utf-8"), null, 0, "ok");
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
				String path = "http://cd.ggzyjyw.com";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				Element div_con = xml.select("div[class=ainfo f-m0]").first();
				Element h1e = div_con.select("h1").first();
				if(h1e == null){
					Element table = xml.select("div[class=detail]").first().select("table").first();
					
					String content = "";
					Element dive = null;
					Elements trs = table.select("tr:has(a)");
					if("1".equals(type)){
						for(int i = 0 ; i < trs.size() ; i++){
							Element tre = trs.get(i);
							Element ae = tre.select("a").first();
							String detailUrl = path + ae.attr("href");
							String str = getContent(detailUrl, "utf-8");
							Document doc = Jsoup.parse(str);
							dive = doc.select("div[class=ainfo f-m0]").first();
						}
					}
					if("2".equals(type)){
						for(Element tre : trs){
							if(tre.text().contains("变更")){
								Element ae = tre.select("a").first();
								String detailUrl = path + ae.attr("href");
								String str = getContent(detailUrl, "utf-8");
								Document doc = Jsoup.parse(str);
								dive = doc.select("div[class=ainfo f-m0]").first();
							}
						}
					}
					if("3".equals(type)){
						for(Element tre : trs){
							if(tre.text().contains("补遗")){
								Element ae = tre.select("a").first();
								String detailUrl = path + ae.attr("href");
								String str = getContent(detailUrl, "utf-8");
								Document doc = Jsoup.parse(str);
								dive = doc.select("div[class=ainfo f-m0]").first();
							}
						}
					}
					if("4".equals(type)){
						for(Element tre : trs){
							if(tre.text().contains("中标") || tre.text().contains("废标")
									|| tre.text().contains("终止") || tre.text().contains("结果")
									|| tre.text().contains("成交")){
								Element ae = tre.select("a").first();
								String detailUrl = path + ae.attr("href");
								String str = getContent(detailUrl, "utf-8");
								Document doc = Jsoup.parse(str);
								dive = doc.select("div[class=ainfo f-m0]").first();
							}
						}
					}
					
					//补全附件的链接
					Elements as = dive.select("a");
					for(Element ae : as){
						String href = ae.attr("href");
						if(!href.contains("http") && href.length() > 0 && !href.contains("HTTP")){
							if(href.startsWith("/")){
								href = path + href;
							} else {
								href = path + "/" + href;
							}
							ae.attr("href", href);
						}
					}
					
					Elements imgs = dive.select("img");
					for(Element imge : imgs){
						String src = imge.attr("src");
						if(!src.contains("http") && !src.contains("HTTP")){
							if(src.startsWith("/")){
								src = path + src;
							} else {
								src = path + "/" + src;
							}
							imge.attr("src", src);
						}
					}
					
					Elements trss = table.select("tr:has(a)");
					for(Element tre : trss){
						tre.remove();
					}
					content = table.outerHtml() + dive.outerHtml();
					response.content = content;
				} else {
					Element dateEle = div_con.select("div[class=pubdate]").first();
					if(dateEle != null){
						dateEle.remove();
					}
					String title = h1e.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
					response.title = title;
					String content = div_con.outerHtml();
					response.content = content;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public static String getContent(String url,String charset) {
		String str = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept", "text/html");
		httpGet.addHeader("Content-type", "text/html; charset=" + charset);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
		httpGet.setConfig(requestConfig);
		
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, charset));
				StringBuilder sb = new StringBuilder();
				while ((str = reader.readLine()) != null) {
					sb.append(str + "\n");
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
