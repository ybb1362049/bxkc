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

public class GJ_03523_Detail extends TxtReqRspHandler {

	private static Pattern p = Pattern.compile("20\\d{2}-\\d{1,2}-\\d{1,2}");
//	public static final String PROXY_POOL_API = "http://192.168.0.112:17080/bxkc_data_proxy_pool/proxy";
	public static final String PROXY_POOL_API = "http://121.46.18.113:17080/bxkc_data_proxy_pool/proxy";
	
	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
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
				response.content = originTxtRspContent;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String path = "http://www.chinatax.gov.cn";
				Document xml = Jsoup.parse(originTxtRspContent);
				xml.outputSettings().prettyPrint(true);
				
				Element conTag = xml.getElementById("tax_content");
				String title = "";
				Element titleTag = xml.select("li.sv_texth1").first();
				if(titleTag != null){
					title = titleTag.text().trim();
					response.title = title;
				}
//				Element conTag = xml.select("div.Contnet").first();
//				Element conTag1 = xml.select("div.Contnet").first().select("div:has(p)").get(1);
				Elements scripts = conTag.select("script");
				for (Element script : scripts) {
					script.remove();
				}
				Elements metas = conTag.select("meta");
				for (Element metae : metas) {
					metae.remove();
				}
				Elements styles = conTag.select("style");
				for (Element style : styles) {
					style.remove();
				}
				
				// 补全附件的链接
				Elements as = conTag.select("a");
				for (Element ae : as) {
					String href = ae.attr("href");
					if (!"#".equals(href) && !href.startsWith("http") && href.length() > 0 && !href.startsWith("HTTP") && href.startsWith("/filesrv")) {
						if (href.startsWith("/")) {
							href = path + href;
						} else {
							href = path + "/" + href;
						}
						ae.attr("href", href);
					}
				}

				Elements imgs = conTag.select("img");
				for (Element imge : imgs) {
					String src = imge.attr("src");
					if (!src.contains("http") && !src.contains("HTTP")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						imge.attr("src", src);
					}
				}
				Elements iframes = conTag.select("iframe");
				for (Element iframe : iframes) {
					String src = iframe.attr("src");
					if (!src.contains("http") && !src.contains("HTTP")) {
						if (src.startsWith("/")) {
							src = path + src;
						} else {
							src = path + "/" + src;
						}
						iframe.attr("src", src);
					}
				}
				Element uselessTag = conTag.select("div[class=news-date tac ptb10]").first();
				if(uselessTag != null){
					uselessTag.remove();
				}
				
				Element dateTag = xml.select("div.zuo1").first();
				if(dateTag != null){
					String str = dateTag.text().trim();
					str = str.replaceAll("[.|/|年|月]", "-");
					Matcher m = p.matcher(str);
					if(m.find()){
						String date = sdf.format(sdf.parse(m.group()));
						response.date = date;
					}
				}
				
//				Element uselessTag1 = conTag.select("div:contains(发布时间)").first();
//				if(uselessTag1 != null){
//					uselessTag1.remove();
//				}
//				Element uselessTag2 = conTag.getElementById("idBoxOpen");
//				if(uselessTag2 != null){
//					uselessTag2.remove();
//				}
				
//				String content = conTag.outerHtml();
				String content = "<div>" + title + "</div>" + conTag.outerHtml();
				response.content = content;
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
			String path = iwRequest.getRequestPath();
			String url = "http://" + host + path;
			String str = getContent(url);
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
