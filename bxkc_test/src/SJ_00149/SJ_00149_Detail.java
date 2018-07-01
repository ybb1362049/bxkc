package SJ_00149;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class SJ_00149_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content = null;

		public String toString() {

			return "BranchNew [ content=" + content + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document doc = Utils.getDocByContent(originTxtRspContent);
				String content = "";
				NodeList list = doc.getElementsByTagName("div");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					if (element.getAttribute("class").equals("intr_title")) {
						content = Utils.getNodeHtml(element);
					}
					if (element.getAttribute("id").equals("p8_content_show")) {
						NodeList list2 = element.getElementsByTagName("img");
						for (int j = 0; j < list2.getLength(); j++) {
							Element img = (Element) list2.item(j);
							String src = img.getAttribute("src");
							if (src.startsWith("/")) {
								src = "http://www.gsei.com.cn" + src;
								img.setAttribute("src", src);
							}
						}
						content += Utils.getNodeHtml(element);
					}
				}
				response.content = content;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return response;
	}

}
