package DX000283;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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

public class DX000283_List3 extends TxtRspHandler {

	private static String regex = "page=(\\d+)";
	private static Pattern p = Pattern.compile(regex);
	private static Matcher m;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {

		String title;
		String id;
		String date;

		@Override
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date
					+ ";]";
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
		if (rspState == RspState.Login) {
			try {
				// 获取整个dom树
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				// 获取所有table
				NodeList table_list = xmlDoc.getElementsByTagName("table");
				// 循环判断目的table
				for (int k = 0; k < table_list.getLength(); k++) {

					Element table_point = (Element) table_list.item(k);
					// 通过class判断目的table
					if (table_point.getAttribute("class").equals(
							"items")) {
						Element tbody=(Element) table_point.getElementsByTagName("tbody").item(1);
						// 获取目的table下的tr
						NodeList tr_list = tbody
								.getElementsByTagName("tr");
						// 循环获取目的tr
						for (int i = 0; i < tr_list.getLength(); i++) {
							// 取得目标tr
							Element td_detail = (Element) tr_list.item(i);
							// 获取目标tr下的a标签
							Element a = (Element) td_detail
									.getElementsByTagName("a").item(0);
							// 获取a标签的属性href
							String href = a.getAttribute("href");
							// String
							// str=super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
							// str=str.substring(str.indexOf("gov.cn")+6);
							// String
							// id=href.substring(href.lastIndexOf("/")+1);
							String id = href;
							// 获取a标签的标题
							String title = td_detail
									.getElementsByTagName("td").item(1).getTextContent();
							title = title.replaceAll("\\s*", "");
							// 获取目标td下的内容
							Element a_date = (Element) td_detail
									.getElementsByTagName("td").item(2);

							String date = a_date.getTextContent();
							date = date.replaceAll("\\s*", "");
							// String
							 date=date.substring(0,10);
							// date=date.replace("/", "-");
							BranchNew branchnew = new BranchNew();
							// 存储数据
							branchnew.id = id;
							branchnew.title = title;
							branchnew.date = date;
							response.list.add(branchnew);
						//	System.out.println(branchnew);
						}

					}

				}
				NodeList divList=xmlDoc.getElementsByTagName("li");
				if(divList!=null)
				{
					for(int i=0;i<divList.getLength();i++)
					{
						Element div=(Element) divList.item(i);
						if("last".equals(div.getAttribute("class")))
						{
							String con=Utils.getNodeHtml(div);
							m=p.matcher(con);
							if(m.find())
							{
								String page=m.group(1);
								response.pageCount=page;
							//	System.out.println(page);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}