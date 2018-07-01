package DS_01092;

import java.util.ArrayList;
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
public class DS_01092_List extends TxtRspHandler {
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

	
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);

				// 以下boolean值决定是否获取相应的内容
				boolean getUrlFlag = false;// 如果为false则不获取detailt的url
				boolean getDateFlag = true;
				boolean getTitleFlag = true;

				// re_rows
				String syntaxOfRows = "li[class=list-item]:has(h3[class=summary]:has(a[class=EventCanSelect]))";// span:matches(20\\d{2}-\\d{1,2}-\\d{1,2})
				String syntaxOfRows_ = "";
				String[] syntaxesOfRowFilter = {}; // 过滤一些row，syntax如：a[class=xx],就会将该标签所在row过滤
				boolean filterateLastRow = true;
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
										continue OUTFOR;
									}
								}
							}
						}
					}
//common.goToBulletinDetail('402880e360d4d3f60160f23a8632123f','12','06');return false;
					// re_aTag
					String syntaxOfATag = "";// 如果a标签中已包括了href，则aTag为默认select("a")
					String idStartToTrim = "?";// id需要从href中分解出来，默认是以?分解出其中的请求参数
					String idEndToTrim = "";
					String[] regexOfIdOnclick = { "(goToBulletinDetail\\(')(.*)(','\\d+','\\d+'\\))", "2" };// 如果href是javascript，则从onclick属性值中解析id，第一个字符串表示id的正则，第二个字符串表示groupId
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
					// 配置detailurl所需的参数
					String iw_cmd = "";
					String iw_ir_n = "";
					
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
					String regexOfTitle = "";// 如果title能从a标签中获取就不需要配置该语法
					String title = null;
					if (getTitleFlag) {
						title = resolveTitle(row, regexOfTitle);
						if (title != null)
							bn.title = title;
					}
					// re_date
					String[] regexesOfDate = {"p[class]:contains(发布时间：)","td[colspan]:contains(发布时间：)"};
					String date = null;
					if (regexesOfDate.length<0) {
						getDateFlag = false;
					}
					if (getDateFlag) {
						for(int i=0;i<regexesOfDate.length;i++){
							String regexOfDate = regexesOfDate[i];
							if(regexOfDate!=null&&!regexOfDate.equals("")){
								if(row.select(regexOfDate)!=null){
//									date = resolveDate(regexOfDate, row);
									date = row.select(regexOfDate).text().replaceAll("(.*)(发布时间：)(20\\d{2}\\D{0,1}\\d{1,2}\\D{0,1}\\d{1,2})(.*)", "$3");
									if(date!=null && !date.equals("")){
										break;
									}
								}
							}
						}
						if (!date.contains("null") && date.matches("(20\\d{2}\\D[01][0-9]\\D[0123][0-9])"))
							bn.date = date;
					}
					response.list.add(bn);
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	// 得到每一类似行
	public static Elements getSameRows(Element element, String syntax) throws Exception {
		Elements sameRows = (Elements) element.select(syntax);
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

	public static boolean excludeRow(Element row, String[] syntaxesOfRowFilter) {
		boolean shouldBeFilter = false;
		if (syntaxesOfRowFilter.length > 0) {
			for (String syntaxOfFilter : syntaxesOfRowFilter) {
				if (syntaxOfFilter != null && !syntaxOfFilter.equals("")) {
					if (!row.select(syntaxOfFilter).isEmpty()) {
						shouldBeFilter = true;
					}
				}
			}
		}
		return shouldBeFilter;
	}

	public static boolean matchStructureWithSytax(Element element, String syntax) {
		boolean canMatch = false;
		if (element.select(syntax) != null) {
			canMatch = true;
		}
		return canMatch;
	}
}
