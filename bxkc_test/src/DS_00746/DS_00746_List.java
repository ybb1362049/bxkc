package DS_00746;



import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00746_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		//String currentPage;
	}

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
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try{
			    Document xmlDoc=Utils.getDocByContent(originTxtRspContent);//获取文档内容
			    
			 
			   
			    	NodeList li_list = xmlDoc.getElementsByTagName("tr");
			    	if(li_list.getLength() > 0){
			    		for(int j = 0; j < li_list.getLength(); j ++){
			    			Element li = (Element)li_list.item(j);
			    			if("24".equals(li.getAttribute("height"))){
			    				NodeList spls = li.getElementsByTagName("td");
				    			NodeList a = li.getElementsByTagName("a");
				    			if(a.getLength() > 0){
				    				BranchNew branchNew = new BranchNew();
				    				Element spe = (Element)spls.item(3);
				    				String tmp = spe.getTextContent().trim();
//				    				branchNew.date = tmp.substring(tmp.indexOf("[")+1, tmp.indexOf("]"));
					    		
				    				branchNew.date = tmp;
				    				
					    			String til = ((Element)a.item(0)).getAttribute("title");
					    			branchNew.title = til.trim();
					    			
					    			String hre = ((Element)a.item(0)).getAttribute("href");
					    			
					    			branchNew.id = hre.substring(hre.indexOf("?")+1);
					    			
					    			response.list.add(branchNew);
			    			}
			    			
			//	   		  //System.out.println(branchNew.toString()); pagination
			    			}
			    			
			    		}
			    	}
			    NodeList dss = xmlDoc.getElementsByTagName("td");
			    for(int k = 0 ; k < dss.getLength() ; k ++){
			    	Element dee = (Element)dss.item(k);
			    	if("huifont".equals(dee.getAttribute("class"))){
			    		
			    		String str = dee.getTextContent();
			    		str = str.substring(str.indexOf("/")+1);
			    		response.pageCount = str;
			    	}
			    }
			    
				
											
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
