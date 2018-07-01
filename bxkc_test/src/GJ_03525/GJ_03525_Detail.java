package GJ_03525;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;



public class GJ_03525_Detail extends TxtRspHandler {
	
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
				originTxtRspContent = "<div>" + originTxtRspContent
						+ "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				Element td_ele=xmlDoc.getElementById("TDContent");
				String content = Utils.getNodeHtml(td_ele).trim();
				
		//		Element tr_ele=xmlDoc.getElementById("trAttach");
				/*NodeList a_ele=tr_ele.getElementsByTagName("a");{
							for(int j = 0; j< a_ele.getLength(); j++){	
							Element a_ele1=(Element)a_ele.item(j);	
							String href=a_ele1.getAttribute("href");
							href = href.replaceFirst(
									"/", "http://60.172.160.41/Front_jyzx/" );
							a_ele1.setAttribute("href", href);
							
							}
							
						}*/
				
							
	//			content+=Utils.getNodeHtml(tr_ele).trim();
				
		//	 //System.out.println(content);
		       response.content=content;
					
			 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}

