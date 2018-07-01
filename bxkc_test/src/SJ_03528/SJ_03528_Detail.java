package SJ_03528;

import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_03528_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		Object content;
		String date;
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
//				originTxtRspContent = "<div>" + originTxtRspContent
//						+ "</div>";
				String cStr = originTxtRspContent.substring(originTxtRspContent.indexOf("\"Result\":")+9,originTxtRspContent.length()-2);
//				cStr=cStr.substring(cStr.indexOf("{"),cStr.lastIndexOf("}"));
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
				
				Object content="";
				content=ob.get("Content");
				response.content=content;
				////System.out.printlnn(response.content);
//				JSONArray data=new JSONArray(arry);
//				for (int i = 0; i < data.length(); i++) {
//					BranchNew branchNew = new BranchNew();
//					JSONObject  dataJson = data.getJSONObject(i);
//					branchNew.title = dataJson.getString("Title").trim();
//					branchNew.id = ""+dataJson.getString("id");
//					JSONObject Json = (JSONObject) dataJson.get("OpeningTime");					
//					String a=Json.getString("CreatedAt");
//					a=a.substring(a.indexOf("(")+1,a.indexOf(")"));
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
//					long ds =Long.parseLong(a);
//					String date = sdf.format(ds);
//					String date =dataJson.getString("OpeningTime").trim();
//					date=date.substring(0,10);
//					branchNew.date=date;
//					response.list.add(branchNew);
//					////System.out.printlnn(branchNew.toString());
//				}	
//				Date date=new Date();
//				DateFormat format=new SimpleDateFormat("MM-dd");
//				String time=format.format(date);
//				////System.out.printlnn(time);
//				////System.out.printlnn(response.pageCount);
				 }catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}

