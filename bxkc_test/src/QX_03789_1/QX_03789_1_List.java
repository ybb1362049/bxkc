package QX_03789_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
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
import cn.internetware.utils.IO;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.gson.Gson;

/**
 * 
 * @author User
 *
 */
public class QX_03789_1_List extends TxtReqRspHandler {
	public final static String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
	public String REQUEST_CONTEXT_PATH = null;
	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		String pageCount;
		String nextPage;
		List<BranchNew> list = new ArrayList<BranchNew>();
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;
		String content;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + ", url=" + url + ", content="
					+ content + "]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				JSONObject objJson = new JSONObject(originTxtRspContent);
				String sourceStr = (String) objJson.get("originTxt");
				String type = null;
				if (objJson.toString().contains("Type-True")) {
					type = objJson.getString("type");
				}
				Document document = Jsoup.parse(sourceStr);
				// listAndPage
				// 解析list需要的用于选择（select）的expression
				String regexOfdivWithRows = null; // 用于进一步缩小list范围的父标签，从而更利于select每个list
				String regexOfRows = null; // 每一行，即每个id+title+date所在的标签
				String regexOfDate = null; // 用于解析date
				String regexOfPage = null; // 用于解析page
				String regexOfTitle = null; // 用于解析title,如果title在aTag中就不需要使用regexOfTtile
				// 使用上面的expression所得到的对象元素（element）
				Element divWithRows = null;// 所有的行（每一行包括id，title，date）
				Element theLastRowPage = null;// 如果divWithRows最后一行包括页数，将所在行标签赋给theLastRowPage
				Element allFatherTagOfPage = null;// 页数所在标签

				// 一下boolean值决定是否获取相应的内容
				boolean getUrlFlag = false;// 如果为false则不获取detailt的url
				boolean getDateFlag = true;
				boolean getTitleFlag = true;
				boolean testPrint = false;
				// 具体expression赋值（该表达式是对jsoup的selector的简单封装，用于更精确地定位网页元素。详见getStake方法）
				regexOfdivWithRows = "div[class=longred_con]";
				regexOfRows = "tr:has(td[class=list_tit12]:has(a)):not(:has(tr))";
				String[] syntaxesOfRowFilter = {}; // 过滤一些row，syntax如：a[class=xx],就会将该标签所在row过滤
				String syntaxOfATag = "";// 如果a标签中已包括了href，则aTag为默认select("a")
				String idStartToTrim = "";// id需要从href中分解出来，默认是以?分解出其中的请求参数
				String idEndToTrim = "";
				String regexOfIdOnclick = "";// id在onclick属性值中
				int groupOfIdOnclick = 0;
				regexOfTitle = "";// 如果title能从a标签中获取就不需要配置该语法
				regexOfDate = "td[class*=date]";
				regexOfPage = "";
				// 配置detailurl所需的参数
				String iw_cmd = "";
				String iw_ir_n = "";


				String regexOfNextPage = "";
				int groupIdOfNextPage = 0; // "(\\D*)(\\d+)(.htm)--2"

