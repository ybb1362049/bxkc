package DX000103;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DX000103_List extends TxtRspHandler {

	private static RspState rsp = RspState.Login;

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
				JSONObject obj = new JSONObject(originTxtRspContent);
				JSONObject oo = obj.getJSONObject("data");
				String page = oo.get("pages").toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				response.pageCount = page;
				JSONArray o_list = oo.getJSONArray("list");
				for(int i = 0 ; i < o_list.length();i++){
					JSONObject o = o_list.getJSONObject(i);
					String id = o.get("data_id").toString();
					String create_at = o.get("created_at").toString();
					Long lo = Long.valueOf(create_at) * 1000;
					Date da = new Date(lo);
					String date = sdf.format(da);
					String title = o.get("title").toString();
					BranchNew bn = new BranchNew();
					bn.id =  id;
					bn.date = date;
					bn.title = title;
					response.list.add(bn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
