package XX0942;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class XX0942_List extends TxtReqRspHandler {
	class BranchNew {
		String id;
	}

	class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		try {
			Pattern pattern = Pattern
					.compile("remoteHandleCallback\\(.*?\"(.*)\"\\)");
			Matcher matcher = pattern.matcher(arg1);
			if(matcher.find()){
				String origin = matcher.group(1);
				Document doc = Jsoup.parse(origin,"",Parser.xmlParser());
				Elements links = doc.select("link");
				for (Element link:links) {
					BranchNew bn = new BranchNew();
					String id = link.text();
					id= id.substring(id.indexOf("/news/"));
					bn.id = id;
					response.list.add(bn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest request) {
		IwResponse iwResponse = null;
		String page =request.getRequestPathParam("page");
		String type = "1";
		String params = "";
		String url = "http://www.cqepc.com.cn/dwr/call/plaincall/portalAjax.getNewsXml.dwr";
		if (type.equals("1")) {
			params = "callCount=1"
					+ "\npage=//type/990219.html"
					+ "\nhttpSessionId=B5B1653A0C16CF824AF919D5FBC52406"
					+ "\nscriptSessionId=8868B9C81C49E2A37C9333A60E3D6DF4679"
					+ "\nc0-scriptName=portalAjax"
					+ "\nc0-methodName=getNewsXml" + "\nc0-id=0"
					+ "\nc0-param0=string:9902"
					+ "\nc0-param1=string:990219"
					+ "\nc0-param2=string:news_" + "\nc0-param3=number:30"
					+ "\nc0-param4=number:"+page+"" + "\nc0-param5=null:null"
					+ "\nc0-param6=null:null" + "\nbatchId=2";
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader(
				"Cookie",
				"UM_distinctid=15fb840f1a142a-0f211ddf5015ec-5c153d17-13c680-15fb840f1a2374; JSESSIONID=B5B1653A0C16CF824AF919D5FBC52406; CNZZDATA2274150=cnzz_eid%3D817577688-1510624133-%26ntime%3D1510624133");
		post.setHeader("Referer",
				"http://www.cqepc.com.cn//type/990219.html");
		post.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36");
		post.setHeader("Content-Type", "text/plain");
		String result = "";
		try {
			StringEntity entity = new StringEntity(params);
			post.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200)
				result = IOUtils.toString(response.getEntity().getContent(),
						"UTF-8");
			iwResponse = new DefaultIwResponse(null, result.getBytes("UTF-8"),
					Charset.forName("UTF-8"), 200, "ok");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iwResponse;
	}

}