				// re_list
				divWithRows = (Element) getStake(regexOfdivWithRows, document, 1, false);
				if (divWithRows == null) {
					System.out.println("divWithUl is null");
				}
				// re_rows
				Elements rows = (Elements) getStake(regexOfRows, divWithRows, 1, true);
				for (Element row : rows) {
					BranchNew bn = new BranchNew();
					// filter row
					if (rowFilter(row, syntaxesOfRowFilter)) {
						continue;
					}
					if (row.text().contains("页") && (row.text().contains("条") || row.text().contains("首")
							|| row.text().contains("共") || row.text().contains("第"))) {
						theLastRowPage = row;
						continue;
					}
					// re_aTag
					if (syntaxOfATag.equals("")) {
						syntaxOfATag = "a";
					}
					Element aTag = row.select(syntaxOfATag).first();

					// re_id 如果href是javascript，id在onclick属性中，下面方法将无效，需重写解析id的代码
					String id = aTag.attr("href").trim();
					if (!id.contains("javascript")) {
						id = resolveId(id, idStartToTrim, idEndToTrim);
					} else if (!regexOfIdOnclick.equals("")) {
						String idOnclick = aTag.attr("onclick");
						if (idOnclick != null) {
							Pattern pattern = Pattern.compile(regexOfIdOnclick);
							Matcher matcher = pattern.matcher(idOnclick);
							if (matcher.find()) {
								id = matcher.group(groupOfIdOnclick);
							}
						}
					}
					if (id != null) {
						bn.id = id.trim();
					}

					// re_url
					REQUEST_CONTEXT_PATH = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
					String newPathPrefix = super.getNewPathPrefix();
					String additionalLinkParamStr = super.getAdditionalLinkParamStr();

					if (getUrlFlag) {
						String url = resolveUrl(newPathPrefix, additionalLinkParamStr, iw_cmd, iw_ir_n, id);
						if (url != null) {
							bn.url = url;
						}
					}
					// re_title
					String title = null;
					if (getTitleFlag) {
						title = resolveTitle(row, regexOfTitle);
						if(title.contains("：")){
							title = title.substring(title.indexOf("：")+1,title.length());
						}
						if (title != null)
							bn.title = title;
					}

					// re_date
					if (regexOfDate.equals("")) {
						getDateFlag = false;
					}
					if (getDateFlag) {
						String date = resolveDate(regexOfDate, row);

						if (!date.matches("20\\d{2}-\\d{2}-\\d{2}")) {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							date = df.format(new Date());
						}
						if (date != null)
							bn.date = date;
					}
					// ----测试-----------sys_4--------------------------------------
					if (testPrint) {
						System.out.println(bn.id);
						System.out.println(bn.url);
						System.out.println(bn.title);
						System.out.println(bn.date);
						System.out.println();
					}

					response.list.add(bn);
				}


				// re_nextPage
				if (!regexOfNextPage.equals("")) {
					String nextPage = null;
					if (type != null && type.contains("/")) {
						type = type.substring(0, type.indexOf("/"));
					}
					String nextPageNum = resovleNextPageNum(allFatherTagOfPage, regexOfNextPage, groupIdOfNextPage);
					if(type==null){
						nextPage = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=" + iw_cmd
								+ "&iw_ir_1=" + "/" + nextPageNum;
					}else{
						nextPage = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr() + "&iw-cmd=" + iw_cmd
								+ "&iw_ir_1=" + type + "/" + nextPageNum;
					}
					if (nextPageNum != null) {
						response.nextPage = nextPage;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String resolveId(String id, String idStartToTrim, String idEndToTrim) {
		while (id.startsWith(".")) {
			for (; id.startsWith(".");) {
				id = id.substring(id.indexOf(".") + 1, id.length());
			}
			if (id.startsWith("/")) {
				id = id.substring(id.indexOf("/") + 1, id.length());
			}
		}

		for (; id.endsWith(".");) {
			id = id.substring(0, id.lastIndexOf("."));
		}

		if (!id.contains("?") && !id.startsWith("/")) {
			id = "/" + id;
		}

		int idStart = 0;
		int idEnd = 0;
		if (idStartToTrim.equals("")) {
			idStart = 0;
		} else {
			idStart = id.indexOf(idStartToTrim) + idStartToTrim.length();
		}
		if (idEndToTrim.equals("")) {
			idEnd = id.length();
		} else {
			idEnd = id.lastIndexOf(idEndToTrim);
		}
		id = id.substring(idStart, idEnd);

		return id;
	}

	public static String resolveUrl(String newPathPrefix, String additionalLinkParamStr, String iw_cmd, String iw_ir_n,
			String id) {
		String url = null;
		url = newPathPrefix + "/?" + additionalLinkParamStr + "&iw-cmd=" + iw_cmd + "&" + iw_ir_n + "=" + id;
		return url;
	}

	public static String resolveTitle(Element row, String regexOfTitle) {
		String title = null;
		if (!regexOfTitle.equals("")) {
			Element title_html = null;
			title_html = row.select(regexOfTitle).last();
			if (title_html != null) {
				title = title_html.text();
			} else {
				// 如果title_html.text()返回空，但title是标签的某个属性值，此处需看情况而定
			}
		} else {
			title = row.select("a").attr("title").trim();
			if (title == null || title.equals("") || title.equals("null")) {
				title = row.select("a").text().trim();
			}
		}
		title = title.replaceAll(SPACE, "");
		// matchesTitle()
		String regex = "(.*)(\\[|.)(20\\d{2}\\D[01][0-9]\\D[0123][0-9])";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			title = matcher.group(1);
		}
		if (title.contains("[") && title.contains("]")) {
			StringBuffer titleBuffer = new StringBuffer(title);
			titleBuffer.delete(title.indexOf("["), title.indexOf("]") + 1);
			title = titleBuffer.toString();
		}
		if (title.contains("【") && title.contains("】")) {
			StringBuffer titleBuffer = new StringBuffer(title);
			titleBuffer.delete(title.indexOf("【") + 1, title.indexOf("】") + 1);
			title = titleBuffer.toString();
		}
		for (; title.endsWith(".");) {
			title = title.substring(0, title.lastIndexOf("."));
		}
		return title;
	}

	public static String resolveDate(String regexOfDate, Element row) {
		String date = null;
		Elements date_htmls = row.select(regexOfDate);
		String regex = "(.*)(20\\d{2})(\\D)([01][0-9])(\\D)([0123][0-9])(.*)";
		Pattern pattern = Pattern.compile(regex);
		String year = null;
		String month = null;
		String day = null;
		if (date_htmls != null) {
			for (Element date_html : date_htmls) {
				date = date_html.text().trim();
				Matcher matcher = pattern.matcher(date);
				if (matcher.find()) {
					year = matcher.group(2);
					month = matcher.group(4);
					day = matcher.group(6);
					if (month.length() < 2) {
						month = "0" + month;
					}
					if (day.length() < 2) {
						day = "0" + day;
					}
					break;
				}
			}
			date = year + "-" + month + "-" + day;
		}
		return date;
	}

	public static String resolvePage(Element allFatherTagOfPage, String pageNumRegex, int groupId) {
		String page = null;
		String allFatherTagOfPage_str = null;
		allFatherTagOfPage_str = allFatherTagOfPage.text().replaceAll(SPACE, "");
		String regex = "";
		int groupIdd = 0;
		if (!pageNumRegex.equals("")) {
			groupIdd = groupId;
			regex = pageNumRegex;
		} else {
			if (allFatherTagOfPage_str.contains("/")) {
				regex = "(.*)/(\\d+)(.*)";
				groupIdd = 2;
			} else if (allFatherTagOfPage_str.contains("共")) {
				regex = "(.*)共(\\d+)页(.*)";
				groupIdd = 2;
			}
		}
		if (!regex.equals("")) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(allFatherTagOfPage_str);
			if (matcher.find()) {
				page = matcher.group(groupIdd);
			}
		}
		if (page != null && !page.matches("\\d+")) {
			page = null;
		}
		return page;
	}

