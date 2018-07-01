package GJ_04000;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class GJ_04000_Detail extends TxtRspHandler {
	
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
				String content=null;
				NodeList list=xmlDoc.getElementsByTagName("td");		
				if(list!=null){
					for(int i=0;i<list.getLength();i++){
						Element ele=(Element) list.item(i);		
						if("DarkTableBorder".equals(ele.getAttribute("class"))){
							
							Element divre=(Element) ele.getElementsByTagName("table").item(1);
							divre.setTextContent("");
							
							NodeList as=ele.getElementsByTagName("img");
							for(int k=0;k<as.getLength();k++){
								Element dw=(Element) as.item(k);		
									if( !dw.getAttribute("src").contains("javascript") && !"".equals(dw.getAttribute("src")) && !dw.getAttribute("src").contains("http")){
										String href=dw.getAttribute("src");
										
										href="http://cg.cdb.com.cn"+href;							
										dw.setAttribute("src", href);
								
								}
								
							}
							NodeList ass=ele.getElementsByTagName("a");
							for(int k=0;k<ass.getLength();k++){
								Element dws=(Element) ass.item(k);
								
									
									if(!dws.getAttribute("href").contains("@") &&!"".equals(dws.getAttribute("href")) && !dws.getAttribute("href").contains("javascript") && !dws.getAttribute("href").contains("http")){
										String href=dws.getAttribute("href");
										
										href="http://cg.cdb.com.cn"+href;							
										dws.setAttribute("href", href);
								   }else{
									   
								   }
								
							}
							content=Utils.getNodeHtml(ele).trim();	
						}				
					}
				}

				response.content =content;//+content2;
		       // System.out.println(response.content);
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}



