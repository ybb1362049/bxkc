package SJ_04010;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_04010_Detail extends TxtRspHandler {
	
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
				NodeList tit=xmlDoc.getElementsByTagName("div");
				String content="";
				for(int j=0;j<tit.getLength();j++){
					Element tits=(Element) tit.item(j);
					if(tits.getAttribute("class").equals("wzy1")){
						content=Utils.getNodeHtml(tits);
						String title=tits.getTextContent().trim();
						response.title=title;
					}
				}
				
				NodeList list=xmlDoc.getElementsByTagName("div");		
				if(list!=null){
					for(int i=0;i<list.getLength();i++){
						Element ele=(Element) list.item(i);		
						if("news_content".equals(ele.getAttribute("id"))){
							
							NodeList as=ele.getElementsByTagName("img");
							for(int k=0;k<as.getLength();k++){
								Element dw=(Element) as.item(k);		
									if( !dw.getAttribute("src").contains("javascript") && !"".equals(dw.getAttribute("src")) && !dw.getAttribute("src").contains("http")){
										String href=dw.getAttribute("src");
										
										href="http://www.xjslt.gov.cn"+href;							
										dw.setAttribute("src", href);
								
								}
								
							}
							NodeList ass=ele.getElementsByTagName("a");
							for(int k=0;k<ass.getLength();k++){
								Element dws=(Element) ass.item(k);
								
									
									if(!dws.getAttribute("href").contains("@") &&!"".equals(dws.getAttribute("href")) && !dws.getAttribute("href").contains("javascript") && !dws.getAttribute("href").contains("http")){
										String href=dws.getAttribute("href");
										
										href="http://www.xjslt.gov.cn"+href;							
										dws.setAttribute("href", href);
								   }else{
									   
								   }
								
							}
							content=content+Utils.getNodeHtml(ele).trim();	
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



