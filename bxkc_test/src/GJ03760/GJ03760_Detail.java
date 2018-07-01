package GJ03760;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class GJ03760_Detail extends TxtRspHandler{


	public class Response extends TxtBaseResponse {
		String content = null;

		public String toString() {

			return "BranchNew [ content=" + content +"]" ;
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
				Document doc = Utils.getDocByContent(originTxtRspContent);
//				System.out.println(originTxtRspContent);
				NodeList nodeList=doc.getElementsByTagName("div");
				for(int i=0;i<nodeList.getLength();i++)
				{
					Element  element =(Element) nodeList.item(i);
					if(element.getAttribute("class").equals("as-article-body table-article"))
					{
						String contentString=Utils.getNodeHtml(element);
						response.content=contentString;
//						System.out.println(contentString);
					}
					
						
				}
//				response.content=originTxtRspContent;
				
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}

		return response;
	}

}