	public static String resovleNextPageNum(Element allFatherTagOfPage, String regexOfNextPage, int groupIdOfNextPage) {
		// re_nextPage
		String nextPageNum = null;
		if (allFatherTagOfPage != null) {
			Elements pageATags = null;
			pageATags = allFatherTagOfPage.select("a");
			if (pageATags != null) {
				for (int i = 0; i < pageATags.size(); i++) {
					if (pageATags.get(i).text().matches("下.*页")) {
						Pattern pattern = Pattern.compile(regexOfNextPage);
						Matcher matcher = pattern.matcher(pageATags.get(i).attr("href").replaceAll("\\s+", ""));
						if (matcher.find()) {
							nextPageNum = matcher.group(groupIdOfNextPage);
						}
						break;
					}
				}
			}
		}
		return nextPageNum;
	}

	public boolean rowFilter(Element row, String[] syntaxesOfRowFilter) {
		boolean shouldBeFilter = false;
		if (syntaxesOfRowFilter.length > 0) {
			for (String syntaxOfFilter : syntaxesOfRowFilter) {
				if (syntaxOfFilter != null && syntaxOfFilter.equals("")) {
					if (row.select(syntaxOfFilter) != null) {
						shouldBeFilter = true;
					}
				}
			}
		}
		return shouldBeFilter;
	}

