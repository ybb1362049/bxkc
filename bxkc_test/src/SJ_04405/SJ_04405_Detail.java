package SJ_04405;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.Context;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class SJ_04405_Detail extends TxtRspHandler {
	public final static String TIMEREGEX = "((?!0000)[0-9]{4})\\D((0[1-9]|1[0-2]))\\D(((0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29)\\D";
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

		String[] notFoundSyns = { "页面不存在" };

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
		String regexOrigin = "(.*)404(.{0,10})(.)ot(.{0,10})(.)ound(.*)";
		Pattern patternOrigin = Pattern.compile(regexOrigin);
		Matcher matcherOrigin = patternOrigin.matcher(notFoundBuff);
		if (matcherOrigin.find()) {
			response.content = "404 Not Found";
			return response;
		}

		// 修改某些flag值确定是否得到title和date
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				document.outputSettings().prettyPrint(true);

				String syntaxOfTitleAndContent = null;
				String syntaxOfTitle = null;
				String syntaxOfDate = null;
				String syntaxOfContent = null;

				boolean getTitleFlag = true;
				boolean getDateFlag = true;// 如果syntaxOfDate是空值，将会把getDateFlag设为false
				boolean testPrint = false;// 测试打印标志，如果为true将在控制台打印输出
				boolean testFinalContent = false;// 是否执行测试最终内容
				syntaxOfTitleAndContent = "{table>tbody>tr>td>div>span[class=Biaoti]}::<6";
				syntaxOfTitle = "{table>tbody>tr>td>div>span[class=Biaoti]}::<1";
				syntaxOfDate = "span[class=Biaotix]";// 如果此处时空值的话，就将getDateFlag设为false
				syntaxOfContent = "{table>tbody>tr>td>div>span[class=Biaoti]}::<6";

				// re_titleAndContent
				Elements titleAndContents = parseSelector(document, syntaxOfTitleAndContent, false, 1);
				Element titleAndContent = null;
				if (titleAndContents != null) {
					titleAndContent = titleAndContents.first();
				}
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
					if (!date.contains("null")) {
						response.date = date;
					}
				}

				// re_title_html
				Elements title_htmls = null;
				Element title_html = null;
				if (getTitleFlag) {
					title_htmls = parseSelector(document, syntaxOfTitle, false, 1);// titleAndContent.select("div.list_info_title").first();
					if (title_htmls != null) {
						title_html = title_htmls.first();
						if (title_html.children() != null) {
							title_html.children().remove();
						}
						if (title_html.text().trim() != null) {
							String title = title_html.text().trim();
							if (!title.equals("")) {
								response.title = title_html.text();
							}
						}
					}
				}
				String title_html_str = null;
				if (title_html != null) {
					title_html_str = title_html.outerHtml();
				}

				// re_content
				Elements content_htmls = parseSelector(document, syntaxOfContent, false, 1);
				Element content_html = null;
				String content_html_str = null;
				if (content_htmls != null) {
					content_html = content_htmls.first();
				}
				if (content_html != null) {
					content_html_str = content_html.outerHtml();
				} else {
					throw new Exception("content_html is null");
				}
				// 拼接title和content
				titleAndContent_clone.prepend(content_html_str);
				if (getTitleFlag) {
					titleAndContent_clone.prepend(title_html_str);
				}

				// clean_clone 根据标签来删除对应元素
				Map<String, String> cleanElesSyntax = new HashMap<String, String>();
				cleanElesSyntax.put("script", "all");
				cleanElesSyntax.put("*[type*=text/css]", "all");
				cleanElesSyntax.put("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]", "all");
				cleanElesSyntax.put("span[class=Biaotix]", "aele");
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

	public static String resolveDate(String regexOfDate, Element ele) throws Exception {
		String date = null;
		if (!regexOfDate.equals("")) {
			Elements date_htmls = parseSelector(ele, regexOfDate, true, 1);
			if (date_htmls != null) {
				for (Element date_html : date_htmls) {
					String date_str = date_html.text().trim();
					if (date_str != null) {
						date = matchDate(date_str);
					}
				}
			}
		}
		if (date == null || !date.matches(TIMEREGEX)) {
			date = ele.text().trim();
			if (date != null) {
				date = matchDate(date);
			}
		}
		return date;
	}

	public static String matchDate(String date) {
		String regex = "(20\\d{2})(\\D)(\\d+)(\\D)(\\d+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(date);
		String year = null;
		String month = null;
		String day = null;
		if (matcher.find()) {
			year = matcher.group(1);
			month = matcher.group(3);
			day = matcher.group(5);
			if (month.length() < 2) {
				month = "0" + month;
			}
			if (day.length() < 2) {
				day = "0" + day;
			}
		}
		return date = year + "-" + month + "-" + day;
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
			if (!lowerCaseSrc.startsWith("http") && !lowerCaseSrc.contains("javascript")
					&& !lowerCaseSrc.contains("@")) {
				if (lowerCaseSrc.startsWith("/")) {
					src = replaceHost + src;
				} else {
					src = replaceHost + "/" + src;
				}
			} else if (lowerCaseSrc.contains("javascript")) {
				return false;
			}
			srcElement.attr("src", src);
		}
		return true;
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

	public static void cleanElement(Element elementWithTrash, Map<String, String> cleanElesSyntaxWithSign)
			throws Exception {
		Elements removeEles = new Elements();
		if (cleanElesSyntaxWithSign.size() != 0) {
			for (Map.Entry<String, String> entry : cleanElesSyntaxWithSign.entrySet()) {
				String cleanEleSyntax = entry.getKey();
				String cleanEleSign = entry.getValue();
				if (!cleanEleSyntax.equals("")) {
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("aele")) {
						Elements trashes = parseSelector(elementWithTrash, cleanEleSyntax, false, 1);
						Element trash = null;
						if (trashes != null) {
							trash = trashes.first();
						}
						if (trash != null) {
							removeEles.add(trash);
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("allele")) {
						Elements trashes = (Elements) parseSelector(elementWithTrash, cleanEleSyntax, true, 1);
						if (trashes != null) {
							for (Element trash : trashes) {
								removeEles.add(trash);
							}
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("sibling")) {
						Elements trashes = parseSelector(elementWithTrash, cleanEleSyntax, true, 1);
						Element trash = null;
						if (trashes != null) {
							trash = trashes.first();
						}
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

	// parseSelector
	public static Elements parseSelector(Element element, String syntax, boolean returnAllElement, int regPartIndex)
			throws Exception {
		String syntaxLeft = null; // jsoup 的selector的syntax
		String syntaxRight = null; // 准确地位元素element的index
		Elements regParts = null;
		Element regPart = null;
		Elements allEles = new Elements();

		// 解析syntax
		if (syntax.contains("::")) {
			syntaxLeft = syntax.substring(syntax.indexOf("{") + 1, syntax.lastIndexOf("}"));
			syntaxRight = syntax.substring(syntax.lastIndexOf("::") + 2, syntax.length());
			if (syntaxRight.startsWith("<")) {
				syntaxRight = syntaxRight.substring(syntaxRight.indexOf("<") + 1, syntaxRight.length());
			}
		} else {
			syntaxLeft = syntax;
			syntaxRight = "1";
		}
		// 解析regLeft，使用jsoup的selector
		if (syntaxLeft != null && !syntaxLeft.equals("")) {
			regParts = element.select(syntaxLeft);
		} else {
			throw new Exception("syntaxLeft = null or syntaxLeft = \"\"");
		}
		if (regParts != null && !regParts.toString().equals("")) {
			if (returnAllElement) {
				for (Element part : regParts) {
					for (int i = 1; i <= new Integer(syntaxRight) - 1; i++) {
						part = part.parent();
					}
					allEles.add(part);
				}
				if(regParts.size() == 0){
					return regParts;
				}
			} else {
				if (regParts.size() == 1) {
					regPart = regParts.first();
					for (int i = 1; i <= new Integer(syntaxRight) - 1; i++) {
						regPart = regPart.parent();
					}
					allEles.add(regPart);
				} else {
					regPart = regParts.get(regPartIndex - 1);// regPartIndex确定当定位的元素element多个时，到底使用第几个element(一般使用第一个元素（first）)
					for (int i = 1; i <= new Integer(syntaxRight) - 1; i++) {
						regPart = regPart.parent();
					}
					allEles.add(regPart);
				}
			}
		} else {
			throw new Exception("regParts is null, please check your syntax=" + syntax);
		}
		return allEles;
	}

}
