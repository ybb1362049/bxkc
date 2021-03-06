package SJ_00140;

import java.net.URL;
import java.net.URLConnection;
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

public class SJ_00140_List extends TxtRspHandler {

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
				Element boxElement=xml.select("table.table_text").first();
				Elements listElement = boxElement.select("tr:has(a)");
				for (Element list : listElement) {
					BranchNew bn = new BranchNew();
					Element aElement=list.select("a").last();
					String id = aElement.attr("href");
					id=id.substring(id.indexOf("0/")+2,id.lastIndexOf("'"));
					String title = aElement.attr("title").replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");					
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=SJ_00140_Detail&iw_ir_2=" + id;
					bn.url=url;
					bn.title = title;
					bn.id = id;
					response.list.add(bn);
				}
				Element pageElement=xml.select("div.pagination").last();
				Element pages=pageElement.select("a").last();
				String page=pages.attr("onclick");
				page=page.substring(page.lastIndexOf("(")+1,page.lastIndexOf(")"));
				response.pageCount=page;
				/*Element pageElement=xml.select("td#fanye43575").last();
				String page=pageElement.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
				page=page.substring(page.lastIndexOf("/")+1);
				response.pageCount=page;*/
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