package GJ03760;
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

public class GJ03760_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {

		String title;
		String id;
		String date;
		String province;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();	
//		originTxtRspContent=originTxtRspContent.toLowerCase();
		if (rspState == RspState.Login) {
			try  {
				Document document = Jsoup.parse(originTxtRspContent);
				Element listUlEle = document.select("ul.as-pager-body").first();
				Elements itemLiEles = listUlEle.select("li");
				for (Element itemLiEle : itemLiEles) {
					String province="";
					Element titleAEle = itemLiEle.select("a.as-pager-item").first();
					Element titleH5Ele = titleAEle.select("h5.as-p-tit").first();
					Element titleSpanEle = titleH5Ele.select("span.txt").first();
					Element dateSpanEle = titleH5Ele.select("span.time").first();
					Element adSpanEle = itemLiEle.select("span.tag-attr").get(1);
					String temp=adSpanEle.select("strong").first().text().trim();
					if (temp.startsWith("广西壮族自治区")) {
						province = "广西";
					} else if (temp.startsWith("新疆维吾尔自治区")) {
						province = "新疆";
					} else if (temp.startsWith("内蒙古自治区")) {
						province = "内蒙古";
					} else if (temp.startsWith("西藏自治区")) {
						province = "西藏";
					} else if (temp.startsWith("宁夏回族自治区")) {
						province = "宁夏";
					} else {
						if (temp.contains("省")) {
							province = temp.substring(0, temp.indexOf("省"));
						} else if (temp.contains("市")) {
							province = temp.substring(0, temp.indexOf("市"));
						} else {
							province = temp;
						}
					}
					
					String title = titleSpanEle.attr("title").trim().replaceAll("(...|···)$", "");
					String href = titleAEle.attr("href").trim();
					String id = href.substring(href.lastIndexOf("/") + "/".length());
					String date = dateSpanEle.text().replaceAll(".*?(\\d{4}-\\d{2}-\\d{2}).*", "$1").trim();
					
					BranchNew branchNew = new BranchNew();
					branchNew.title = title;
					branchNew.id = id;
					branchNew.date = date;
					branchNew.province=province;
					response.list.add(branchNew);
				}
				
				Element pageFormEle = document.select("#pagerSubmitForm").first();
				Element pageAEle = pageFormEle.select("a[href=#]").last().nextElementSibling();
				response.pageCount = pageAEle.text().trim();
				
				/*String con=Utils.callApi("http://www.daqobid.com/custom/News/ViewNews.aspx?id=76438");
				response.pageCount=con;*/
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