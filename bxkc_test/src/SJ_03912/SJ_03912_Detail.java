package SJ_03912;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_03912_Detail extends TxtRspHandler {
	
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
				originTxtRspContent = "<td>" + originTxtRspContent+ "</td>";
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				NodeList h2_list = (NodeList)xmlDoc.getElementsByTagName("h2");
				String  title = null;
				if(h2_list!=null){
					Element h2_ee = (Element)h2_list.item(0);
					title = Utils.getNodeHtml(h2_ee);
				}
				
				NodeList div_list = (NodeList)xmlDoc.getElementsByTagName("div");
				if(div_list!=null){
					for(int i = 0;i<div_list.getLength();i++){
						Element div_ee = (Element)div_list.item(i);
						if("article-box".equals(div_ee.getAttribute("class"))){
							String content = Utils.getNodeHtml(div_ee);
							content = title + content;
							response.content = content;
							//System.out.println(response.content);
						}      
					}
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}



