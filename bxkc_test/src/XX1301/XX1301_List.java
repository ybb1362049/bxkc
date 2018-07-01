package XX1301;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class XX1301_List extends TxtRspHandler {
	class BranchNew {
		String title;
		String id;
		String date;
		String url;
	}
	class Response extends TxtBaseResponse{
		List <BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}
	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState repState, String originTxtRspContent) {
		Response response = new Response();
		try {
			Pattern pattern = Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})");
			Matcher matcher ;
			if(repState.equals(RspState.Login)){
				Document doc = Jsoup.parse(originTxtRspContent);
				Element titleEle = doc.select("ul.list.lh24.f14").first();
				Elements titleList = titleEle.select("li:has(a)");
				for(Element titleLi:titleList){
					BranchNew bn = new BranchNew();
					Element a = titleLi.select("a").first();
					String title = a.text().trim();
					String id = a.attr("href");
					String date = titleLi.select("span.rt").first().text().trim();
					matcher = pattern.matcher(date);
					if(matcher.find()){
						String year = matcher.group(1);
						String month = matcher.group(2);
						String day = matcher.group(3);
						if(month.length() == 1)
							month = "0" + month;
						if(day.length() == 1)
							day = "0" + day;
						date = year + "-" + month + "-" + day;
					}
					if(id.startsWith("/")||id.contains("ncxq.swpu.edu.cn")){
						id = id.substring(id.indexOf("&id="));
						bn.url = super.getNewPathPrefix() + "/?"
								+ super.getAdditionalLinkParamStr()
								+ "&iw-cmd=XX1301_Detail2"+id;
					}
					else if(id.contains("www.swpu.edu.cn")&&id.contains("wbnewsid")){
						id = id.substring(id.indexOf("wbnewsid="));
						bn.url = super.getNewPathPrefix() + "/?"
								+ super.getAdditionalLinkParamStr()
								+ "&iw-cmd=XX1301_Detail1&"+id;
					}
					else{
						continue;
					}
					bn.title = title.replaceAll("【.*?】", "");
					bn.id = id;
					bn.date = date;
					response.list.add(bn);
				}
				Element pageEle = doc.select("#pages :contains(下一页)").last().previousElementSibling();
				if(pageEle != null)
					response.pageCount = pageEle.text().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

}
