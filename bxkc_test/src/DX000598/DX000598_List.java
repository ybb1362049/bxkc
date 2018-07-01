package DX000598;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class DX000598_List extends TxtRspHandler {

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
				Document xml = Jsoup.parse(originTxtRspContent);
				Element boxElement=xml.select("ul.nr_new").first();
				Elements listElement = boxElement.select("li:has(a)");
				for (Element list : listElement) {
					BranchNew bn = new BranchNew();
					Element hrefElement = list.select("a").last();
					Element dateElement=list.select("span").last();
					String dateString=dateElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
					Pattern p = Pattern.compile("\\d+{4}-\\d{1,2}-\\d{1,2}");
					Matcher m;
					m = p.matcher(dateString);
					if (m.find()) {
						dateString = m.group();

					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
					dateString = sdf.format(sdf.parse(dateString));
					String id = hrefElement.attr("href");
					id=id.substring(id.indexOf("n/")+2);
					String title = hrefElement.attr("title").replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");					
					bn.title = title;
					bn.id = id;
					bn.date=dateString;
					response.list.add(bn);
				}
/*
				Element pageElement=xml.select("div#page_4133739").last();
				Element pages=pageElement.select("a").last();
				String page=pages.attr("paged");
				response.pageCount=page;
				System.out.println(page);*/
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