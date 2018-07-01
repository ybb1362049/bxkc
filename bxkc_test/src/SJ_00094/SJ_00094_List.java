package SJ_00094;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_00094_List extends TxtRspHandler {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
 
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
		// String nextPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

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
				JSONObject obj = new JSONObject(originTxtRspContent);
				JSONArray obj1 = obj.getJSONArray("L");
				for(int i = 0;i<obj1.length();i++){
					JSONObject object = obj1.getJSONObject(i);
					String title = object.getString("ProjectName");
					String id ="N="+object.getString("N")+"&id="+object.getString("ID");
					String date = object.getString("Time");
					date = sdf.format(sdf.parse(date));
					BranchNew bn = new BranchNew();
					bn.title = title;
					bn.id = id;
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=SJ_00094_Detail&" + id;
					bn.url=url;
					bn.date = date;
					response.list.add(bn);
				}
				/*
				Object page = obj.get("pageCount");
				response.pageCount = page.toString();
				*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}