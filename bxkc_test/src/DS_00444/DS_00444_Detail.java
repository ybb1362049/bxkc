package DS_00444;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00444_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
				String content = null;
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if(divs != null){
					for(int i = 0 ; i < divs.getLength();i++){
						Element dive = (Element) divs.item(i);
						if("main-bd".equals(dive.getAttribute("class"))){
							Element h2e = (Element) dive.getElementsByTagName("h2").item(0);
							String title = h2e.getTextContent().trim();
							response.title = title;
							content = Utils.getNodeHtml(h2e);
						}
						if("line-height:25px; padding-left:43px; padding-right:43px;".equals(dive.getAttribute("style"))){
							NodeList div_list = dive.getElementsByTagName("div");
							div_list.item(div_list.getLength() - 1).setTextContent(null);
							div_list.item(div_list.getLength() - 2).setTextContent(null);
							content = content +  Utils.getNodeHtml(dive);
						}
					}
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}

