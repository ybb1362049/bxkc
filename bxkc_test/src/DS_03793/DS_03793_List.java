package DS_03793;

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

public class DS_03793_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String id;
		String title;
		String date;

		@Override
		public String toString() {

			return "BranchNew [ title=" + title + ", date=" + date + ",id="
					+ id + "]" + "\n";
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
				NodeList tables = xmlDoc.getElementsByTagName("table");
				if(tables !=null){
					for(int k=0;k<tables.getLength();k++){
						Element tabless = (Element) tables.item(k);
						if("info".equals(tabless.getAttribute("id"))){
							NodeList trs = tabless.getElementsByTagName("TR");
							if(trs !=null){
								for(int i=0;i<trs.getLength();i++){
									BranchNew bn = new BranchNew();
									Element trss = (Element)trs.item(i);
									Element alist = (Element)trss.getElementsByTagName("A").item(0);
									String href = alist.getAttribute("href");
									String id = href.replace("?", "&");
									String title = alist.getTextContent().trim();
									Element dates = (Element)trss.getElementsByTagName("TD").item(1);
									String date = dates.getTextContent().trim();
									bn.date = date;
									bn.title = title;
									bn.id  = id;
									response.list.add(bn);
								}
							}
						}
					}
					
						}
						
			
//				//System.out.println(response.list);
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if(divs !=null){
					for(int j=0;j<divs.getLength();j++){
						Element divss = (Element)divs.item(j);
						if("text-align:center".equals(divss.getAttribute("style"))){
							NodeList aa = divss.getElementsByTagName("A");
							if(aa !=null){
								for(int a=0;a<aa.getLength();a++){
									Element page = (Element)divss.getElementsByTagName("A").item(a);
									String pages = page.getAttribute("href");
									pages = pages.substring(pages.lastIndexOf("=")+1);
									response.pageCount = pages;
									//System.out.println(response.pageCount);
								}
								
							}
							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// //System.out.println(response.list.toString());
		}
		return response;
	}


}