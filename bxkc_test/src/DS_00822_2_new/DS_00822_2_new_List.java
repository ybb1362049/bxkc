package DS_00822_2_new;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;


public class DS_00822_2_new_List extends TxtRspHandler{
	private static final Pattern PATTERN1=Pattern.compile("bulletinId=(?<value>.*?)\"",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern PATTERN2=Pattern.compile("_blank\">(?<value>.*?)</a>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL); 
//	private static final Pattern PATTERN3=Pattern.compile("middle\"><span\\s+title='(?<value>.*?)'>",
//			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public class BranchNew{
		String id;
		String title;
		String date;
		
		@Override 
		public String toString(){
			return "JiangXi[id="+id+",title="+title+",date="+date+"]";
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
				
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				//System.out.println(originTxtRspContent);
				
				NodeList table_list=xmlDoc.getElementsByTagName("ul");
				for(int i=0;i<table_list.getLength();i++){
					Element table_eles=(Element)table_list.item(i);
					if("content-list clearfix".equals(table_eles.getAttribute("class"))){
						        NodeList table_el=table_eles.getElementsByTagName("li");
						        for(int j=0;j<table_el.getLength();j++){
								Element table_ele=(Element) table_el.item(j);
								Element a_ele=(Element)table_ele.getElementsByTagName("a").item(0);	
								Element tit=(Element)table_ele.getElementsByTagName("span").item(0);
								String title=tit.getTextContent();
								title=title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
								String href=a_ele.getAttribute("href");
								href=href.substring(href.indexOf("?")+1);
								
								Element dat=(Element) table_ele.getElementsByTagName("span").item(1);
								String date=dat.getTextContent().trim();
								//date=date.substring(1,11);
								//date=date.replaceAll("/", "-").replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
								//date=date.substring(date.indexOf("[")+1,date.length()-1);
								BranchNew branchNew=new BranchNew();
								branchNew.title=title;						
								branchNew.date=date;
								branchNew.id=href;
								
								response.list.add(branchNew);
								//System.out.println(branchNew.toString());
						        }
								
					}		
					}
			}catch(Exception e){e.printStackTrace();}
		}
		return response;
	}



}
