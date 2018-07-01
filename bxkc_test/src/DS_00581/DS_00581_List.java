package DS_00581;

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


public class DS_00581_List extends TxtRspHandler{

	public static Pattern pageInfoPattern=Pattern.compile("共计(?<totalPage>\\d+)条");
	public static Matcher pageInfoMatcher;
	
	public class BranchNew{
		String title;
		String id;
		String date;
		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}
	public class Response extends TxtBaseResponse{
		List<BranchNew> list=new ArrayList<BranchNew>();
		String pageCount;
	}
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent){
	return RspState.Login; 
	}
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspstate,String originTxtRspContent)
	{
		Response response=new Response();
		if(rspstate==RspState.Login)
		{
			try{  
				Document document = Jsoup.parse(originTxtRspContent);
				Element tableEle=document.select("div[class=box-text-list]").first();
				Elements tableListEle=tableEle.select("li");
				for(Element listEle:tableListEle){
					Element titleEle=listEle.select("a[href]").first();
					Element dateEle=listEle.select("span[class=btl-time fr]").first();
					
					String title=titleEle.text();
					String date=dateEle.text();
					String href=titleEle.attr("href");
					String id=href.substring(href.indexOf("Content/")+"Content/".length());
					
					BranchNew branchNew=new BranchNew();
					branchNew.title=title;
					branchNew.id=id;
					branchNew.date=date;
					response.list.add(branchNew);
					}
				Element pageInfoEle=document.select("div[class=pager]").first();
				String pageInfo=pageInfoEle.text();
				pageInfoMatcher=pageInfoPattern.matcher(pageInfo);
				if(pageInfoMatcher.find()){
					int pageCount=Integer.parseInt(pageInfoMatcher.group("totalPage"));
					response.pageCount=((pageCount/20)+1)+"";
				}
				}catch(Exception e){
				e.printStackTrace();
			}
		}
		return response;
	}
}

