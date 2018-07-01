package DS_00858;

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

public class DS_00858_List extends TxtRspHandler {

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
				NodeList uls = xmlDoc.getElementsByTagName("ul");
				if(uls !=null){
					for(int i=0;i<uls.getLength();i++){
						Element ulss = (Element)uls.item(i);
						if("sec_list clearfix".equals(ulss.getAttribute("class"))){
							NodeList lis = ulss.getElementsByTagName("li");
							if(lis !=null){
								for(int j=0;j<lis.getLength();j++){
									Element liss = (Element)lis.item(j);
									BranchNew bn = new BranchNew();
									Element alist = (Element)liss.getElementsByTagName("a").item(0);
									String id = alist.getAttribute("href");
									String title = alist.getAttribute("title");
									Element spans = (Element)liss.getElementsByTagName("span").item(1);
									String date = spans.getTextContent().trim();
									date = date.substring(date.indexOf("[")+1,date.indexOf("]"));
									bn.id = id;
									bn.title = title;
									bn.date  = date;
									response.list.add(bn);
								}
							}
						}
					}
				}
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if(divs !=null){
					for(int k=0;k<divs.getLength();k++){
						Element divss = (Element)divs.item(k);
						if("page_r".equals(divss.getAttribute("class"))){
							String pages = divss.getTextContent().trim();
							pages = pages.substring(pages.indexOf("/")+1);
							response.pageCount = pages;
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