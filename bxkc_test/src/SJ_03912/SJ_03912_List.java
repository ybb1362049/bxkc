package SJ_03912;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_03912_List extends TxtRspHandler {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";]";
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
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				NodeList ul_list = (NodeList)xmlDoc.getElementsByTagName("ul");
				if(ul_list!=null){
					for(int i = 2;i<ul_list.getLength();i++){
						Element ul_ee = (Element)ul_list.item(i);
						if("list-t".equals(ul_ee.getAttribute("class"))){
							NodeList li_list = (NodeList)ul_ee.getElementsByTagName("li");
							if(li_list!=null){
								for(int j = 1;j<li_list.getLength();j++){
									Element li_ee = (Element)li_list.item(j);
									Element a_ee = (Element)li_ee.getElementsByTagName("a").item(0);
									String href = a_ee.getAttribute("href");
									String id = href.substring(href.indexOf("/")+1,href.length());
									//System.out.println(id);
									String title = a_ee.getAttribute("title");
									//System.out.println(title);
									Element span_ee = (Element)li_ee.getElementsByTagName("span").item(0);
									String date = span_ee.getTextContent().trim();
									date = sdf.format(sdf.parse(date));
									BranchNew bn = new BranchNew();
									bn.id = id;
									bn.title = title;
									bn.date = date;
									response.list.add(bn);
									//System.out.println(bn);
								}
							}
						}
					}
				}
				
			 NodeList div_list = (NodeList)xmlDoc.getElementsByTagName("div");
			 if(div_list!=null){
				 for(int k = 0;k<div_list.getLength();k++){
					 Element div_ee = (Element)div_list.item(k);
					 if("pages".equals(div_ee.getAttribute("class"))){
						 NodeList a_list = (NodeList)div_ee.getElementsByTagName("a");
						 if(a_list!=null){
							 Element a_ee = (Element)a_list.item(2);
							 String page = a_ee.getTextContent().trim();
							 page = page.substring(page.indexOf("/")+1, page.length());
							 response.pageCount = page;
							 //System.out.println(response.pageCount);
						 }
					 }
				 }
			 }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
}

