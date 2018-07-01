package SJ_00108;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_00108_Detail extends TxtRspHandler{
	public class Response extends TxtBaseResponse{
		String content;
	}
	
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if(rspState == RspState.Login){
			try {
				originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
				String title="";
				String content="";
				Document xmlDoc=Jsoup.parse(originTxtRspContent);
				Elements as=xmlDoc.select("a");
				if(as!=null){
					for(Element a:as){
						String href=a.attr("href");
						if(!href.contains("http")){
							a.attr("href", "http://www.ccgp-guizhou.gov.cn"+href);
						}
					}
				}
				Elements imgs=xmlDoc.select("img");
				if(imgs!=null){
					for(Element img:imgs){
						String src=img.attr("src");
						if(!src.contains("http")){
							img.attr("src", "http://www.ccgp-guizhou.gov.cn"+src);
						}
					}
				}
				Element td=xmlDoc.select("h3").last();
				if(td!=null){
					title=td.outerHtml();
				}
				Element div=xmlDoc.select("div#content").first();
				if(div!=null){
					content=div.outerHtml();
				}else{
					div=xmlDoc.select("div.you div").get(2);
					content=div.outerHtml();
				}
				response.content=title+content;
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return response;
	}

}
