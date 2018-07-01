package DS_00700;

import java.io.IOException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

public class DS_00700_List extends TxtReqRspHandler {

	private Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private Pattern page = Pattern.compile("createPageHTML\\((\\d{1,5})");

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String id;
		String date;
		String title;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document doc = Jsoup.parse(originRspContent);
				Elements listElement = doc.select("table[class=bg]").select("tr:has(a)");
				for (Element e : listElement) {

					Element a = e.select("a[title]").last();
					if (a == null) {
						continue;
					}
					String title = a.attr("title").trim();
					String id = a.attr("href");
					if (id.indexOf("../jzxtp") == 0) {
						continue;
					}
					id = id.substring(id.indexOf("/") + 1);

					Matcher m = p.matcher(e.select("td").last().text());
					String date = "";
					if (m.find()) {
						String month = m.group(2);
						month = month.length() == 2 ? month : "0" + month;
						String day = m.group(3);
						day = day.length() == 2 ? day : "0" + day;
						date = m.group(1) + "-" + month + "-" + day;
					}

					BranchNew branchNew = new BranchNew();
					branchNew.id = id;
					branchNew.title = title;
					branchNew.date = date;
					response.list.add(branchNew);
				}

				String pageCount = "";
				Matcher m = page.matcher(doc.outerHtml());
				if (m.find()) {
					pageCount = m.group(1);
				}
				response.pageCount = pageCount;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			String path = "http://" + iwRequest.getHost() + iwRequest.getRequestPath();
			String result = getContent(path);
			iwResponse = new DefaultIwResponse(null, result.getBytes("UTF-8"), Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
	}

	public String getContent(String path) {
		String respStr = "";
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html");
		httpGet.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		try {
			res = client.execute(httpGet);
			try {
				respStr = EntityUtils.toString(res.getEntity(), "gbk");
			} finally {
				res.getEntity().getContent().close();
				res.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respStr;
	}

}
