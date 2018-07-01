package XX0949;

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

public class XX0949_List extends TxtRspHandler {
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	Pattern page = Pattern.compile("/(\\s*)(\\d+)(\\s*)");
	Matcher m;

	public class BranchNew {
		String title;
		String id;
		String date;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date + ";]";
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
				JSONObject json = new JSONObject(originTxtRspContent);
				if (json.has("datalist")) {
					JSONArray arr = json.getJSONArray("datalist");
					for (int i = 0; i < arr.length(); i++) {
						BranchNew bn = new BranchNew();
						JSONObject oo = arr.getJSONObject(i);
						if (oo.has("Day") && oo.has("Month") && oo.has("Year")) {
							String date = oo.getString("Year") + "-" + oo.getString("Month") + "-" + oo.getString("Day");
							bn.date = date;
						}
						if (oo.has("Url")) {
							bn.id = oo.getString("Url");
						}
						response.list.add(bn);
					}
					if (json.has("total")) {
						String count = json.get("total").toString();
						int total = Integer.parseInt(count);
						response.pageCount = (total - 1) / 20 + 1 + "";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
