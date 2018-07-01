package SJ_00048;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_00048_1_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
		// String nextPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;
		String content;

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
				String top=callApi("http://www.hljcg.gov.cn/xwzs!index.action");
				Document xml = Jsoup.parse(originTxtRspContent);
				Element boxElement=xml.select("table.htgs_table").first();
				Elements listElement = boxElement.select("tr:has(td)");
				for (Element list : listElement) {
					BranchNew bn = new BranchNew();
					Element hrefElement = list.select("a").first();
					Element dateElement=list.select("td").last();
					Element titElement=list.select("td").get(1);
					String dateString=dateElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					dateString=dateString.substring(0,10);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
					dateString = sdf.format(sdf.parse(dateString));
					String id = hrefElement.attr("onclick");
					id=id.substring(id.indexOf("'")+1,id.lastIndexOf("'"));
					String title = titElement.attr("title").replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");					
					bn.content="<a href=http://www.hljcg.gov.cn/daima!download.action?sjs=9426419&fjbh="+id+">文件下载</a>";
					bn.title = title;
					bn.id = id;
					bn.date=dateString;
					response.list.add(bn);
				}

				Element pageElement=xml.select("div#page").last();
				Element pages=pageElement.select("a").last();
				String page=pages.attr("href");
				page=page.substring(page.lastIndexOf("/")+1,page.indexOf("-"));
				response.pageCount=page;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	
}