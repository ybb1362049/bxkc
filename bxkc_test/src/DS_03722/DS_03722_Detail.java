package DS_03722;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_03722_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content = null;
		String date;

		public String toString() {

			return "BranchNew [ content=" + content + "]";
		}
	}

	Pattern p = Pattern.compile("(\\d{4})(年|-)(\\d{1,2})(月|-)(\\d{1,2})");
	Matcher m;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
				Date nDate = new Date();

				org.w3c.dom.NodeList list = doc.getElementsByTagName("div");
				if (list != null) {
					for (int j = 0; j < list.getLength(); j++) {
						Element ele_aElement = (Element) list.item(j);
						if (ele_aElement.getAttribute("class").equals("frameReport")) {
							NodeList list1 = ele_aElement.getElementsByTagName("div");

							String text = ele_aElement.getTextContent().replaceAll("\n|\r|\\s+", "");
							m = p.matcher(text);
							if (m.find()) {
								String year = m.group(1);
								String month = m.group(3).length() == 2 ? m.group(3) : "0" + m.group(3);
								String day = m.group(5).length() == 2 ? m.group(5) : "0" + m.group(5);
								String date = year + "-" + month + "-" + day;
								Date pDate = sdf.parse(date);
								if (pDate.after(nDate)) {
									response.date = sdf.format(nDate);
								} else {
									response.date = date;
								}
							} else {
								response.date = sdf.format(nDate);
							}
							if (list != null) {
								for (int i = 0; i < list1.getLength(); i++) {
									Element ele_aElement1 = (Element) list.item(i);
									if (ele_aElement1.getAttribute("class").equals("reportTitle")
											|| ele_aElement1.getAttribute("class").equals("file")) {
										ele_aElement.removeChild((Node) ele_aElement1);
									}
								}
							}
							String pageCountString = Utils.getNodeHtml(ele_aElement).trim();
							response.content = pageCountString;
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return response;
	}

}
