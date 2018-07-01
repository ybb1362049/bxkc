package DS_03793;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_03793_Detail extends TxtRspHandler{


	public class Response extends TxtBaseResponse {
		String content;
		String date;
		public String toString() {

			return "BranchNew [ content=" + content +"+date ="+date+" ]" ;
		}
	}
	
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if(rspState == RspState.Login){
			try{
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList divs = xmlDoc.getElementsByTagName("DIV");
				String contents =null;
				String content =null;
				if(divs !=null){
					for(int i=0;i<divs.getLength();i++){
						Element divss = (Element)divs.item(i);
						if("text-align:center".equals(divss.getAttribute("style"))){
							divss.getElementsByTagName("P").item(0).setTextContent(null);
							contents = Utils.getNodeHtml(divss);
							
						}
						Element iframe = xmlDoc.getElementById("tempContent");
						String iframes = iframe.getTextContent().trim().replace("textarea ", "div");
						
						response.content = contents+iframes;
						
					}
				}
				
				
				
				} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return response;
	}

}


