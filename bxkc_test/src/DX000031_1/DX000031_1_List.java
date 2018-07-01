package DX000031_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DX000031_1_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public static class BranchNew {
		String title;
		String id;
		String date;
		String url;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", url=" + url + "]";
		}
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
				response.pageCount = originTxtRspContent;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Document xml = Jsoup.parse(originTxtRspContent);
				Elements divs = xml.select("div[class=list01]:has(a)");
				for(Element dive : divs){
					Element ae = dive.select("a").last();
					String title = ae.attr("title");
					if(title.length() < 1){
						title = ae.text().trim();
					}
					title = title.replaceAll("\\[[^\\]]+\\]", "");
					String str = dive.text().trim();
					Matcher m = p.matcher(str);
					String date = "";
					if(m.find()){
						date = sdf.format(sdf.parse(m.group()));
					}
					String id = ae.attr("href");
					String url = "";
					if(id.contains("zbxmbh")){
						id = id.substring(id.indexOf("?") + 1 , id.length());
						url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=DX000031_1_Detail1&" + id;
					} else {
						id = id.replace("?", "&");
						url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=DX000031_1_Detail&iw_ir_2=" + id;
					}
					BranchNew bn = new BranchNew();
					bn.id = id;
					bn.date = date;
					bn.url = url;
					bn.title = title;
					response.list.add(bn);
				}
				String str = xml.getElementById("lblCurPage").text().trim();
				String page = str.replaceAll(".*共(\\d+)页.*", "$1");
				response.pageCount = page;
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
