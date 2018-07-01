package DX000273;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000273_Detail extends TxtRspHandler{
	
	
	
	public class Response extends TxtBaseResponse{
			String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response =  new Response();
		try{
			Document doc =Jsoup.parse(arg1);
			String title =doc.select("span[class=font48]").html();
			
			String content=doc.select("table[width=95%]").html();
			Document doc1 = Jsoup.parse(content);
			Elements a_list =doc1.select("a[href]");
			for(Element a : a_list){
				String href =a.attr("href");
				if(href.endsWith(".doc")||href.endsWith(".rar")||href.endsWith(".zip")){
					a.attr("href","http://www.crpn.cn"+a.attr("href"));
				}
			}
			content =doc1.html();
			content=title+content;
			response.content=content;

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return response;
	}
	
	

	

}
