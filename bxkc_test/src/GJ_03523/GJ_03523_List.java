package GJ_03523;

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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
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

public class GJ_03523_List extends TxtReqRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");
	private String totalPage;
	private String page;
	private String path;
//	public static final String PROXY_POOL_API = "http://192.168.0.112:17080/bxkc_data_proxy_pool/proxy";
	public static final String PROXY_POOL_API = "http://121.46.18.113:17080/bxkc_data_proxy_pool/proxy";

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	public class BranchNew {
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
				Element totalTag = xml.getElementById("comp_831221");
				if(totalTag == null){
					totalTag = xml;
				}
				Elements eachTags = totalTag.select("dd:has(a)");
				for(Element eachTag : eachTags){
					Element ae = eachTag.select("a").first();
					String title = ae.attr("title").trim();
					if(title.length() < 1){
						title = ae.text().trim();
					}
					title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
					title = title.replaceAll("\\[[^\\]]+]", "");
					title = title.replace("...", "");
					if(title.length() < 2){
						continue;
					}
					String str = eachTag.text().trim();
					str = str.replaceAll("[.|/|年|月]", "-");
					String date = "";
					Matcher m = p.matcher(str);
					if(m.find()){
						date = sdf.format(sdf.parse(m.group()));
					}
					String id = ae.attr("href").trim();
					if(id.startsWith("../")){
						id = "/" + id.replace("../", "");
					}
//					id = id.substring(id.indexOf("?") + 1 , id.length());
//					id = id.replace("?", "&");
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
								+ "&iw-cmd=GJ_03523_Detail&iw_ir_1=" + id;
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.title = title;
					bn.url = url;
					response.list.add(bn);
				}
				String pathParam = path.substring(path.indexOf("n810621/") + "n810621/".length() , path.indexOf("index"));
				String nextPage = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
						+ "&iw-cmd=GJ_03523_List&iw_ir_2=" + pathParam + "index_831221_" + (Integer.valueOf(totalPage) - Integer.valueOf(page)) + "&page=" + (Integer.valueOf(page) + 1);
				response.nextPage = nextPage;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String host = iwRequest.getHost();
			path = iwRequest.getRequestPath();
			page = iwRequest.getRequestPathParam("page");
			String url = "http://" + host + path;
			String str = getContent(url);
			if(totalPage == null || totalPage.length() == 0 || "null".equals(totalPage)){
				String pathParam = path.substring(0, path.indexOf("index"));
				String oriUrl = "http://" + host + pathParam + "index.html";
				String oriCon = getContent(oriUrl);
				String str1 = oriCon.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]+", "");
				totalPage = str1.replaceAll(".*maxPageNum831221=(\\d+)\";.*", "$1");
			}
			iwr = new DefaultIwResponse(null, str.getBytes("UTF-8"), null, 0, "ok");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwr;
	}
	
	public String getContent(String url) {
		String str = "";
		CloseableHttpClient client = null;
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=UTF-8");
		httpGet.addHeader("Accept", "*/*");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
		httpGet.setConfig(requestConfig);
		
		try {
			client = getHttpClient();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
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
	public CloseableHttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder b = HttpClientBuilder.create();

		// 禁用SSL验证
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();
		b.setSslcontext(sslContext);
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslSocketFactory).build();
		HttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);
		b.setConnectionManager(connMgr);


		HttpHost httpHost = null;
		// 验证IP
		HttpURLConnection connection = null;
		int i = 5;
		while (i >= 0) {
			try {
				httpHost = invokeProxyPoolApi(PROXY_POOL_API);
				String host = httpHost.getHostName();
				Integer port = httpHost.getPort();
				URL url = new URL("http://www.chinatax.gov.cn");
				// URL url = new URL("http://www.baidu.com/");
				InetSocketAddress addr = new InetSocketAddress(host, port);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
				connection = (HttpURLConnection) url.openConnection(proxy);
				connection.setConnectTimeout(4 * 1000); // 4s
				connection.setReadTimeout(4 * 1000);
				connection.setUseCaches(false);
				int code = connection.getResponseCode();
				if (code == 200) {
					System.out.println(host + ":" + port);
					break;
				}
			} catch (Exception e) {
				System.out.println("the " + i + " time to error");
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
