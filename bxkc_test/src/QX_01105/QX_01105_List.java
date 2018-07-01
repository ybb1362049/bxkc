package QX_01105;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class QX_01105_List extends TxtReqRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	private static Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static Pattern page = Pattern.compile("共(\\d*)页");

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + ";]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Matcher m;
				Document doc = Jsoup.parse(originTxtRspContent);
				Element dive = doc.select("span.total").first();
				if (dive != null) {
					m=page.matcher(dive.text());
					if(m.find())
					{
						response.pageCount=m.group(1);
					}
				}
				Element div = doc.select("ul[class=newsList]").first();
				if (div != null) {
					Element ul = div.select("ul").first();
					Elements list = ul.select("li:has(a)");
					for (int i = 0; i < list.size(); i++) {
						Element li = list.get(i);
						BranchNew bn = new BranchNew();
						Element a = li.select("a").last();
						String id = a.attr("href");
						//id = id.substring(id.indexOf("?") + 1);
						bn.url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=QX_01105_Detail&iw_ir_1=" + id;
						m = p.matcher(li.text());
						String date = "";
						if (m.find()) {
							String month = m.group(2).length() == 2 ? m.group(2) : "0" + m.group(2);
							String day = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
							date = m.group(1) + "-" + month + "-" + day;
						}
						String title = a.text().trim();
						bn.id = id;
						bn.title = title.replaceAll("\\[(.*?)\\]", "");
						bn.date = date;
						response.list.add(bn);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	private String host = "";
	private Integer port;
	public static final String PROXY_POOL_API = "http://192.168.0.112:17080/bxkc_data_proxy_pool/proxy";
	//public static final String PROXY_POOL_API ="http://121.46.18.113:17080/bxkc_data_proxy_pool/proxy";
	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String url = "http://" + iwRequest.getHost() + iwRequest.getRequestPath();
			String str = getContent(url, "");
			iwr = new DefaultIwResponse(null, str.getBytes("utf-8"), null, 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwr;
	}

	public String getContent(String url, String page)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		String str = "";
		url += page;
		CloseableHttpClient client = null;
		// HttpHost httphost = null;
		// httphost = invokeProxyPoolApi(PROXY_POOL_API);
		client = getHttpClient();
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-type", "text/html;charset=utf-8");
		httpGet.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).setSocketTimeout(45 * 1000)
				.build();
		httpGet.setConfig(requestConfig);
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "utf-8"));
				StringBuilder sb = new StringBuilder();
				while ((str = reader.readLine()) != null) {
					sb.append(str);
				}
				str = sb.toString();
			}
		} catch (Exception e) {
		} finally {
			try {
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 从IP代理池中获取一个IP
	 */
	public HttpHost invokeProxyPoolApi(String path) {
		String respStr = "";
		HttpHost proxy = null;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse res;
		HttpGet httpGet = new HttpGet(path);
		httpGet.addHeader("Content-type", "application/json;charset=UTF-8");
		httpGet.setHeader("Accept", "*/*");
		try {
			res = client.execute(httpGet);
			int statusCode = res.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 建议的写法1（编码要去Http头信息中查看）
				respStr = EntityUtils.toString(res.getEntity(), "UTF-8");
				String ip = null;
				Integer port = null;
				try {
					JSONObject json = new JSONObject(respStr);
					if (json.has("result")) {
						JSONArray arr = json.getJSONArray("result");
						JSONObject ipJson = arr.getJSONObject(0);
						ip = ipJson.getString("ip");
						port = ipJson.getInt("port");
						proxy = new HttpHost(ip, port);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
		return proxy;
	}

	/**
	 * 获取HttpClient实体
	 * 
	 * @param httphost
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	public CloseableHttpClient getHttpClient()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder b = HttpClientBuilder.create();
		boolean isVal = false;

		HttpHost httpHost = null;
		// 验证IP
		HttpURLConnection connection = null;
		if (!host.equals("")) {
			try {
				URL url = new URL("http://jzsc.mohurd.gov.cn");
				InetSocketAddress addr = new InetSocketAddress(host, port);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
				connection = (HttpURLConnection) url.openConnection(proxy);
				connection.setConnectTimeout(4 * 1000); // 4s
				connection.setReadTimeout(4 * 1000);
				connection.setUseCaches(false);
				int code = connection.getResponseCode();
				if (code == 200) {
					isVal = true;
					httpHost = new HttpHost(host, port);
				}
			} catch (Exception e) {
			}
		}
		int i = 5;
		while (!isVal && i >= 0) {
			try {
				httpHost = invokeProxyPoolApi(PROXY_POOL_API);
				host = httpHost.getHostName();
				port = httpHost.getPort();
				URL url = new URL("http://jzsc.mohurd.gov.cn");
				// URL url = new URL("http://www.baidu.com/");
				InetSocketAddress addr = new InetSocketAddress(host, port);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
				connection = (HttpURLConnection) url.openConnection(proxy);
				connection.setConnectTimeout(4 * 1000); // 4s
				connection.setReadTimeout(4 * 1000);
				connection.setUseCaches(false);
				int code = connection.getResponseCode();
				if (code == 200) {
					break;
				}
			} catch (Exception e) {
			} finally {
				i--;
			}
		}

		// 设置访问代理
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);
		b.setRoutePlanner(routePlanner);

		CloseableHttpClient httpclient = b.build();
		return httpclient;
	}
}
