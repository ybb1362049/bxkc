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
import java.text.SimpleDateFormat;

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
import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class QX_01105_Detail extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://www.xuyong.gov.cn";
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
				Element dive = doc.select("h2.title").first();
				if (dive != null) {
					content = dive.outerHtml();
					response.title = dive.text().replaceAll("\\s*", "");
				}
				Element div = doc.select("div.conTxt").first();
				if (div != null) {
					div.select("script").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
						if (href.equals("#")) {
							a.remove();
							continue;
						}
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
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								a.attr("href", href);
							}
						}
					}
					Elements imgList = div.select("IMG");
					for (Element img : imgList) {
						String href = img.attr("src");
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								img.attr("src", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								img.attr("src", href);
							} else if (href.indexOf("/") == 0) {
								href = url + href;
								img.attr("src", href);
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								img.attr("src", href);
							}
						}
					}
					String con = div.outerHtml();
					con = con.replaceAll("上一篇：|下一篇：", "");
					content += con;
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	private String host = "";
	private Integer port;
	 public static final String PROXY_POOL_API = "http://192.168.0.112:17080/bxkc_data_proxy_pool/proxy";
	//public static final String PROXY_POOL_API = "http://121.46.18.113:17080/bxkc_data_proxy_pool/proxy";

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwr = null;
		try {
			String url = "http://" + iwRequest.getHost() + iwRequest.getRequestPath();
			Thread.sleep(1000);
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
