package SJ_04014;

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

public class SJ_04014_List extends TxtRspHandler {

	private static String regex = "Key_Index=()";
	private static Pattern p = Pattern.compile(regex);
	private static Matcher m;
	private static int count=1;
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String nextPage;
		String currentPage;
		
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
				NodeList table_list = xmlDoc.getElementsByTagName("div");
				// 循环判断目的table
				for (int k = 0; k < table_list.getLength(); k++) {

					Element table_point = (Element) table_list.item(k);
					// 通过class判断目的table
					if (table_point.getAttribute("class").equals(
							"custom_con")) {

						// 获取目的table下的tr
						NodeList tr_list = table_point
								.getElementsByTagName("li");
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
							String id = href.substring(href.indexOf("?") + 1);
							// 获取a标签的标题
							String title = a.getTextContent();
							title = title.replaceAll("\\s*", "");
							// 获取目标td下的内容
							Element a_date = (Element) td_detail
									.getElementsByTagName("span").item(0);

							String date = a_date.getTextContent();
							date = date.replaceAll("\\s*", "");
							// String
							// date=date_t.substring(0,date_t.indexOf(" "));
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
				NodeList spanList=xmlDoc.getElementsByTagName("span");
				if(spanList!=null)
				{
					for(int k=0;k<spanList.getLength();k++)
					{
						Element span=(Element) spanList.item(k);
						if("focus".equals(span.getAttribute("class")))
						{
							String page=span.getTextContent().trim();
							count=Integer.parseInt(page);
						}
					}
				}
				Element pp=xmlDoc.getElementById("ctl00");
				String action=pp.getAttribute("action");
				action=action.substring(action.indexOf("/")+1,action.lastIndexOf(".html"));
				Element div=xmlDoc.getElementById("fenye");
				NodeList aList=div.getElementsByTagName("a");
				if(aList.getLength()>1)
				{
					Element a=(Element) aList.item(count);
					if(a!=null)
					{
						String href=a.getAttribute("href");
						String id=href.substring(href.indexOf("=")+1);
						//System.out.println(id);
						String url=super.getNewPathPrefix()+"/?"+super.getAdditionalLinkParamStr()+"&iw-cmd=SJ_04014_List&iw_ir_2="+action;
						url+="&Key_Index="+id;
						response.nextPage=url;
						response.currentPage=count+"";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}