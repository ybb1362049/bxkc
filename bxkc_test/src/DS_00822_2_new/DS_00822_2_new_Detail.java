package DS_00822_2_new;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00822_2_new_Detail extends TxtRspHandler{

	public class Response extends TxtBaseResponse{
		String date;
		String content;
	}
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent){
		return RspState.Login;
	}
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspstate,String originTxtRspContent){
		Response response=new Response();
		
		if(rspstate==RspState.Login){
			try{
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				
						Element  td_element=xmlDoc.getElementById("mainContent");
						
						NodeList as=td_element.getElementsByTagName("a");
						for(int k=0;k<as.getLength();k++){
							Element dw=(Element) as.item(k);
							
								
								if( !dw.getAttribute("href").contains("javascript") && !"".equals(dw.getAttribute("href")) && !dw.getAttribute("href").contains("http") ){
									String href=dw.getAttribute("href");
									
									href="http://www.npggzy.gov.cn"+href;							
									dw.setAttribute("href", href);
								}
						
				       }
				
						String content=Utils.getNodeHtml(td_element);
						response.content = content ;

					
				}
				catch(Exception e){e.printStackTrace();}
		}
		return response;
	}


}
