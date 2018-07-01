package SJ_00081;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_00081_Detail extends TxtRspHandler{


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
				NodeList list=doc.getElementsByTagName("table");
				for (int i = 0; i < list.getLength(); i++) {
					Element element=(Element) list.item(i);
					if(element.getAttribute("width").equals("100%")&&element.getAttribute("cellspacing").equals("1"))
					{
						String pageCountString=Utils.getNodeHtml(element).trim();
						response.content=pageCountString;
						//System.out.println(response.content);
						
					}
				}
//						
//				Element  element=doc.getElementById("ztb_zbxx1");
//						String pageCountString=Utils.getNodeHtml(element).trim();
//						response.content=pageCountString;
//						//System.out.println(response.content);
//				
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}

		return response;
	}

}
