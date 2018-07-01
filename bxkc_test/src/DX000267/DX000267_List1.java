package DX000267;

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

public class DX000267_List1 extends TxtRspHandler {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
 
	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
		// String nextPage;
	}

	public class BranchNew {
		String title;
		String content;
		String date;
		String id;

		public String toString() {
			return "BranchNew[ title=" + title + ";content=" + content + ";date=" + date
					+ ";id="+id+";]";
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
				JSONObject obj1=obj.getJSONObject("returnValue");
				JSONArray obj2 = obj1.getJSONArray("recordList");
				for(int i = 0;i<obj2.length();i++){
					JSONObject object = obj2.getJSONObject(i);
					String title = object.getString("forenoticetitle");
					String id=object.getString("cgplanguid");
					String date = object.getString("publishtime");
					date =  date.substring(0,10);
					date = sdf.format(sdf.parse(date));
					String content ="项目名称："+title+"<br>";
					content=content+"时间："+date+"<br>";
					content=content+"中标单位："+object.getString("supplierName");
					
					BranchNew bn = new BranchNew();
					bn.title = title;
					bn.content = content;
					bn.id=id;
					bn.date = date;
					response.list.add(bn);
					//System.out.println(bn);
				}
				
				Object page = obj1.get("pages");
				response.pageCount = page.toString();
				//System.out.println(page.toString());
				
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