	// re_stake
	public static Object getStake(String syntax, Element element, int regPartNum, boolean returnAllElement)
			throws Exception {
		String regLeft = null; // jsoup 的selector的syntax
		String regRight = null; // 准确地位元素element的index
		Elements regParts = null;
		Element regPart = null;

		// 解析syntax
		if (syntax.contains("::")) {
			regLeft = syntax.substring(syntax.indexOf("{") + 1, syntax.lastIndexOf("}"));
			regRight = syntax.substring(syntax.lastIndexOf("::") + 2, syntax.length());
			if (regRight.startsWith("<")) {
				regRight = regRight.substring(regRight.indexOf("<") + 1, regRight.length());
			}
		} else {
			regLeft = syntax;
			regRight = "1";
		}
		// 解析regLeft，使用jsoup的selector
		if (regLeft != null && !regLeft.equals("")) {
			regParts = element.select(regLeft);
		} else {
			System.out.println("regLeft is null");
		}
		if (regParts != null && !regParts.toString().equals("")) {
			if (returnAllElement) {// 当returnAllElement为true时，返回所有定位的元素（集合Elements）
				return regParts;
			}
			if (regParts.size() == 1) {
				regPart = regParts.first();
				for (int i = 1; i <= new Integer(regRight) - 1; i++) {
					regPart = regPart.parent();
				}
			} else {
				regPart = regParts.get(regPartNum - 1);// regPartNum确定当定位的元素element多个时，到底使用第几个element，一般使用第一个元素（first）
				for (int i = 1; i <= new Integer(regRight) - 1; i++) {
					regPart = regPart.parent();
				}
			}
		} else {
			throw new Exception("regParts is null, please check your syntax=" + syntax);
		}

		return regPart;
	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwReq) {
		IwResponse iwRsp = null;
		String host = null;
		String requestPath = null;
		String scheme = null;
		String type = null;
		String path = null;
		String result = null;
		String page = null;
		String originTxtRspContent = null;
		String startToTrim = "";
		String endToTrim = "";
		String trimMarkInTrimStr = "";
		boolean customJumpPageParam = false;// 是否有自定义的跳页参数
		boolean httpclientFirst = true;// 是否优先使用httpclient抓取网页，适用于简单的网站
		Map<String, String> originMap = new HashMap<String, String>();
		try {
			String pageParam = "";// 在带有跳页参数的首页url中提出那个跳页参数

			scheme = iwReq.getScheme();
			host = iwReq.getHost();
			requestPath = iwReq.getRequestPath();// 上下文context中request-path部分（也就是request-uri-replacement-pattern）
			// System.out.println(requestPath);
			if (!startToTrim.equals("") || !endToTrim.equals("")) {
				type = strTrim(requestPath, startToTrim, endToTrim, trimMarkInTrimStr);
			}

			// 分两种情况：自定义跳页参数说明非参数跳页，无自定义跳页参数说明是参数跳页
			if (customJumpPageParam) {
				page = iwReq.getRequestPathParam("page");// 获取跳页参数，可以自己增加这样一个参数
				Map<String, String> requestPathParams = iwReq.getRequestPathParams();
				if (requestPathParams.size() > 0) {
					for (Map.Entry<String, String> paramEntry : requestPathParams.entrySet()) {
						// 如果有自定义的page跳页参数，抓取其自身就有的参数时要将其过滤掉
						if (paramEntry.getKey().equals("page")) {
							continue;
						}
						path = scheme + "://" + host + requestPath + "?" + paramEntry.getKey() + "="
								+ paramEntry.getValue();
					}
				} else {
					path = scheme + "://" + host + requestPath;
				}
			} else {
				if (!pageParam.equals("")) {
					page = iwReq.getRequestPathParam(pageParam);
				}
				Map<String, String> requestPathParams = iwReq.getRequestPathParams();
				if (requestPathParams.size() > 0) {
					path = scheme + "://" + host + requestPath + "?";
					for (Map.Entry<String, String> paramEntry : requestPathParams.entrySet()){
						path = path + paramEntry.getKey() + "="
								+ paramEntry.getValue() + "&";
					}
				} else {
					path = scheme + "://" + host + requestPath;
				}
			}
			 System.out.println(path);

			// getContent
			if (httpclientFirst) {
				result = getContentWithHttpClient(path);
			} else {
				result = getContentWithHtmlUnit(path, page, customJumpPageParam);
			}
			 System.out.println(result);

			String hasType = "Type-False";
			if (type != null) {
				hasType = "Type-True";
				originMap.put("type", type);
			}
			originMap.put("hasType", hasType);
			originMap.put("originTxt", result);

			Gson gson = new Gson();
			originTxtRspContent = gson.toJson(originMap);

			iwRsp = new DefaultIwResponse(null, originTxtRspContent.getBytes("utf8"), null, 0, "ok");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return iwRsp;
	}

	public static HtmlPage getPage(String url) {
		HtmlPage htmlpage = null;
		try {
			WebClient webclient = new WebClient(BrowserVersion.CHROME);
			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(true);
			webclient.getOptions().setTimeout(1000 * 60);
			webclient.waitForBackgroundJavaScript(1000 * 3);
			htmlpage = webclient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlpage;
	}

	// 使用htmlunit获取内容
	public static String getContentWithHtmlUnit(String url, String page, boolean customJumpPageParam) {
		HtmlPage htmlpage = null;
		String content = null;
		HtmlTextInput pageInput = null;
		HtmlButtonInput button = null;
		String intputProp = "";
		String buttonProp = "";
		String inputValue = "";
		String buttonValue = "";
		try {
			if (page == null || page.equals("") || page.equals("1") || !customJumpPageParam) {
				htmlpage = getPage(url);
				content = htmlpage.asXml();
			} else if (customJumpPageParam) {
				htmlpage = getPage(url);
				// content = htmlpage.asXml();
				DomNodeList<DomElement> inputElements = htmlpage.getElementsByTagName("input");
				for (DomElement input : inputElements) {
					if (input.getAttribute(intputProp).equals(inputValue)) {
						pageInput = (HtmlTextInput) input;
					}
				}
				for (DomElement input : inputElements) {
					if (input.getAttribute(buttonProp).equals(buttonValue)) {
						button = (HtmlButtonInput) input;
					}
				}
				if (pageInput != null) {
					pageInput.setValueAttribute(page);
					htmlpage = (HtmlPage) button.click();
				}
				content = htmlpage.asXml();
			}
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	// 使用httpclien获取内容
	public static String getContentWithHttpClient(String url) {
		String str = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
		httpGet.addHeader("Content-type", "text/html; charset=GBK");
		httpGet.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "gzip, deflate");
		httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpGet.addHeader("Connection", "keep-alive");
		httpGet.addHeader("Accept-Encoding", "gzip, deflate");
		httpGet.addHeader("Upgrade-Insecure-Requests", "Upgrade-Insecure-Requests");
		 httpGet.addHeader("Cookie","JSESSIONID=666D0A94B6D3FA30B1F3A3940E120039; TS01aca45f=01627a1cf53fc52dc0fcce7afb90569fe6906b8ccf1a5171ad0baec36ec4f0bd3fc55fef7c875a00fa569b6cec7e34c29f0126e2f9; UM_distinctid=160f456bc4f6fb-06833b1378f98d-4323461-13c680-160f456bc5080f; CNZZDATA1261208313=1216810429-1515925310-%7C1515925310; TS01a2fe5c=01627a1cf586d56ab6f1b9223645d3ee9e1c68eb09da6bc54a86af73149f8344ad55a0f86c; CNZZDATA1259204835=385050202-1515927857-%7C1515929727; Hm_lvt_763378eb652ce1003434622a86b3dc46=1515931123; Hm_lpvt_763378eb652ce1003434622a86b3dc46=1515931123");

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000)
				.build();
		httpGet.setConfig(requestConfig);

		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				InputStream ins = resEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "utf8"));
				StringBuilder sb = new StringBuilder();
				while ((str = reader.readLine()) != null) {
					sb.append(str);
				}
				str = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static String strTrim(String strNeedTrim, String startToTrim, String endToTrim, String trimMarkInTrimStr) {
		int idStart = 0;
		int idEnd = 0;
		String strTrimed = null;
		if (startToTrim.equals("")) {
			idStart = 0;
		} else {
			idStart = strNeedTrim.indexOf(startToTrim) + startToTrim.length();
		}
		if (endToTrim.equals("")) {
			idEnd = strNeedTrim.length();
		} else {
			idEnd = strNeedTrim.lastIndexOf(endToTrim);
		}
		strTrimed = strNeedTrim.substring(idStart, idEnd);

		if (!trimMarkInTrimStr.equals("")) {
			if (strTrimed.contains(trimMarkInTrimStr)) {
				strTrimed = strTrimed.substring(0, strTrimed.indexOf(trimMarkInTrimStr));
			}
		}
		return strTrimed;

	}

	// re_main
}
