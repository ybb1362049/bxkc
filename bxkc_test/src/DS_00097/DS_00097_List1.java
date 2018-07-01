package DS_00097;

import java.text.SimpleDateFormat;
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

public class DS_00097_List1 extends TxtRspHandler {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

	public DS_00097_List1() {

	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		if (arg0 == RspState.Login) {
			try {
				String typeid = "8";
				JSONObject object = new JSONObject(arg1);
				JSONObject rtnObject = object.getJSONObject("rtnObject");
				JSONArray list = rtnObject.getJSONArray("list");
				for (int i = 0 ; i < list.length() ; i++) {
					BranchNew bn = new BranchNew();
					JSONObject obj1 = list.getJSONObject(i);
					String str = obj1.getString("updatetime");
					String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(str);
					if (matcher.find()) {
						String date = matcher.group();
						bn.date = sdf.format(sdf.parse(date));
					}
					bn.title = obj1.getString("bidsectionnames");
					bn.id = obj1.getString("id");
					bn.content = "<a href='http://117.25.161.110:7070/xmjy/app/common/attachment/downLoadFile?fileType=word&typeId="
							+ typeid + "&id=" + bn.id+"'>点击下载</a>";
					response.list.add(bn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
