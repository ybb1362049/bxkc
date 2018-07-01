package cn.internetware.utils;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelectType
{
	public static final int DEFAULT = -1;
	public static final int TEXT = 0;
	public static final int ATTR = 1;
	public static final int HTML = 2;
	public static final int OUTERHTML = 3;
	
	public interface SelectTypeInterface
	{
		public String handleString(Element element, String string, int selectIndex, int... selectType);
	}
	
	public interface RemoveTypeInterface
	{
		public String handleString(Elements elements, String string, int removeIndex, int... removeType);
	}
	
	public static String selectElements(Document document, String select, SelectTypeInterface selectTypeInterface, int... selectType)
	{
		Elements elements = document.select(select);
		String string = "";
		if (elements != null && elements.size() > 0)
		{
			for (int i = 0; i < elements.size(); i++)
			{
				string = "";
				Element element = elements.get(i);
				switch (selectType[0])
				{
					case SelectType.TEXT:
						string = element.text();
						
						break;
					case SelectType.ATTR:
						string = element.attr("src");
						break;
					
					case SelectType.HTML:
						string = element.html();
						break;
					
					case SelectType.OUTERHTML:
						string = element.outerHtml();
						break;
					
					case SelectType.DEFAULT:
					default:
						string = element.outerHtml();
						break;
				}
				if (selectType.length > 1 && selectTypeInterface!=null)
				{
					string = selectTypeInterface.handleString(element, string, 1, selectType);
				}
//				//System.out.println(string);
			}
		}
		return string;
	}
	
	public static String removeElements(Document document, String select, RemoveTypeInterface removeTypeInterface, int... removeType)
	{
		Elements elements = document.select(select);
		String string = "";
		if (elements != null && elements.size() > 0)
		{
			for (int i = 0; i < elements.size(); i++)
			{
				string = "";
				Element element = elements.get(i);
				document.replaceWith(element);
				switch (removeType[0])
				{
					case SelectType.TEXT:
						
						break;
					case SelectType.ATTR:
						break;
					
					case SelectType.HTML:
						break;
					
					case SelectType.OUTERHTML:
						break;
					
					case SelectType.DEFAULT:
					default:
						break;
				}
				if (removeType.length > 1)
				{
					string = removeTypeInterface.handleString(elements, string, 1, removeType);
				}
				//System.out.println(string);
			}
		}
		return string;
	}
	
	public static Document removeElements2(Document document, String select)
	{
		Elements elements = document.select(select);
		if (elements != null && elements.size() > 0)
		{
			for (int i = 0; i < elements.size(); i++)
			{
				Element element = elements.get(i);
				if (element != null)
				{
					try
					{
						// element.replaceWith(element);
						element.remove();
					} catch (Exception e)
					{
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}
		//System.out.println(document);
		return document;
	}
	
	public static Document removeElements3(Document document, String select, String... select2)
	{
		Elements elements = document.select(select);
		if (elements != null && elements.size() > 0)
		{
			for (int i = 0; i < elements.size(); i++)
			{
				Element element = elements.get(i);
				
				if (element != null)
				{
					for (int j = 0; j < select2.length; j++)
					{
						Elements elements2 = element.select(select2[j]);
						for (int k = 0; k < elements2.size(); k++)
						{
							Element element2 = elements2.get(k);
							try
							{
								// element.replaceWith(element);
								element2.remove();
							} catch (Exception e)
							{
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	//	//System.out.println(document);
		return document;
	}
	

	public static Document removeHtml(Document document, String select, String... select2)
	{
		Elements elements = document.select(select);
		if (elements != null && elements.size() > 0)
		{
			for (int i = 0; i < elements.size(); i++)
			{
				Element element = elements.get(i);
				if (element != null)
				{
					for (int j = 0; j < select2.length; j++)
					{
						String replace = select2[j];
						if (element.outerHtml().contains(replace))
						{
							element.after(element.outerHtml().replace(replace, ""));
							element.remove();
						}
					}
				}
			}
		}
//		//System.out.println(document);
		return document;
	}

	public static String substring(String string, String beginIndexString, int beginIndexOffset, String endIndexString, int endIndexOffset)
	{
		if (string != null && !StringUtil.isBlank(string))
		{
			// String beginIndexString = "<!--内容--> ";
			int beginIndexStringLength = beginIndexString.length();
			// String endIndexString = "<!--内容 end--> ";
			string = string.substring(string.indexOf(beginIndexString) + beginIndexStringLength + beginIndexOffset, string.indexOf(endIndexString) + endIndexOffset);
		}
		return string;
	}
	public static String substring2(String string, int beginIndexOffset, String endIndexString, int endIndexOffset)
	{
		if (string != null && !StringUtil.isBlank(string))
		{
			string = string.substring(beginIndexOffset, string.indexOf(endIndexString) + endIndexOffset);
		}
		return string;
	}
	
}
