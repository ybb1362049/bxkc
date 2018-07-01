package DX000105;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000105_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;	
		String title;
		String date;
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
				String title = "";
				title = xmlDoc.getElementsByTagName("h2").item(0).getTextContent().trim();
				response.title = title;		
				Element boElement = (Element) xmlDoc.getElementsByTagName("pre").item(0);
				String body = Utils.getNodeHtml(boElement);
				response.content = title + "\r\n" + body;
//				//System.out.println(response.content);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}

