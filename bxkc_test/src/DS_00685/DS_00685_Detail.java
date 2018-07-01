package DS_00685;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.Utils;

public class DS_00685_Detail extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
		String date;
	}

	private static String url = "http://cgb.yantai.gov.cn";

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document doc = Jsoup.parse(originTxtRspContent);
				String content = "";
				Element td = doc.select("td.tit_con").first();
				if (td != null) {
					content = td.outerHtml();
					response.title=td.text().trim();
				}
				Element div = doc.select("div#zoom").last();
				if (div != null) {
					div.select("script").remove();
					div.select("[style=display:none]").remove();
					div.select("input").remove();
					Elements aList = div.select("a");
					for (Element a : aList) {
						String href = a.attr("href");
						if (href.contains("mailto")) {
							continue;
						}
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("/") == 0) {
								href = url + href;
								a.attr("href", href);
							} else if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								a.attr("href", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								a.attr("href", href);
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								a.attr("href", href);
							}
						}
					}
					Elements imgList = div.select("IMG");
					for (Element img : imgList) {
						String href = img.attr("src");
						if (href.length() > 10 && href.indexOf("http") != 0) {
							if (href.indexOf("../") == 0) {
								href = href.replace("../", "");
								href = url + "/" + href;
								img.attr("src", href);
							} else if (href.indexOf("./") == 0) {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href.substring(2);
								img.attr("src", href);
							} else if (href.indexOf("/") == 0) {
								href = url + href;
								img.attr("src", href);
							} else {
								href = super.getContextInfo(Context.REQUEST_CONTEXT_PATH) + href;
								img.attr("src", href);
							}
						}
					}
					String con = div.outerHtml();
					con = con.replaceAll("点击：(\\d+)", "");
					content += con;
				}else if(originTxtRspContent.contains("Access not allowed"))
				{
					content="该新闻需要登录才能查看";
					response.date="2016-01-01";
				}
				response.content = content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwRsp = null;
		try {
			// 得到Params的跳转页码
			String InfoID = iwRequest.getRequestPathParam("InfoID");
			String CategoryNum=iwRequest.getRequestPathParam("CategoryNum");
			String detailUrl="http://www.aqzbcg.org/Front_qs/InfoDetail/?InfoID="+InfoID+"&CategoryNum="+CategoryNum;
			String result = Utils.callApi(detailUrl);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;
	}

}
