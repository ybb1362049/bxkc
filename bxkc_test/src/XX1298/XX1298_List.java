package XX1298;

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

public class XX1298_List extends TxtRspHandler {
	class BranchNew {
		String title;
		String id;
		String date;
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
				Elements titleList = doc.select("div.div_item");
				for(Element titleLi:titleList){
					BranchNew bn = new BranchNew();
					Element a = titleLi.select("a").first();
					String title = a.text().trim();
					String id = a.attr("href");
					String date = titleLi.select("div.other").first().text().trim();
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
					id = id.substring(id.indexOf("StId=")+"StId=".length());
					bn.title = title;
					bn.id = id;
					bn.date = date;
					response.list.add(bn);
				}
				Element pageEle = doc.select("div.myp2c_div_paging").last();
				Pattern pattern2 = Pattern.compile("/\\s*(\\d+)\\s*页");
				Matcher matcher2 = pattern2.matcher(pageEle.text());
				if(matcher2.find())
					response.pageCount = matcher2.group(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

}
