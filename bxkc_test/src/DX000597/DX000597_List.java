package DX000597;

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

public class DX000597_List extends TxtRspHandler {

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
				// Element boxElement=xml.select("table[width=92%]").first();
				Elements listElements = xml.select("table[width=92%]");
				for (Element lists : listElements) {
					Elements listElement = lists.select("table");
					for (Element list : listElement) {
						BranchNew bn = new BranchNew();
						Element hrefElement = list.select("a").first();
						Element dateElement = list.select("td").last();
						String dateString = dateElement.text().replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
						dateString = sdf.format(sdf.parse(dateString));
						String id = hrefElement.attr("href");
						id = id.substring(id.indexOf("m/") + 2);
						String title = hrefElement.text().replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						bn.title = title;
						bn.id = id;
						bn.date = dateString;
						response.list.add(bn);
					}
				}
				/*
				 * Element pageElement=xml.select("div#page").last(); Element
				 * pages=pageElement.select("a").last(); String
				 * page=pages.attr("href");
				 * page=page.substring(page.lastIndexOf("/")+1,page.indexOf("-")
				 * ); response.pageCount=page;
				 */
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