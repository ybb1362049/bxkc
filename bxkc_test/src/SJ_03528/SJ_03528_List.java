package SJ_03528;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_03528_List extends TxtRspHandler {

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
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";]";
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
			try  {
	//			Document doc = Utils.getDocByContent(originTxtRspContent);
//				////System.out.printlnn(originTxtRspContent);
				String cStr = originTxtRspContent.substring(originTxtRspContent.indexOf("{\"d\":")+5,originTxtRspContent.length()-1);	
//				////System.out.printlnn(cStr);
//				cStr = cStr.substring(0,cStr.lastIndexOf("]")+1);		
//				String c = originTxtRspContent.substring(originTxtRspContent.lastIndexOf("["),originTxtRspContent.lastIndexOf("]")+1);
//				JSONArray da=new JSONArray(c);
//				for (int i = 0; i < da.length(); i++) {
//					JSONObject  dataJson = da.getJSONObject(i);
//					int s=dataJson.getInt("TOTAL");
//					////System.out.printlnn(s);
//					int page;
//					page=s%20;
//					if(s!=0){
//					page=s/20+1;	
//					}else {page=s/20;	}			
//					response.pageCount="1";
//				}				
//				////System.out.printlnn(response.pageCount);
				JSONObject 	ob=new JSONObject(cStr);
				String arry= ob.get("Rows").toString();
				int s=ob.getInt("TotalPages");
//				int page=1;
//				page=s%8;
//				if(page!=0){
//					page=s/8+1;	
//				}else {page=s/8;	}			
				response.pageCount=""+s;
				JSONArray data=new JSONArray(arry);
				for (int i = 0; i < data.length(); i++) {
					BranchNew branchNew = new BranchNew();
					JSONObject  dataJson = data.getJSONObject(i);
					branchNew.title = dataJson.getString("ConTitle").trim();
					branchNew.id = ""+dataJson.getInt("ContentID");
//					if(branchNew.id.equals("")){
//						branchNew.id = ""+dataJson.getString("gongShiGuid");
//					}
//					JSONObject Json = (JSONObject) dataJson.get("OpeningTime");					
//					String a=Json.getString("CreatedAt");
//					a=a.substring(a.indexOf("(")+1,a.indexOf(")"));
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
//					long ds =Long.parseLong(a);
//					String date = sdf.format(ds);
					String date =dataJson.getString("strCreateTime").trim();
					date=date.substring(0,10);
					branchNew.date=date;
					response.list.add(branchNew);
					////System.out.printlnn(branchNew.toString());
				}		
//				Date date=new Date();
//				DateFormat format=new SimpleDateFormat("MM-dd");
//				String time=format.format(date);
//				////System.out.printlnn(time);
				////System.out.printlnn(response.pageCount);
				 } catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;


}