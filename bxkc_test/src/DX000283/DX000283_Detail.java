package DX000283;

import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000283_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	Pattern p = Pattern.compile("(\\d{4})(年||-)(\\d{1,2})(月||-)(\\d{1,2})");

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
				// 获取整个dom树
				// System.out.println(originTxtRspContent);
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				// 获取所有td
				NodeList td_list = xmlDoc.getElementsByTagName("div");
				// 循环所有的td
				String content = "";
				for (int i = 0; i < td_list.getLength(); i++) {
					// 获取每个td
					Element td_point = (Element) td_list.item(i);
					// 判断目的td
					/*
					 * if
					 * ("container fn-clear".equals(td_point.getAttribute("class"
					 * ))) { Element h2 = (Element)
					 * td_point.getElementsByTagName( "h2").item(0);
					 * response.title=h2.getTextContent().trim(); content =
					 * Utils.getNodeHtml(h2); }
					 */
					// 将整个td的内容存放到字符串
					if ("m_main".equals(td_point.getAttribute("class"))
							|| "m_main_".equals(td_point.getAttribute("class"))) {
						Element h1 = (Element) td_point.getElementsByTagName(
								"h1").item(0);
						if (h1 != null) {
							response.title = h1.getTextContent().trim();
						}
						NodeList IMG_list = td_point
								.getElementsByTagName("img");
						if (IMG_list != null) {
							for (int j = 0; j < IMG_list.getLength(); j++) {
								Element IMG_ele = (Element) IMG_list.item(j);
								String src = IMG_ele.getAttribute("src");
								if (src.indexOf("http") == -1) {
									src = "http://biz.shengquan.com" + src;
								}
								IMG_ele.setAttribute("src", src);
							}
						}

						NodeList a_list = td_point.getElementsByTagName("a");
						if (a_list != null) {
							for (int j = 0; j < a_list.getLength(); j++) {
								Element a_ele = (Element) a_list.item(j);
								String href = a_ele.getAttribute("href");
								if (href.indexOf("www") == -1
										&& href.indexOf("@") == -1
										&& href.indexOf("http") == -1) {

									href = "http://biz.shengquan.com" + href;
								}
								a_ele.setAttribute("href", href);
							}
						}
						content += Utils.getNodeHtml(td_point);

					}

				}
				// System.out.println(content);
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
