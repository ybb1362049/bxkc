package DX000263;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000263_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
		
		
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
				String content="";
				Element list=(Element) xmlDoc.getElementsByTagName("table").item(0);		
				
							NodeList as=list.getElementsByTagName("img");
							for(int k=0;k<as.getLength();k++){
								Element dw=(Element) as.item(k);		
									if( !dw.getAttribute("src").contains("javascript") && !"".equals(dw.getAttribute("src")) && !dw.getAttribute("src").contains("http")){
										String href=dw.getAttribute("src");
										
										href="http://srm.crland.com.cn"+href;							
										dw.setAttribute("src", href);
								
								}
								
							}
							NodeList ass=list.getElementsByTagName("a");
							for(int k=0;k<ass.getLength();k++){
								Element dws=(Element) ass.item(k);
								
									
									if(!dws.getAttribute("href").contains("@") &&!"".equals(dws.getAttribute("href")) && !dws.getAttribute("href").contains("javascript") && !dws.getAttribute("href").contains("http")){
										String href=dws.getAttribute("href");
										
										href="http://srm.crland.com.cn"+href;							
										dws.setAttribute("href", href);
								   }else{
									   
								   }
								
							}
							content=content+Utils.getNodeHtml(list).trim();	
				
				response.content =content;//+content2;

			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}



