package QX_02099;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_02099_List extends TxtRspHandler {
	private SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd");

	public class BranchNew {
		String title;
		String id;
		String url;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				originTxtRspContent = originTxtRspContent.substring(originTxtRspContent.indexOf("(") + 1,
						originTxtRspContent.length() - 1);
				JSONObject json = new JSONObject(originTxtRspContent);
				JSONArray jArray = json.getJSONArray("lists");
				String id = null, title = null, date = null, url = null;
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json2 = jArray.getJSONObject(i);
					id = json2.getString("n_id");
					title = json2.getString("title");
					date = json2.getString("vc_inputtime");
					date = date.replaceAll(
							".*(\\d{4})[\\年|\\-|\\/|\\.](\\d{1,2})[\\月|\\/|\\-|\\.](\\d{1,2})[\\日|\\-|\\/|\\.]?.*",
							"$1-$2-$3");
					date = newsdf.format(newsdf.parse(date));
					url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
							+ "&iw-cmd=QX_02099_Detail&id=" + id;
					BranchNew bNew = new BranchNew();
					bNew.id = id;
					bNew.title = title;
					bNew.date = date;
					bNew.url = url;
					response.list.add(bNew);
				}
				response.pageCount = "1";
				String pageCount = json.getString("allnums");
				pageCount = (int) Math.ceil(Integer.parseInt(pageCount) / 20.0) + "";
				response.pageCount = pageCount;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
