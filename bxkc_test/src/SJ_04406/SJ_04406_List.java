package SJ_04406;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

/**
 * 
 * @author User
 *
 */
public class SJ_04406_List extends TxtRspHandler {
	private final static String SPACE = "[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]";
	private String REQUEST_CONTEXT_PATH = null;
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

	// 自学习list的抓取：复制粘贴，自学习
	// 找到相同或相似的结构的标签
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);

				Element theLastRowPage = null;// 如果divWithRows最后一行包括页数，将所在行标签赋给theLastRowPage
				Element allFatherTagOfPage = null;// 页数所在标签

				// 以下boolean值决定是否获取相应的内容
				boolean getUrlFlag = false;// 如果为false则不获取detailt的url
				boolean getDateFlag = true;
				boolean getTitleFlag = true;
				boolean testPrint = false;

				// 具体赋值
				String syntaxOfRows = "{li>h3>a[title]}::<3";//span:matches(20\\d{2}-\\d{1,2}-\\d{1,2})
				String syntaxOfRows_ = "";
				String[] syntaxesOfRowFilter = {}; // 过滤一些row，syntax如：a[class=xx],就会将该标签所在row过滤
				boolean filterateLastRow = true;
				String syntaxOfATag = "";// 如果a标签中已包括了href，则aTag为默认select("a")
				String idStartToTrim = "http://www.ehongcheng.com";// id需要从href中分解出来，默认是以?分解出其中的请求参数
				String idEndToTrim = "";
				String[] regexOfIdOnclick = { "", "" };// 如果href是javascript，则从onclick属性值中解析id，第一个字符串表示id的正则，第二个字符串表示groupId
				String regexOfTitle = "";// 如果title能从a标签中获取就不需要配置该语法
				String regexOfDate = "span";
				String regexOfPage = "{div[class=pageController]}::<1";
				String[] pageNumRegex = { "", "" };// 如果既不包含“共”和“/”，就要特定配置page的语法
				// 配置detailurl所需的参数
				String iw_cmd = "";
				String iw_ir_n = "";

				// 从末页href中解析pageCount需要配置的regex
				String[] moyePageRegex = { "", "" };// "(\\?|\\&)(\\D+)=(\\d*)"--3
				// "(\\D+)(\\d+)\\.(\\D+)"--2

				// re_rows
				Elements rows = getSameRows(document, syntaxOfRows);

				OUTFOR: for (Element row : rows) {
					BranchNew bn = new BranchNew();
					// filter row
					if (!syntaxOfRows_.equals("")) {
						if (!matchStructureWithSytax(row, syntaxOfRows_)) {
							continue;
						}
					}
					if (excludeRow(row, syntaxesOfRowFilter)) {
						continue;
					}
					// 如果row是最后一个row，判断内容是否包含以下字符，若包含就将该行赋给theLastRowPage变量，并过滤掉
					String[] specialWordsOfLastRowPage = { "条", "共", "第", "首", "尾" };
					if (filterateLastRow) {
						if (row == rows.last()) {
							for (String specialWord : specialWordsOfLastRowPage) {
								if (row.text() != null) {
									if (row.text().contains("页") && row.text().contains(specialWord)) {
										theLastRowPage = row;
										continue OUTFOR;
									}
								}
							}
						}
					}

					// re_aTag
					if (syntaxOfATag.equals("")) {
						syntaxOfATag = "a";// 默认是a标签
					}
					Element aTag = row.select(syntaxOfATag).first();

					// re_id 如果href是javascript，id在onclick属性中，下面方法将无效，需重写解析id的代码
					String id = aTag.attr("href").trim();
					if (!id.contains("javascript")) {
						id = resolveId(id, idStartToTrim, idEndToTrim);
					} else if (!regexOfIdOnclick.equals("")) {
						String idOnclick = aTag.attr("onclick");
						if (idOnclick != null) {
							Pattern pattern = Pattern.compile(regexOfIdOnclick[0]);
							Matcher matcher = pattern.matcher(idOnclick);
							if (matcher.find()) {
								id = matcher.group(new Integer(regexOfIdOnclick[1]));
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
						if (title != null)
							bn.title = title;
					}

					// re_date
					if (regexOfDate.equals("")) {
						getDateFlag = false;
					}
					if (getDateFlag) {
						String date = resolveDate(regexOfDate, row);

						if (!date.matches("20\\d{2}-\\d{1,2}-\\d{1,2}")) {
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

				// re_page
				String page = null;

				if (regexOfPage.equals("") && theLastRowPage != null) {
					allFatherTagOfPage = theLastRowPage;
				} else if (!regexOfPage.equals("")) {
					allFatherTagOfPage = (Element) parseSelector(document, regexOfPage, false, 1).first();
				}
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = resolvePage(allFatherTagOfPage, pageNumRegex[0], pageNumRegex[1]);// pageNumRegex是pageCount的具体匹配语法，groupId是group分组的编号

					// 如果 有“末页”关键字
					if (page == null) {
						Elements pageATags = allFatherTagOfPage.select("a");
						String moye = null;
						if (pageATags != null) {
							for (Element pageATag : pageATags) {
								moye = pageATag.text();
								if ((moye.contains("末页") || moye.contains("尾页") || moye.contains(">>"))
										&& !moyePageRegex[0].equals("")) {
									String hrefOfMoye = null;
									hrefOfMoye = pageATag.attr("href");
									Pattern pattern = Pattern.compile(moyePageRegex[0]);
									Matcher matcher = pattern.matcher(hrefOfMoye);
									if (matcher.find()) {
										if (new Integer(moyePageRegex[1]) == 0) {
											throw new Exception("moyePageRegex[1] == 0");
										}
										page = matcher.group(new Integer(moyePageRegex[1]));
									}
								}
							}
						}
					}
					// 最后的a标签给出pageCount
					if (page == null) {
						Elements pageATags = allFatherTagOfPage.select("a");
						int maxPageNum = 0;
						int nextPageNum = 0;
						if (pageATags != null) {
							for (int i = 0; i < pageATags.size(); i++) {
								if (pageATags.get(i).text().matches("\\d{1,3}")) {
									nextPageNum = new Integer(pageATags.get(i).text());
									if (maxPageNum < nextPageNum) {
										maxPageNum = nextPageNum;
									}
								}
							}
							if (maxPageNum != 0) {
								page = String.valueOf(maxPageNum);
							}
						}
					}
					if (page != null)
						response.pageCount = page;
				}
				// -------------sys_page--------------
				if (testPrint) {
					System.out.println(response.pageCount);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	// 得到每一类似行
	public static Elements getSameRows(Element element, String syntax) throws Exception {
		Elements sameRows = (Elements) parseSelector(element, syntax, true, 1);
		return sameRows;
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
			titleBuffer.delete(title.indexOf("【"), title.indexOf("】") + 1);
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
		String regex = "(20\\d{2})(\\D{0,1})(\\d{1,2})(\\D{0,1})(\\d{1,2})";
		Pattern pattern = Pattern.compile(regex);
		String year = null;
		String month = null;
		String day = null;
		if (date_htmls != null) {
			for (Element date_html : date_htmls) {
				date = date_html.text().trim();
				if (date != null) {
					Matcher matcher = pattern.matcher(date);
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
						break;
					}
				}
			}
			date = year + "-" + month + "-" + day;
		}
		return date;
	}


	public static String resolvePage(Element allFatherTagOfPage, String pageNumRegex, String groupId) {
		String page = null;
		String allFatherTagOfPage_str = null;
		allFatherTagOfPage_str = allFatherTagOfPage.text().replaceAll(SPACE, "");
		String regex = "";
		String groupIdd = "0";
		if (!pageNumRegex.equals("") && !groupId.equals("")) {
			groupIdd = groupId;
			regex = pageNumRegex;
		} else {
			if (allFatherTagOfPage_str.contains("/")) {
				regex = "(.*)/(\\d+)(.*)";
				groupIdd = "2";
			} else if (allFatherTagOfPage_str.contains("共")) {
				regex = "(.*)共(\\d+)页(.*)";
				groupIdd = "2";
			}
		}
		if (!regex.equals("")) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(allFatherTagOfPage_str);
			if (matcher.find()) {
				page = matcher.group(new Integer(groupIdd));
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

	public static boolean excludeRow(Element row, String[] syntaxesOfRowFilter) {
		boolean shouldBeFilter = false;
		if (syntaxesOfRowFilter.length > 0) {
			for (String syntaxOfFilter : syntaxesOfRowFilter) {
				System.out.println(syntaxOfFilter);
				if (syntaxOfFilter != null && !syntaxOfFilter.equals("")) {
					if (!row.select(syntaxOfFilter).isEmpty()) {
						shouldBeFilter = true;
					}
				}
			}
		}
		return shouldBeFilter;
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
				if (regParts.size() == 0) {
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

	public static boolean matchStructureWithSytax(Element element, String syntax) {
		boolean canMatch = false;
		if (element.select(syntax) != null) {
			canMatch = true;
		}
		return canMatch;
	}

	// re_main
}
