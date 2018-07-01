package DS_00848;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class DS_00848_List1 extends TxtRspHandler {

	private static RspState rsp = RspState.Login;
	
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String extraInfo;
		String url;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", extraInfo=" + extraInfo + ", url=" + url + "]";
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				JSONObject obj = new JSONObject(originTxtRspContent);
				String total = obj.get("total").toString();
				String page = String.valueOf((int)Math.ceil((Double.valueOf(total)) / 20));
				response.pageCount = page;
				JSONArray oo = obj.getJSONArray("rows");
				for(int i = 0 ; i < oo.length() ; i++){
					JSONObject o = oo.getJSONObject(i);
					String title = o.get("itemname").toString();
					String id = o.get("md5id").toString();
					String url = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
							+ "&iw-cmd=DS_00848_Detail&iw_ir_1=/deal/" + id + "&type=2";
					String longDate = o.get("paystime").toString();
					String date = sdf.format(new Date(Long.valueOf(longDate) * 1000));
					
					String extraInfo = o.get("ibstatusmsg").toString();
					String ischange = o.get("ischange").toString();//变更
					String iswinchange = o.get("iswinchange").toString();//变更结果
					if("1".equals(ischange) || "1".equals(iswinchange)){
						extraInfo = "公告变更";
					}
					BranchNew bn = new BranchNew();
					bn.date = date;
					bn.id = id;
					bn.extraInfo = extraInfo;
					bn.url = url;
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
