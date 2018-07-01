package QX_02023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

public class QX_02023_Detail extends TxtReqRspHandler {
	public final static String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		String title;
		String date;
		String content;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();

		String[] notFoundSyns = { "404", "页面不存在", "Not Found" };

		String notFoundBuff = null;

		if (originTxtRspContent.length() > 1000) {
			notFoundBuff = originTxtRspContent.substring(0, 1000);
		} else {
			notFoundBuff = originTxtRspContent;
		}
		for (String notFoundSyn : notFoundSyns) {
			if (notFoundBuff.contains(notFoundSyn)) {
				response.content = "404 Not Found";
				return response;
			}
		}
		if (originTxtRspContent.startsWith("<script>")) {
			response.content = "404 Not Found";
			return response;
		}

		// 修改某些flag值确定是否得到title和date
		if (rspState == rsp) {
			try {
				JSONObject objJson = new JSONObject(originTxtRspContent);
				String sourceStr = (String) objJson.get("originTxt");

				Document document = Jsoup.parse(sourceStr);
				document.outputSettings().prettyPrint(true);

				String syntaxOfTitleAndContent = null;
				String syntaxOfTitle = null;
				String syntaxOfDate = null;
				String syntaxOfContent = null;

				boolean getTitleFlag = false;
				boolean getDateFlag = true;// 如果syntaxOfDate是空值，将会把getDateFlag设为false
				boolean testPrint = false;// 测试打印标志，如果为true将在控制台打印输出
				boolean testFinalContent = false;// 是否执行测试最终内容
				syntaxOfTitleAndContent = "table[class=xilan_tab]";
				syntaxOfTitle = "table[class=xilan_tab] h1[id=jiuctit]";
				syntaxOfDate = "";// 如果此处时空值的话，就将getDateFlag设为false
				syntaxOfContent = "table[class=xilan_tab] td[id=xilan_cont]";
				// replaceHost = "http://xdec.cnpc.com.cn";//
				// 如果是javascript的href就要findUrl
				// 注意getContextRequestPath()
				// re_titleAndContent

				if (document.select("table[class=xilan_tab]") == null
						|| document.select("table[class=xilan_tab]").isEmpty()) {
					response.content = "404 Not Found";
					return response;
				}
				Element titleAndContent = (Element) getStake(syntaxOfTitleAndContent, document, 1, false);
				if (titleAndContent == null) {
					System.out.println("titleAndContent is null ");
				}
				// -------------sys_title
				// System.out.println(titleAndContent);
				// re_clone
				Element titleAndContent_clone = null;
				titleAndContent_clone = titleAndContent.clone();
				titleAndContent_clone.children().remove();

				// re_date 放到解析title之上，防止在删除多余title部分将日期删除
				if (syntaxOfDate.equals("")) {
					getDateFlag = false;
				} else {
					getDateFlag = true;
				}
				if (getDateFlag) {
					String date = resolveDate(syntaxOfDate, document);
					// 如果无法获取日期，就从整个originTxtRspContent
					if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
						String regex_ori = "(\\D)(20\\d{2})(\\D)(\\d{1,2})(\\D)(\\d{1,2})(\\D)";
						Pattern pattern_ori = Pattern.compile(regex_ori);
						Matcher matcher_ori = pattern_ori.matcher(document.text().replaceAll("\\s+", ""));
						String year_ori = null;
						String month_ori = null;
						String day_ori = null;
						if (matcher_ori.find()) {
							year_ori = matcher_ori.group(2);
							month_ori = matcher_ori.group(4);
							day_ori = matcher_ori.group(6);
							if (month_ori.length() < 2) {
								month_ori = "0" + month_ori;
							}
							if (day_ori.length() < 2) {
								day_ori = "0" + day_ori;
							}
						}
						date = year_ori + "-" + month_ori + "-" + day_ori;
					}
					if (!date.contains("null")) {
						response.date = date;
					}
				}

				// re_title_html
				Element title_html = null;
				if (getTitleFlag) {
					title_html = (Element) getStake(syntaxOfTitle, document, 1, false);// titleAndContent.select("div.list_info_title").first();
					if (title_html != null) {
						if (title_html.children() != null) {
							title_html.children().remove();
						}
						if (title_html.text().trim() != null) {
							String title = title_html.text().trim();
							if (!title.equals("")) {
								response.title = title_html.text();
							}
						}
					} else if (title_html == null) {
						getTitleFlag = false;
					}
				}
				String title_html_str = null;
				if (title_html == null) {
					title_html = (Element) getStake(syntaxOfTitle, document, 1, false);
				}
				if (title_html != null) {
					title_html_str = title_html.outerHtml();
				}

				// re_content
				Element content_html = (Element) getStake(syntaxOfContent, document, 1, false);
				String content_html_str = null;
				if (content_html != null) {
					content_html_str = content_html.outerHtml();
				} else {
					throw new Exception("content_html is null");
				}
				// 拼接title和content
				titleAndContent_clone.prepend(content_html_str);
				titleAndContent_clone.prepend(title_html_str);

				// clean_clone
				Map<String, String> cleanElesSyntax = new HashMap<String, String>();
				cleanElesSyntax.put("script", "all");
				cleanElesSyntax.put("text/css", "all");
				cleanElesSyntax.put("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]", "all");
				cleanElement(titleAndContent_clone, cleanElesSyntax);
				// re_url
				boolean completedUrl = completeUrl(titleAndContent_clone, null);
				if (!completedUrl) {
					System.out.println("url 中包含javascript，需要手动获取参数拼接url");
				}

				// 下面的代码判断返回内容的正确性，如果文本内容少于100，又没有a和img标签，不太确定内容的正确性，应该抛出异常（打包时可以无条件删去）
				if (testFinalContent) {
					if (titleAndContent_clone.text().length() < 100 && (titleAndContent_clone.select("a") == null
							|| titleAndContent_clone.select("img") == null)) {
						throw new Exception("字符少于1000，并且没有a或者img标签");

					}
				}

				// .replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]","")
				response.content = "<div>" + titleAndContent_clone.outerHtml().trim() + "</div>";
				if (testPrint) {
					System.out.println(response.content);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String resolveDate(String regexOfDate, Element row) throws Exception {
		String date = null;
		if (!regexOfDate.equals("")) {
			Element date_html = (Element) getStake(regexOfDate, row, 1, false);
			if (date_html != null) {
				date = date_html.text().trim();
				String regex = "(.*)(20\\d{2})(.{1})(\\d+)(.{1})(\\d+)(.*)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(date);
				String year = null;
				String month = null;
				String day = null;
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
				}
				date = year + "-" + month + "-" + day;
			}
		}

		return date;
	}

	public boolean completeUrl(Element htmlNeedCompleteUrl, Map<String, String> params) {
		String replaceHost = getContextRequestPath();

		Elements attachments = htmlNeedCompleteUrl.select("a[href]");
		for (Element oneAttachment : attachments) {
			String href = oneAttachment.attr("href");
			String lowerCaseHref = href.toLowerCase(Locale.ENGLISH);
			if (!lowerCaseHref.startsWith("http") && !lowerCaseHref.contains("javascript")
					&& !lowerCaseHref.contains("@")) {
				if (lowerCaseHref.startsWith("/")) {
					href = replaceHost + href;
				} else {
					href = replaceHost + "/" + href;
				}
			} else if (lowerCaseHref.contains("javascript")) {
				return false;
			}
			oneAttachment.attr("href", href);
		}
		for (Element srcElement : htmlNeedCompleteUrl.select("*[src]")) {
			String src = srcElement.attr("src");
			String lowerCaseSrc = src.toLowerCase(Locale.ENGLISH);
			if (!lowerCaseSrc.startsWith("http") && !lowerCaseSrc.contains("onclick") && !lowerCaseSrc.contains("@")) {
				if (lowerCaseSrc.startsWith("/")) {
					src = replaceHost + src;
				} else {
					src = replaceHost + "/" + src;
				}
			} else if (lowerCaseSrc.contains("onclick")) {
				return false;
			}
			srcElement.attr("src", src);
		}
		return true;
	}

	public static void cleanElement(Element elementWithTrash, Map<String, String> cleanElesSyntaxWithSign)
			throws Exception {
		Elements removeEles = new Elements();
		if (cleanElesSyntaxWithSign.size() != 0) {
			for (Map.Entry<String, String> entry : cleanElesSyntaxWithSign.entrySet()) {
				String cleanEleSyntax = entry.getKey();
				String cleanEleSign = entry.getValue();
				if (!cleanEleSyntax.equals("")) {
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("aele")) {
						Element trash = (Element) getStake(cleanEleSyntax, elementWithTrash, 1, false);
						if (trash != null) {
							removeEles.add(trash);
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("allele")) {
						Elements trashes = (Elements) getStake(cleanEleSyntax, elementWithTrash, 1, true);
						if (trashes != null) {
							for (Element trash : trashes) {
								removeEles.add(trash);
							}
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("sibling")) {
						Element trash = (Element) getStake(cleanEleSyntax, elementWithTrash, 1, false);
						if (trash != null) {
							removeEles.add(trash);
							Element siblinOfTrash = trash.nextElementSibling();
							while (siblinOfTrash != null) {
								removeEles.add(siblinOfTrash);
								siblinOfTrash = siblinOfTrash.nextElementSibling();
							}
						}
					}
				}
			}
		}
		if (removeEles != null && removeEles.size() > 0) {
			for (Element removeEle : removeEles) {
				removeEle.remove();
			}
		}
	}

	private String getContextRequestPath() {
		String regex = "(http(s)?://)([\\w-]+\\.)+([\\w-]+)";
		Pattern p = Pattern.compile(regex);
		String contextRequestPath = super.getContextInfo(Context.REQUEST_CONTEXT_PATH);
		if (contextRequestPath == null) {
			return "null";
		}
		Matcher m = p.matcher(contextRequestPath);
		if (m.find()) {
			contextRequestPath = m.group();
		}
		return contextRequestPath;
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
			// throw new Exception("regParts is null, please check your syntax="
			// + syntax);
			return null;
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
		String originTxtRspContent = null;
		Map<String, String> originMap = new HashMap<String, String>();
		try {

			scheme = iwReq.getScheme();
			host = iwReq.getHost();
			requestPath = iwReq.getRequestPath();// 上下文context中request-path部分（也就是request-uri-replacement-pattern）

			Map<String, String> requestPathParams = iwReq.getRequestPathParams();
			if (requestPathParams.size() > 0) {
				path = scheme + "://" + host + requestPath + "?";
				for (Map.Entry<String, String> paramEntry : requestPathParams.entrySet()) {
					path = path + paramEntry.getKey() + "=" + paramEntry.getValue() + "&";
				}
			} else {
				path = scheme + "://" + host + requestPath;
			}
			// System.out.println(path);
			// getContent
			result = getContentWithHttpClient(path);
			// System.out.println(result);
			originMap.put("type", type);
			originMap.put("originTxt", result);

			Gson gson = new Gson();
			originTxtRspContent = gson.toJson(originMap);

			iwRsp = new DefaultIwResponse(null, originTxtRspContent.getBytes("utf8"), null, 0, "ok");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return iwRsp;
	}

	// 使用htmlunit获取内容
	public static String getContentWithHtmlUnit(String url) {
		HtmlPage htmlpage = null;
		String content = null;
		try {
			WebClient webclient = new WebClient(BrowserVersion.CHROME);
			webclient.getOptions().setCssEnabled(false);
			webclient.getOptions().setJavaScriptEnabled(true);
			webclient.getOptions().setTimeout(1000 * 60);
			htmlpage = webclient.getPage(url);
			webclient.waitForBackgroundJavaScript(1000 * 3);
			content = htmlpage.asXml();
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
		httpGet.addHeader("Cookie",
				"UM_distinctid=160f456bc4f6fb-06833b1378f98d-4323461-13c680-160f456bc5080f; Hm_lvt_763378eb652ce1003434622a86b3dc46=1515931123; Hm_lpvt_763378eb652ce1003434622a86b3dc46=1515931123; CNZZDATA1261208270=556244705-1515927432-%7C1516003225; TS01a2fe5c=01627a1cf55ed56828d82ef8a12b15ccc730dab344bc4fa0d01e3f698e7796af0018250707");

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

}
