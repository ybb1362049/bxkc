package DS_00456;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00456_1_Detail5 extends TxtRspHandler {

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
			//	System.out.println(originTxtRspContent);
				originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList divs = (NodeList)xmlDoc.getElementsByTagName("table");
				String content = null;
				String title = null;
				int n=0;
				if(divs!=null){
					for(int i = 0;i<divs.getLength();i++){
						Element div_ee = (Element)divs.item(i);
						if("81%".equals(div_ee.getAttribute("width"))){
							
							div_ee.getElementsByTagName("tr").item(0).setTextContent(null);
							div_ee.getElementsByTagName("tr").item(1).setTextContent(null);
							Element tr=(org.w3c.dom.Element) div_ee.getElementsByTagName("tr").item(2);
							tr.getElementsByTagName("p").item(1).setTextContent(null);
							
							NodeList aList = div_ee.getElementsByTagName("a");
							if (aList != null) {
								for (int j = 0; j < aList.getLength(); j++) {
									Element a = (Element) aList.item(j);
									String href ="";
									href= a.getAttribute("onclick");
									if(href.length()>6)
									{
										Pattern p=Pattern.compile("'(.*?)'");
										Pattern p2=Pattern.compile(",'(.*?)'\\)");
										Matcher m=p.matcher(href);
										Matcher m2=p2.matcher(href);
										String id="";
										String con="";
										String hre="";
										if(m.find())
										{
											id=m.group(1);
											//System.out.println(str);
										}
										if(m2.find())
										{
											con=m2.group(1);
										}
										if(con.equals("招标文件")||con.equals("资格预审文件"))
										{
											hre="http://218.2.208.144:8094/EBTS/placard/announcebaseinfo/doFileDownload1?file_id="+id;
										}else{
											hre="http://218.2.208.144:8094/EBTS/placard/announcebaseinfo/doFileDownload?file_id="+id;
										}
										a.setAttribute("href", hre);
										a.setAttribute("onclick", "");
									}
								}
							}
							NodeList imgList = div_ee.getElementsByTagName("img");
							if (imgList != null) {
								for (int j = 0; j < imgList.getLength(); j++) {
									Element a = (Element) imgList.item(j);
									String href = a.getAttribute("src");
									if (href.length() > 0 && href.substring(0, 1).equals("/")) {
										href = "http://218.2.208.144:8094" + href;
										a.setAttribute("src", href);
									}
								}
							}
							content=Utils.getNodeHtml(div_ee);
						}
					}
				}
				
				
				response.content = content;
				response.title = title;
			//	System.out.println(content);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
