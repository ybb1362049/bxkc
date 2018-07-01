package GJ_03524;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class GJ_03524_List extends TxtRspHandler {

	// 定义日期格式化对象
	private static SimpleDateFormat sourceSdf = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	// 构建请求
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	// 构建bean对象
	public class BranchNew {
		String title; // 标题
		String id;    // 标识ID
		String date;  // 日期

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login; // 默认成功登录状态
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				// 处理文件内容
				// 转换为DOM节点
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				// 根据网页标签实际情况获取标签 字符串
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if (divs != null) {                                             // NodeList空判断
					for (int i = 0; i < divs.getLength(); i++) {
						Element divlee = (Element) divs.item(i);
						if ("cfcpn_list_content text-left".equals(divlee.getAttribute("class"))) { // 选择第n个符合条件的divlee
							        Element a_ele = (Element) divlee.getElementsByTagName("a").item(0);    // 定位标签
									// 获取标识（id）
									String id = a_ele.getAttribute("href");
									id = id.substring(id.lastIndexOf("/")+1).trim();
									// 获取标题（title）
									String title = a_ele.getTextContent().trim();
									// 获取日期（date）
									String date = divlee.getElementsByTagName("p").item(2).getTextContent().trim();
									date=date.substring(date.indexOf("发布时间：")+"发布时间：".length());
									date=date.substring(0,10).trim();
//									Date date_D = sourceSdf.parse(date); // 转换为Date类型
//									date = sdf.format(date_D); // 转换成yyyy-MM-dd格式
									// 构造BranchNew对象
									BranchNew branchNew = new BranchNew();
									branchNew.title = title;
									branchNew.date = date;
									branchNew.id = id;
									response.list.add(branchNew);
									// 测试输出
//									System.out.println(branchNew.toString());
									 
						}
					}
				}
				// 获取总页数
				NodeList uls = xmlDoc.getElementsByTagName("ul");
				Element ule = null;
				if(uls.getLength() > 0){
					for(int i = 0 ; i < uls.getLength() ; i ++){
						ule = (Element)uls.item(i);
						if("pagination".equals(ule.getAttribute("class")))break;
					}
				}
				NodeList lis = ule.getElementsByTagName("li");
				Element lse = null;
				String sumpg = null;
				String pageCount = null;
				if(lis.getLength() > 0){
					for( int k = 0 ; k < lis.getLength() ; k ++){
						lse = (Element)lis.item(k);					
						if("page active".equals(lse.getAttribute("class"))){
							NodeList ass = lse.getElementsByTagName("a");
							pageCount = ((Element)ass.item(0)).getTextContent();
						}
					}
				}
				lse = (Element)lis.item(lis.getLength() -2);
				NodeList ass = lse.getElementsByTagName("a");
				sumpg = ((Element)ass.item(0)).getTextContent();
				int p = Integer.parseInt(sumpg);
				int q = Integer.parseInt(pageCount);
				if(p > q){
					pageCount = sumpg;
				}
					response.pageCount = pageCount;
			// 测试输出总页数
			if (response.pageCount == null)
				response.pageCount = "1";// [特殊情况：某页获取不到总页数,默认赋值1]
//				 System.out.println(response.pageCount);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	// 主方法:用于本地测试
}
