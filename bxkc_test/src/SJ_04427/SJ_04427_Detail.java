package SJ_04427;

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

public class SJ_04427_Detail extends TxtRspHandler {
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

				boolean getTitleFlag = false;
				boolean getDateFlag = true;// 如果syntaxOfDate是空值，将会把getDateFlag设为false
				boolean testFinalContent = false;// 是否执行测试最终内容
				syntaxOfTitleAndContent = "div[class=main]";
				syntaxOfTitle = "";
				syntaxOfDate = "tr:contains(报名及购买文件开始时间)";// 如果此处时空值的话，就将getDateFlag设为false
				syntaxOfContent = "div[class=main] table[class=tables_02]";
				// clean_clone 根据标签来删除对应元素
				Map<String, String> cleanElesSyntax = new HashMap<String, String>();
				cleanElesSyntax.put("script", "all");
				cleanElesSyntax.put("*[type*=text/css]", "all");
				cleanElesSyntax.put("*[style~=^.*display\\s*:\\s*none\\s*(;\\s*[0-9A-Za-z]+|;\\s*)?$]", "all");
				cleanElesSyntax.put("script", "all");

				// re_titleAndContent
				Elements titleAndContents = document.select(syntaxOfTitleAndContent);
				Element titleAndContent = null;
				if (titleAndContents != null) {
					titleAndContent = titleAndContents.first();
				}
				if (titleAndContent == null) {
					System.out.println("titleAndContent is null ");
				}
				// System.out.println(titleAndContent);
				// re_clone
				Element titleAndContent_clone = null;
				titleAndContent_clone = titleAndContent.clone();
				titleAndContent_clone.children().remove();

				// re_date 放到解析title之上，防止在删除多余title部分将日期删除
				String date = null;
				if (syntaxOfDate.equals("")) {
					getDateFlag = false;
				} else {
					getDateFlag = true;
				}
				if (getDateFlag) {
					String datee = null;
				
					if(!document.select(syntaxOfDate).isEmpty()){
						datee = document.select(syntaxOfDate).first().text();
						if(datee!=null){
							date = datee.replaceAll("(.*)((20\\d{2}\\D[01][0-9]\\D[0123][0-9]))(.*)","$2");
						}
					}
					if(!document.select("tr:contains(公告日期):not(:has(tr))").isEmpty()){
						datee = document.select("tr:contains(公告日期):not(:has(tr))").first().text();
//						System.out.println("aldlfalsdl"+datee);
						if(datee!=null){
							date = datee.replaceAll("(.*)((20\\d{2}\\D[01][0-9]\\D[0123][0-9]))(.*)","$2");
						}
					}
					if(date == null || date.contains("null") || !date.matches("(20\\d{2}\\D[01][0-9]\\D[0123][0-9])")){
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
					// 如果无法获取日期，就从整个originTxtRspContent
					if (!date.contains("null") && date.matches("(20\\d{2}\\D[01][0-9]\\D[0123][0-9])")) {
						response.date = date;
					}
				}

				// re_title_html
				Elements title_htmls = null;
				Element title_html = null;
				if (getTitleFlag) {
					title_htmls = document.select(syntaxOfTitle);
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
				Elements content_htmls = document.select(syntaxOfContent);
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
					if(title_html_str != null){
						titleAndContent_clone.prepend(title_html_str);
					}
				}
				//re_clean
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String resolveDate(String regexOfDate, Element ele) throws Exception {
		String date = null;
		if (!regexOfDate.equals("")) {
			Elements date_htmls = ele.select(regexOfDate);
			if (date_htmls != null) {
				for (Element date_html : date_htmls) {
					String date_str = date_html.text().trim();
					if (date_str != null) {
						date = matchDate(date_str);
					}
				}
			}
		}
		if (date == null || !date.matches("20\\d{2}-\\d{1,2}-\\d{1,2}")) {
			date = ele.text().trim();
			if (date != null) {
				date = date.replaceAll("(20\\d{2}[.|年|/|-]\\d{1,2}[.|月|/|-]\\d{1,2})", "$1");
			}
		}
		return date;
	}

	public static String matchDate(String date) {
		String regex = "(20\\d{2})(\\D{0,1})(\\d{1,2})(\\D{0,1})(\\d{1,2})";
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
						Elements trashes = elementWithTrash.select(cleanEleSyntax);
						Element trash = null;
						if (trashes != null && trashes.size()>0) {
							trash = trashes.first();
							removeEles.add(trash);
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("allele")) {
						Elements trashes = elementWithTrash.select(cleanEleSyntax);
						if (trashes != null) {
							for (Element trash : trashes) {
								removeEles.add(trash);
							}
						}
					}
					if (cleanEleSign.toLowerCase(Locale.ENGLISH).contains("sibling")) {
						Elements trashes = elementWithTrash.select(cleanEleSyntax);
						Element trash = null;
						if (trashes != null && trashes.size()>0) {
							trash = trashes.first();
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
}
