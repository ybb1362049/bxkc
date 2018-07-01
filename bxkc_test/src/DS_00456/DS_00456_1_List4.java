package DS_00456;

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
import cn.internetware.utils.IO;

public class DS_00456_1_List4 extends TxtRspHandler {
	
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
			//	System.out.println(originTxtRspContent);
				//Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				JSONObject jObject=new JSONObject(originTxtRspContent);
				JSONArray jArray=jObject.getJSONArray("rows");
				for(int i=0;i<jArray.length();i++)
				{
					JSONObject jso=jArray.getJSONObject(i);
					String title=jso.getString("zb_placard_name");
					String id=jso.getString("zb_placard_id");
					String date=jso.getString("bid_notice_date");
					if(date.contains("/")){
						date=date.replace("/", "-");
					}
					Pattern p = Pattern.compile("\\d+{4}-\\d{1,2}-\\d{1,2}");
					
					Matcher m;
					m = p.matcher(date);
					if (m.find()) {
						date = m.group();
					}
					BranchNew bn = new BranchNew();
					bn.id = id+",9";
					String url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00456_1_Detail4&str=" + bn.id;
					bn.url=url;
					bn.title = title;
					bn.date = date;
					response.list.add(bn);
				//	System.out.println(bn);
				}
				
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	

}