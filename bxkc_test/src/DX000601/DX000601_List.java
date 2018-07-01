package DX000601;

import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
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

public class DX000601_List extends TxtRspHandler {

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
		String content;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + ";]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	public static String getMD5(String str) {
		String md5Str = null;
	    try {
	        // 生成一个MD5加密计算摘要
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        // 计算md5函数
	        md.update(str.getBytes());
	        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
	        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
	        md5Str = new BigInteger(1, md.digest()).toString(16);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return md5Str;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xml = Jsoup.parse(originTxtRspContent);
				Element boxElement = xml.select("#aptList > tbody").first();
				if (boxElement.select("a").first() != null) {
					Elements listElement = boxElement.select("tr:has(a)");
					for (Element list : listElement) {
						BranchNew bn = new BranchNew();
						Element hrefElement = list.select("a").first();
						Element dateElement = list.select("td:nth-child(2) > p").first();
						String dateString = dateElement.text().replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
						dateString = sdf.format(sdf.parse(dateString));
						String id = hrefElement.attr("href");
						id = id.substring(id.indexOf("?") + 1);
						String title = hrefElement.attr("title").replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						bn.title = title;
						bn.id = id;
						bn.date = dateString;
						response.list.add(bn);
					}
				} else {
					Elements listElement = boxElement.select("tr");
					for (Element list : listElement) {
						BranchNew bn = new BranchNew();
						Element hrefElement = list.select("p.col_txt").first();
						String title = hrefElement.text().replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						hrefElement.remove();
						Element content = list.select("p.col_txt").first();
						String contents = content.text().trim();
						Element dateElement = list.select("p.col_txt").last();
						String dateString = dateElement.text().replaceAll(
								"[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
						dateString = sdf.format(sdf.parse(dateString));
						String id = getMD5(title+dateString);
						bn.title = title;
						bn.id = id;
						bn.content = "<h1>" + title + "</h1>" + "<p>" + contents + "</p>";
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