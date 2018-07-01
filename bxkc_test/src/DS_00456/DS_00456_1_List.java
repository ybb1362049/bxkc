package DS_00456;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.Utils;

public class DS_00456_1_List extends TxtRspHandler {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
 
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
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date
					+ ";]";
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
		if (rspState == RspState.Login) {
			try {
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList div_list = xmlDoc.getElementsByTagName("div");
				if (div_list != null) {
					for (int i = 0; i < div_list.getLength(); i++) {
						Element div_ele = (Element) div_list.item(i);
						if ("box04_con".equals(div_ele.getAttribute("class"))) {
							NodeList li_list = div_ele.getElementsByTagName("li");
							if(li_list!=null){
								for(int j = 0;j<li_list.getLength();j++){
									Element li_ee = (Element)li_list.item(j);
									Element a = (Element) li_ee.getElementsByTagName("a").item(0);								
									String id = a.getAttribute("href");
									id = id.substring(5,id.indexOf("."));
									String title = a.getAttribute("title").trim();
									Element span = (Element)li_ee.getElementsByTagName("span").item(0);
									String date = span.getTextContent().trim();
									if(date.contains("/")){
										date=date.replace("/", "-");
									}
									Pattern p = Pattern.compile("\\d+{4}-\\d{1,2}-\\d{1,2}");
									
									Matcher m;
									m = p.matcher(date);
									if (m.find()) {
										date = m.group();
									}
									date = sdf.format(sdf.parse(date));
									
									BranchNew bn = new BranchNew();
									String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
									+ "&iw-cmd=DS_00456_1_Detail&iw_ir_2=" + id;
									bn.url=url;
									bn.id = id;
									bn.title = title;
									bn.date = date;
									response.list.add(bn);
								}
							}
						}
						
						if("pager".equals(div_ele.getAttribute("id"))){
							NodeList span_List = div_ele.getElementsByTagName("span");
							Element span_ee = (Element) span_List.item(0);
							String page = span_ee.getTextContent().trim();
							page = page.replaceAll("[\u4e00-\u9fa5]+", "");
							double pagecount = Math.round((double)(Integer.parseInt(page)/40));
							response.pageCount = pagecount+" ";
						}
					}
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;
}