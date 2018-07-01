package DX000267;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000267_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	private static final String Element = null;

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
				String content="";
				
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				JSONObject obj = new JSONObject(originTxtRspContent);
                
				JSONObject obj2 = obj.getJSONObject("returnValue");
				JSONArray obj4 = obj2.getJSONArray("resources");
				JSONObject obj3 = obj4.getJSONObject(0);
				content="<h1>"+obj2.getString("forenoticetitle")+"</h1>"+"<br>";
				content=content+"项目地址："+obj2.getString("city")+"<br>";
				content=content+"报名截止时间："+obj2.getString("jhinvitebidtime")+"<br>";
				content=content+"报名条件："+obj2.getString("ygtext")+"<br>";
				content=content+"招标负责人："+obj2.getString("contactman")+"<br>";
				content=content+"联系电话："+obj2.getString("contactphone")+"<br>";
				content=content+"电子邮箱："+obj2.getString("email")+"<br>";
				content=content+"附件："+"<a href=http://cg.900950.com"+obj3.getString("path")+">"+obj3.getString("name")+"</a>"+"<br>";
			    response.content=content;
			    //System.out.println(content);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
