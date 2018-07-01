package DS_00456;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DS_00456_1_Detail extends TxtRspHandler {

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
				originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList divs = (NodeList)xmlDoc.getElementsByTagName("div");
				String content = null;
				String title = null;
				
				if(divs!=null){
					for(int i = 0;i<divs.getLength();i++){
						Element div_ee = (Element)divs.item(i);
						if("mainCont".equals(div_ee.getAttribute("class"))){
							Element h1 = (Element)div_ee.getElementsByTagName("h1").item(0);
							title = h1.getTextContent().trim();
							//System.out.println(title);
							NodeList div_List = (NodeList)div_ee.getElementsByTagName("div");
							if(div_List!=null){
								for(int j = 0;j<div_List.getLength();j++){
									Element div_List_ee = (Element) div_List.item(j);
									if("Zoom".equals(div_List_ee.getAttribute("id"))){
										Element img1 = (Element)div_List_ee.getElementsByTagName("img").item(0);
										Element img2 = (Element)div_List_ee.getElementsByTagName("img").item(1);
										
										if(img1!=null && img2!=null){
											String src1 = img1.getAttribute("src");
											String src2 = img2.getAttribute("src");
											img1.setAttribute("src", "http://xzfw.wuxi.gov.cn"+src1);
											img2.setAttribute("src", "http://xzfw.wuxi.gov.cn"+src2);
											//System.out.println(img1.getAttribute("src"));
											//System.out.println(img2.getAttribute("src"));
										}
										
										Element a1 = (Element)div_List_ee.getElementsByTagName("a").item(1);
										if(a1!=null){
											String a = a1.getAttribute("href");
											a1.setAttribute("href","http://xzfw.wuxi.gov.cn"+a);
										}
										content = Utils.getNodeHtml(div_List_ee); 
									}
								}
							}
						}
					}
				}
				
				
				response.content = content;
				response.title = title;
				//System.out.println(title+""+content);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


}
