package DX000268;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000268_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	private static Pattern p = Pattern.compile("(?<=共)\\d+(?=页)");
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
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				String str = originTxtRspContent.substring(originTxtRspContent.indexOf("({") + 1,originTxtRspContent.length() - 1);
				JSONObject obj = new JSONObject(str);
				response.pageCount = obj.get("totalPage").toString();
//				System.out.println(response.pageCount);
				JSONArray oo = obj.getJSONArray("dataList");
				for(int i = 0 ; i < oo.length();i++){
					JSONObject o = oo.getJSONObject(i);
					String projectid = o.get("projectid").toString();
					String itemId = o.get("itemId").toString();
					String id = "projectidTemp=" + projectid + "&itemId=" + itemId;
					String date = o.get("isSueTimeYMStr").toString();
					String title = o.get("remark").toString();
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.title = title;
					response.list.add(bn);
//					System.out.println(bn.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
