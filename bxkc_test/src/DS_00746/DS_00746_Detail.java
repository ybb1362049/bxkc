package DS_00746;



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00746_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
	//	String date;
		
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
				originTxtRspContent = "<div>" + originTxtRspContent
						+ "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);	
			
				Element dvv = xmlDoc.getElementById("TDContent");
		
				Element d22 = xmlDoc.getElementById("filedown");
				
				String att = "";
				if(d22 != null){
					NodeList als = d22.getElementsByTagName("a");
					if(als.getLength() > 0){
						for(int j = 0 ; j < als.getLength() ; j ++){
							Element aee = (Element)als.item(j);
							String str = "http://ggzy.huzhou.gov.cn/";
							String tmp = aee.getAttribute("href");
							tmp = tmp.replaceFirst("/", str);
							aee.setAttribute("href", tmp);
						}
						
						att = Utils.getNodeHtml(d22);
				
			}
				}
				
				
				
				String content = null;				
				content = Utils.getNodeHtml(dvv);
				response.content = content + att;
				
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
