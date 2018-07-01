package GJ_03760;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

public class GJ_03760_List extends TxtReqRspHandler {
	
	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwResponse = null;
		try {
			String infoClassCodes = iwRequest.getRequestContentParam("infoClassCodes");
			String zoneCode = iwRequest.getRequestContentParam("zoneCode");
			String currentPage = iwRequest.getRequestContentParam("currentPage");
			
			String url = "http://www.chinabidding.com/search/proj.htm";
			String paramStr = "infoClassCodes=" + infoClassCodes + "&zoneCode=" + zoneCode + "&currentPage=" + currentPage;
			
			String resp = sendPost(url, paramStr);

			iwResponse = new DefaultIwResponse(null, resp.getBytes("UTF-8"), Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwResponse;
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
				Document document = Jsoup.parse(originTxtRspContent);
				Element listUlEle = document.select("ul.as-pager-body").first();
				Elements itemLiEles = listUlEle.select("li");
				for (Element itemLiEle : itemLiEles) {
					Element titleAEle = itemLiEle.select("a.as-pager-item").first();
					Element titleH5Ele = titleAEle.select("h5.as-p-tit").first();
					Element titleSpanEle = titleH5Ele.select("span.txt").first();
					Element dateSpanEle = titleH5Ele.select("span.time").first();
					
					String title = titleSpanEle.attr("title").trim().replaceAll("(...|···)$", "");
					String href = titleAEle.attr("href").trim();
					String id = href.substring(href.lastIndexOf("/") + "/".length());
					String date = dateSpanEle.text().replaceAll(".*?(\\d{4}-\\d{2}-\\d{2}).*", "$1").trim();
					if("贵州健康职业学院一期学生食堂西侧商铺项".equals(title)){
			            continue;
					}
					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.id = id;
					branchNew.date = date;
					response.list.add(branchNew);
				}
				
				Element pageFormEle = document.select("#pagerSubmitForm").first();
				Element pageAEle = pageFormEle.select("a[href=#]").last().nextElementSibling();
				response.pageCount = pageAEle.text().trim();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	/**
	 * 利用HttpClient发送POST请求
	 * @param url
	 * @param paramStr，格式："name1=value1&name2=value2&name3=value3"
	 * @return
	 */
	public static String sendPost(String url, String paramStr) {
		String resp = "";
		CloseableHttpClient httpClient = null;
		
		try {
			httpClient = HttpClients.createDefault();
			HttpPost request = new HttpPost(url);
			request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (compatible; Baiduspider/2.0;+http://www.baidu.com/search/spider.html)");;
			
			List<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>();
			String[] params = paramStr.split("&");
			for (String param : params) {
				String[] paramInfos = param.split("=");
				BasicNameValuePair pair = new BasicNameValuePair(paramInfos[0], paramInfos[1]);
				paramList.add(pair);
			}
			
			UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(paramList);
			request.setEntity(requestEntity);
			
			int retryTimes = 5 + randomGenerator.nextInt(6);
			do {
				CloseableHttpResponse httpResponse = httpClient.execute(request);
				resp = EntityUtils.toString(httpResponse.getEntity());
				
				int httpStatus = httpResponse.getStatusLine().getStatusCode();
				// System.out.println("status: " + httpStatus);
				if (httpStatus == HttpStatus.SC_OK) {
					break;
				} else {
					try {
						Thread.sleep((randomGenerator.nextInt(5) + 1) * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} while (--retryTimes > 0);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return resp;
	}

	private static Random randomGenerator = new Random();
	
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String province;
	}
	
}