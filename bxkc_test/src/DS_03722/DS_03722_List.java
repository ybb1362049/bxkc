package DS_03722;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.Utils;

public class DS_03722_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

		@Override
		public String toString() {

			return "BranchNew [ title=" + title + ", date=" + date + ",id=" + id + "]" + "\n";
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
				org.w3c.dom.NodeList list_aList = doc.getElementsByTagName("ul");
				Element element1 = (Element) list_aList.item(0);
				NodeList ele_tr = element1.getElementsByTagName("li");
				if (ele_tr != null && ele_tr.getLength() > 0) {
					for (int i = 0; i < ele_tr.getLength(); i++) {
						Element element = (Element) ele_tr.item(i);
						Element eleA = (Element) element.getElementsByTagName("a").item(0);
						String hrefString = eleA.getAttribute("href");
						String id = hrefString.substring(hrefString.indexOf("staticpags/") + 11);
						String title = eleA.getTextContent().trim();
						Element eletd = (Element) element.getElementsByTagName("span").item(0);
						String dateString = null;
						dateString = eletd.getTextContent().trim();
						BranchNew branchNew = new BranchNew();
						branchNew.title = title;
						branchNew.id = id;
						branchNew.date = dateString;
						branchNew.url=super.getNewPathPrefix()+"/?"+super.getAdditionalLinkParamStr()+"&iw-cmd=DS_03722_Detail&iw_ir_2="+id;
						response.list.add(branchNew);
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}