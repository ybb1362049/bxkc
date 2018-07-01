package SJ_00171;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;


	public class SJ_00171_9_Detail extends TxtRspHandler {


	public class Response extends TxtBaseResponse {
		String content;

	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		try {
			Document doc = Jsoup.parse(arg1);			
			doc.select("script").remove();
			if(doc.select("div.PageTextInfo").size()>0){
				String title   =doc.select("div.PageList > h1").html();
				String content =doc.select("div.PageTextInfo").html();
				response.content="<div>"+title+"</div>"+content;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return response;
}	

}