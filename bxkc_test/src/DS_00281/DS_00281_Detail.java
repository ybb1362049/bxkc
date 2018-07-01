package DS_00281;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_00281_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String title;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Document xmlDoc = Jsoup.parse(originTxtRspContent);
			Element divTitle = xmlDoc.select("div[style~=line-height:28px]").first();
			Element tableContent = xmlDoc.select("table[width=96%]").first();
			Elements imgList=tableContent.select("img");
			if(imgList!=null){
				for(Element img:imgList){
					String src=img.attr("src");
					src="http://www.scncggzy.com.cn"+src;
					img.attr("href", src);
				}
			}
			String title = divTitle.text().trim();
			String content = tableContent.outerHtml();
			response.title = title;
			response.content = content;
		}
		return response;
	}


}
