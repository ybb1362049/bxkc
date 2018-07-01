package DS_00707;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.reqrsp.utils.IwReqRspHelper;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00707_List2 extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date="+date+ ";]";
		}

	}
	public static String callApi(String path) {
		try {
			HttpClient httpClient = IwReqRspHelper.getAApacheHttpClientThatTrustAllSSL();//new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(path);
			HttpResponse response = httpClient.execute(getRequest);
			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, "gb2312");
			 return writer.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "";
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
				 ////////System.out.println(originTxtRspContent);
				//response.currentPage=originTxtRspContent;
//				String content=originTxtRspContent.replace("</body>", "");
//				content=content.replace("</html>", "");
//				////System.out.println(originTxtRspContent);
//				////System.out.println(originTxtRspContent);
//				originTxtRspContent=originTxtRspContent.replace("<!--", "");
//				originTxtRspContent=originTxtRspContent.replace("-->]", "");
//				////System.out.println(originTxtRspContent);
//				originTxtRspContent="<html><body>"+originTxtRspContent+"<\body>"+"<"+"\\"+"html>";
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				NodeList div_list=xmlDoc.getElementsByTagName("TABLE");
				for(int j=0;j<div_list.getLength();j++){
				Element div=(Element) div_list.item(j);
				if("#f3f3f3".equals(div.getAttribute("bgcolor"))){
					NodeList tr_list=div.getElementsByTagName("TR");
						for(int i=0;i<tr_list.getLength()-2;i++){
						Element ul_ele=(Element)tr_list.item(i);
							BranchNew branchNew=new BranchNew();
							Element a_ele=(Element) ul_ele.getElementsByTagName("A").item(0);
							if(a_ele==null){
								continue;
							}
//							String title=a_ele.getAttribute("titLe").trim();
							String title=a_ele.getTextContent().trim();
//							title=title.substring(title.indexOf("】")+1);
							if(title.substring(title.length()-1, title.length()).equals(".")){
								title=title.substring(0,title.length()-3);
							}
//							////System.out.println(title);
//							title=title.replaceAll("\\s*", "");
//							title=title.replaceAll("　", "");
//							title=title.replaceAll("\n", "");
							branchNew.title=title;
//							//////System.out.println(title);
							String href=a_ele.getAttribute("href");
//							//////System.out.println(href);
							String InfoID="";
							InfoID=href.substring(href.indexOf("=")+1);
							////////System.out.println(InfoID);
							//String CategoryNum=href.substring(href.lastIndexOf("=")+1);
							branchNew.id=InfoID;
							String date="";
//							Element SUP=(Element)div_list.item(j+1);
//							Element div2=(Element) div_list.item(j+2);
							Element SUP=(Element) ul_ele.getElementsByTagName("TD").item(2);
							date=SUP.getTextContent().trim();
//							date=date.substring(date.indexOf("[")+1,date.lastIndexOf("]"));
//							////System.out.println(date);
//							date=date.substring(0,date.indexOf(" "));
//							date=date.replaceAll("/", "-");
//							date=date.replaceAll("月", "-");
							if(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")).length()<2){
								date=date.substring(0,4)+"-"+"0"+date.substring(date.indexOf("-")+1,date.lastIndexOf("-"))+"-"+date.substring(date.lastIndexOf("-")+1);
							}
							if(date.substring(date.lastIndexOf("-")+1).length()<2){
								date=date.substring(0,date.lastIndexOf("-")+1)+"0"+date.substring(date.lastIndexOf("-")+1);
							}
//							
							branchNew.date=date;
							response.list.add(branchNew);
							////System.out.println(branchNew.toString());
							
						}
						break;
					}
				
				}
				
				String page="1";
//				page=originTxtRspContent.substring(originTxtRspContent.lastIndexOf("(")+1,originTxtRspContent.lastIndexOf(","));
//				page=page.substring(0,page.indexOf(","));
//				page=page.substring(0,page.indexOf(","));
//				int pa=Integer.parseInt(page);;
//				
//				int s=pa%45;
//				if(s!=0){
//					pa=pa/45+1;
//				}else{
//					pa=pa/45;
//				}
//				page=""+pa;
				
//				page=td_ele.getTextContent().trim();
				NodeList div_list1=xmlDoc.getElementsByTagName("TD");
				for(int i=1;i<div_list1.getLength();i++){
					Element td_ele=(Element) div_list1.item(i);
					if("bghg2".equals(td_ele.getAttribute("class"))){
						page=td_ele.getTextContent();
//						if(page.substring(0,1).equals("共")){
//							page=page.substring(page.indexOf("共")+1);
							page=page.substring(page.indexOf("有")+1,page.indexOf("条"));
//						}
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(0,page.indexOf("/")).trim();
//						////System.out.println(page);
//						NodeList td_list1=td_ele.getElementsByTagName("A");
//						Element td_ele1=(Element) td_ele.getElementsByTagName("A").item(td_list1.getLength()-1);
//						page=td_ele.getTextContent().trim();
//						page=td_ele1.getAttribute("href");	
//						page=page.substring(page.lastIndexOf("共")+1,page.lastIndexOf("页")).trim();
//						page=page.substring(0,page.lastIndexOf("页")).trim();
//						page=page.substring(0,page.lastIndexOf("页")).trim();
						int pa=Integer.parseInt(page);
						int pp=pa/10;
						if(pa%10!=0){
							pp=pp+1;
						}
						page=""+pp;
//						}
//						if(td_ele1.getTextContent().equals("...")){
//							page="19";
//							page=td_ele.getTextContent().trim();
//							page=page.substring(page.indexOf("/")+1,page.indexOf("页")).trim();
//						}
					}
				}
//				page=page.substring(page.indexOf("共")+1,page.indexOf("页"));
				response.pageCount=page;
				////System.out.println(response.pageCount);		
						
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	

}
