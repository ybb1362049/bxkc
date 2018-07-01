package cn.internetware.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.cyberneko.html.parsers.DOMParser;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.internetware.phone.extension.reqrsp.utils.IwReqRspHelper;

public class Utils {
	private static final Pattern INVISIBLE_PATTERN = Pattern.compile("display( )*:( )*none", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final String[] CONTROL_TYPE_LIST = { "INPUT", "SELECT", "TEXTAREA"};
	
	/**
	 * Get html document
	 * @param content
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document getDocByContent(String content) throws SAXException,
			IOException {
		content = content.trim();
		InputSource source = new InputSource(new StringReader(content.replaceFirst("xmlns=\"[^\"]*\"", "")));
		DOMParser parser = new DOMParser();
		parser.parse(source);
		Document doc = parser.getDocument();
		
		return doc;
	}
	
	/**
	 * 获取隐藏的模块input/textarea
	 * @param content
	 * @param className
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public static List<Control> getHiddenControls(String content, String hiddenClassName) 
			throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{
		Document doc = getDocByContent(content);
		List<Control> hiddenList = new ArrayList<Control>();
		
		// 获取所有的隐藏控件(hidden和display:none)
		hiddenList.addAll(getHiddenControls(doc, content, "", "",hiddenClassName, true));
		
		// 获取所有隐藏的div/span里的非隐藏的控件
		NodeList divList = doc.getElementsByTagName("div");
		NodeList spanList = doc.getElementsByTagName("span");
		List<Node> nodeList = mergeNodeList(divList, spanList);
		if(nodeList!=null && nodeList.size()>0){
			for(int i=0;i<nodeList.size();i++){
				Element ele = (Element)nodeList.get(i);
				String style = ele.getAttribute("style");
				String classNameStr = ele.getAttribute("class");
				Matcher matcher = INVISIBLE_PATTERN.matcher(style);
				if(matcher.find() || (hiddenClassName != null && classNameStr!=null && classNameStr.contains(hiddenClassName))){
					String nodeHtml = getNodeHtml(nodeList.get(i));
					hiddenList.addAll(getHiddenControls(doc, nodeHtml, style, classNameStr,hiddenClassName, false));
				}
			}
		}
		
		return hiddenList;
	}
	
	// 获取隐藏/非隐藏的控件
	public static List<Control> getHiddenControls(Document doc , String content, String parentSytle, String parentClassName, String hiddenClassName, Boolean isHidden) 
			throws SAXException, IOException{
		List<Control> hiddenList = new ArrayList<Control>();
		if(doc == null || !isHidden){
			doc = getDocByContent(content);
		}
		
		NodeList nodeList1 = doc.getElementsByTagName("INPUT");
		NodeList nodeList2 = doc.getElementsByTagName("Textarea");
		List<Node> nodeList = mergeNodeList(nodeList1, nodeList2);
		if(nodeList != null && nodeList.size()>0){
			for(int i=0;i<nodeList.size();i++){
				Element ele = (Element)nodeList.get(i);
				String name = ele.getAttribute("name");
				String value = ele.getAttribute("value");
				String type = ele.getAttribute("type");
				String style = ele.getAttribute("style");
				String classNameStr = ele.getAttribute("class");
				Matcher matcher = INVISIBLE_PATTERN.matcher(style);
				boolean classIsHidden = hiddenClassName != null && hiddenClassName != "" 
						&& classNameStr!=null && classNameStr != "" && classNameStr.contains(hiddenClassName);
				boolean flag = false;
				if(isHidden){
					flag = (type!= null && "hidden".equals(type.toLowerCase())) || 
							matcher.find() || classIsHidden;
				} else{
					Matcher matcherParent = INVISIBLE_PATTERN.matcher(parentSytle);
					boolean parentClassIsHidden = hiddenClassName != null && hiddenClassName != "" 
							&& parentClassName!=null && parentClassName!="" && parentClassName.contains(hiddenClassName);
					if(matcherParent.find() || parentClassIsHidden){
						flag = !(type!= null && "hidden".equals(type.toLowerCase())) && !classIsHidden && !matcher.find();
					}
				}
				if(flag){
					Control control = new Control();
					control.setName(name);
					control.setValue(value);
					control.setType("hidden");
					hiddenList.add(control);
				}
			}
		}
		
		return hiddenList;
	}
	
	/**
	 * 获取<table>作为list的数据
	 * @param tableHtml
	 * @param hasTitle
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public static ListDataResponse getTableList(String tableHtml, boolean hasTitle) throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{
		ListDataResponse tableList = new ListDataResponse();
		Document doc = getDocByContent(tableHtml);
		NodeList trNodeList = doc.getElementsByTagName("tr");
		if(trNodeList!=null && trNodeList.getLength()>0){
			for(int i = 0; i < trNodeList.getLength(); i++){
				List<Node> tdNodeList = getChildNodes(trNodeList.item(i), "TD");
				if(hasTitle && i == 0){
					if(tdNodeList== null || tdNodeList.size() == 0){
						tdNodeList = getChildNodes(trNodeList.item(i), "TH");
					}
					if(tdNodeList!=null && tdNodeList.size()>0){
						List<String> titleList = new ArrayList<String>();
						for(Node node : tdNodeList){
							titleList.add(node.getTextContent().trim());
						}
						tableList.getTitleList().add(titleList);
					}
				} else {
					if(tdNodeList!=null && tdNodeList.size()>0){
						List<String> dataList = new ArrayList<String>();
						for(Node node : tdNodeList){
							dataList.add(node.getTextContent().trim());
						}
						tableList.getDataList().add(dataList);
					}
				}
			}
		}
		
		return tableList;
	}
	
	/**
	 * 获取Table里的KeyValue格式的控件
	 * @param tableHtml
	 * @param keyClass
	 * @param valueClass
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static List<KeyControlMap> getTableKeyValue(String tableHtml, String keyClass, String valueClass) 
			throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{
		List<KeyControlMap> controlList = new ArrayList<KeyControlMap>();
		Document doc = getDocByContent(tableHtml);
		NodeList tdList = doc.getElementsByTagName("TD");
		if(tdList != null && tdList.getLength() > 0){
			for(int i =0; i<tdList.getLength(); i++){
				KeyControlMap keyControl = new KeyControlMap();
				controlList.add(keyControl);
				String key = tdList.item(i).getTextContent().trim();
				keyControl.setKey(key);
				
				if(i<tdList.getLength()){
					i++;
					String tdHtml = getNodeHtml(tdList.item(i));
					Document tdDoc = getDocByContent(tdHtml);
					for (String controlType : CONTROL_TYPE_LIST) {
						NodeList controlNodeList = tdDoc.getElementsByTagName(controlType);
						if (controlNodeList != null && controlNodeList.getLength() > 0) {
							for (int controlIndex = 0; controlIndex < controlNodeList.getLength(); controlIndex++) {
								Control control = new Control();
								keyControl.getControlList().add(control);
								Node node = controlNodeList.item(controlIndex);
								Element controlElement = (Element)node;
								control.setId(controlElement.getAttribute("id"));
								control.setName(controlElement.getAttribute("name"));
								control.setValue(controlElement.getAttribute("value"));
								if(controlType.equals("SELECT")){
									control.setValidValue(getSelectChildren(node));
								}
							}
						}
					}
				}
				
				if(keyControl.getKey()== null || keyControl.getKey().equals("") || keyControl.getControlList() == null || keyControl.getControlList().size() == 0){
					controlList.remove(keyControl);
				}
			}
		}
		
		return controlList;
	}
	
	public static List<Control> getInputControls(String html) 
			throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{
		List<Control> controlList = new ArrayList<Control>();
		Document doc = getDocByContent(html);
		for (String controlType : CONTROL_TYPE_LIST) {
			NodeList controlNodeList = doc.getElementsByTagName(controlType);
			if (controlNodeList != null && controlNodeList.getLength() > 0) {
				for (int controlIndex = 0; controlIndex < controlNodeList.getLength(); controlIndex++) {
					Control control = new Control();
					controlList.add(control);
					Node node = controlNodeList.item(controlIndex);
					Element controlElement = (Element)node;
					control.setId(controlElement.getAttribute("id"));
					control.setName(controlElement.getAttribute("name"));
					control.setValue(controlElement.getAttribute("value"));
					control.setType(controlElement.getAttribute("type"));
					if(controlType.equals("SELECT")){
						control.setValidValue(getSelectChildren(node));
					}
				}
			}
		}
		
		return controlList;
	}
	
	private static List<Pair> getSelectChildren(Node node){
		List<Pair> pairList = new ArrayList<Pair>();
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null && nodeList.getLength()>0){
			for(int i = 0; i< nodeList.getLength(); i++){
				if(nodeList.item(i).getNodeName().equals("OPTION")){
					Element ele = (Element)nodeList.item(i);
					Pair pair = new Pair(ele.getAttribute("value"), ele.getTextContent());
					pairList.add(pair);
				}
			}
		}
		
		return pairList;
	}
	
	/**
	 * 合并两个NodeList
	 * @param nodeList1
	 * @param nodeList2
	 * @return
	 */
	public static List<Node> mergeNodeList(NodeList nodeList1,
			NodeList nodeList2) {
		List<Node> nodeList = new ArrayList<Node>();
		if (nodeList1 != null && nodeList1.getLength() > 0) {
			for (int i = 0; i < nodeList1.getLength(); i++) {
				nodeList.add(nodeList1.item(i));
			}
		}

		if (nodeList2 != null && nodeList2.getLength() > 0) {
			for (int i = 0; i < nodeList2.getLength(); i++) {
				nodeList.add(nodeList2.item(i));
			}
		}

		return nodeList;
	}
	
	/**
	 * 获取node的html
	 * @param node
	 * @return
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	public static String getNodeHtml(Node node) throws TransformerFactoryConfigurationError, TransformerException{
		StringWriter writer = new StringWriter();
		Transformer transformer;
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperty(OutputKeys.STANDALONE,"no");
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		
		String html = writer != null ? writer.toString() : "";
		//html = StringEscapeUtils.unescapeHtml3(html);
		
		return html;
	}
	
	/**
	 * 根据xpath获取node
	 * @param doc
	 * @param xpathValue
	 * @return
	 * @throws XPathExpressionException 
	 */
	/*public static Node getNodeByXpath(Document doc, String xpathValue) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate(xpathValue, doc, XPathConstants.NODE);
		
		return node;
	}*/
	
	public static Node getNodeByXpath(Document doc, String xpathValue) throws JaxenException{
		xpathValue = formatXpath(xpathValue);
		DOMXPath xpath = new DOMXPath(xpathValue);
		return (Node)xpath.selectSingleNode(doc.getFirstChild());
	}
	
	public static Node getNodeByXpath(String htmlContent, String xpathValue) throws SAXException, IOException, JaxenException{
		Document doc = getDocByContent(htmlContent);
		if(doc != null){
			xpathValue = formatXpath(xpathValue);
			return getNodeByXpath(doc, xpathValue);
		}
		
		return null;
	}
	
	/*public static NodeList getNodeListByXpath(Document doc, String xpathValue) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xpath.evaluate(xpathValue, doc, XPathConstants.NODESET);
		
		return nodeList;
	}*/
	
	@SuppressWarnings("unchecked")
	public static List<Node> getNodeListByXpath(Document doc, String xpathValue) throws JaxenException{
		/*XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xpath.evaluate(xpathValue, doc, XPathConstants.NODESET);
		
		return nodeList;*/
		xpathValue = formatXpath(xpathValue);
		DOMXPath xpath = new DOMXPath(xpathValue);
		List<Node> nodeList = xpath.selectNodes(doc.getFirstChild());
		if(nodeList == null){
			nodeList = new ArrayList<Node>();
		}
		
		return nodeList;
	}
	
	public static List<Node> getNodeListByXpath(String htmlContent, String xpathValue) throws SAXException, IOException, JaxenException{
		xpathValue = formatXpath(xpathValue);
		Document doc = getDocByContent(htmlContent);
		if(doc != null){
			return getNodeListByXpath(doc, xpathValue);
		}
		
		return null;
	}
	
	/**
	 * 由于用org.cyberneko.html.parsers.DOMParser获取Document对象后，所有的节点名会变成大写，属性会变成小写，
	 * 所以需要把调用者传入的xpath格式化一下
	 * @param xpath
	 * @return
	 */
	private static String formatXpath(String xpath){
		String newXpath = "";
		String[] temps = xpath.split("/");
		for(int i=0;i<temps.length;i++){
			String temp = temps[i];
			if(StringUtils.isNotEmpty(temp)){
				if(!temp.contains("[") && !temp.contains("]") && !temp.contains("::")){ //xpath like '/bookstore/book/title'
					temp = temp.toUpperCase();
				} else if(temp.contains("[") && temp.contains("]")){ //xpath like '//*[@id=\"test_id\"]'
					if(temp.contains("::")){
						String[] predicates = temp.split("::");
						if(predicates.length == 2){
							String[] properties = predicates[1].split("\\[");
							if(properties.length == 2){//xpath like '/descendant::label[text()=\"agree\"]'
								temp = predicates[0] + "::" + properties[0].toUpperCase() + "[" + properties[1];
							}
						}
					} else{
						String[] properties = temp.split("\\[");
						temp = properties[0].toUpperCase() + "[" + properties[1];
					}
				} else if(!temp.contains("[") && !temp.contains("]") && temp.contains("::")){//xpath like 'attribute::lang' , 'child::text()' or 'attribute::lang'
					String[] predicates = temp.split("::");
					if(!"attribute".equals(predicates[0]) && !predicates[1].contains("(")){
						temp = predicates[0] + "::" + predicates[1].toUpperCase();
					}
				}
			}
			
			if(i == temps.length -1){
				newXpath += temp;
			} else{
				newXpath += temp + "/";
			}
		}
		
		return newXpath;
	}
	
	/**
	 * 获取某个节点的下一个兄弟节点
	 * @param node
	 * @param tag
	 * @return
	 */
	@SuppressWarnings("unused")
	private Node getNextSibling(Node node, String tag){
		if(node != null){
			node = node.getNextSibling();
			if(node.getNodeName().equals(tag)){
				return node;
			} else{
				return getNextSibling(node, tag);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * 获取某个节点的字节点
	 * @param node
	 * @param tag
	 * @return
	 */
	public static List<Node> getChildNodes(Node node, String tag){
		List<Node> list = new ArrayList<Node>();
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null && nodeList.getLength()>0){
			for(int i=0;i<nodeList.getLength();i++){
				if(nodeList.item(i).getNodeName().equals(tag)){
					list.add(nodeList.item(i));
				}
			}
		}
		
		return list;
	}
	
	public static void setFieldValue(Object obj, String fieldName, String value) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = obj.getClass().getDeclaredFields();
		 for (int i = 0; i < fields.length; i++) {
			 fields[i].setAccessible(true);
			 if(fields[i].getName().equals(fieldName)){
				 fields[i].set(obj, value);
				 break;
			 }
		 }
	}
	
	public static String getNodeAttr(Node node, String attrVal) throws TransformerFactoryConfigurationError, TransformerException {
		if(node != null){
			String val = node.getTextContent();
			if (attrVal.contains("@")
					&& attrVal.length() > attrVal.indexOf("@") + 2) {
				String attr = attrVal.substring(attrVal.indexOf("@") + 1);
				val = ((Element) node).getAttribute(attr);
			} else if (attrVal.toLowerCase().contains("text()")) {
				val = node.getTextContent().trim();
			} else if (attrVal.toLowerCase().contains("html()")) {
				val = getNodeHtml(node);
			}

			return val;
		} else {
			return "";
		}
	}
	
	/**
	 * 解析str = "&#23458;&#36816;&#31449;&#38382;&#39064;";
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str) {
		StringBuilder sb = new StringBuilder();
		try {
			if (str != null && str.startsWith("&#") && str.endsWith(";")) {
				str = str.replace("&#", "");
				if (str.endsWith(";")) {
					str = str.substring(0, str.length() - 1);
					String[] temp = str.split(";");
					if (temp != null && temp.length > 0) {
						for (String t : temp) {
							if (t.startsWith(" ")) {
								sb.append(" ");
							}
							char chr = (char) Integer.parseInt(t.trim());
							sb.append(chr);
						}
					}
				}

				return sb.toString();
			} else {
				return str;
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		return str;
	}

	/**
	 * 解析str = "\u817E\u8BAF\u79FB\u52A8\u4E92\u8054\u7F51";
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeUnicode1(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}

		return outBuffer.toString();
	}
	
	final static int TIMEOUT_IN_MS = 1000000;
	
	public static String callApiUsingPost(String path, Map<String, String> params) {
		try {
			HttpClient httpClient = IwReqRspHelper.getAApacheHttpClientThatTrustAllSSL();//new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(path);
	        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
	        for (String key : params.keySet()) {
	        	 nvps.add(new BasicNameValuePair(key, params.get(key)));  
			}
	        postRequest.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));  
	        ////System.out.println(EntityUtils.toString(new UrlEncodedFormEntity(nvps,"UTF-8")));
			HttpResponse response = httpClient.execute(postRequest);
			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
			 return writer.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "";
	}
	
	
	
	public static String callApi(String path) {
		byte[] retBytes = new byte[0];
		try {
//			HttpClient httpClient = IwReqRspHelper.getAApacheHttpClientThatTrustAllSSL();//new DefaultHttpClient();
//			HttpGet getRequest = new HttpGet(path);
//			HttpResponse response = httpClient.execute(getRequest);
//			StringWriter writer = new StringWriter();
//			IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
            retBytes = IOUtils.toByteArray(conn);
            return new String(retBytes, "UTF-8");
//			 return writer.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	public static String callHttpsApi(String path) {
		byte[] retBytes = new byte[0];
		try {
			HttpGet getRequest = new HttpGet(path);
			HttpResponse response = IwReqRspHelper.getAApacheHttpClientThatTrustAllSSL().execute(getRequest);
			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
//			URLConnection conn = new URL(path).openConnection();
//			conn.setConnectTimeout(TIMEOUT_IN_MS);
//			conn.setReadTimeout(TIMEOUT_IN_MS);
//            retBytes = IOUtils.toByteArray(conn);
//            return new String(retBytes, "UTF-8");
			 return writer.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	public static String getJson(String Content,String ID){
		String resultStr="";
		try{
			if(Content.substring(0,1)!="["){
				Content="["+Content+"]";
			}
			
			JSONArray jsonArray=new JSONArray(Content);
			resultStr=jsonArray.getJSONObject(0).get(ID).toString();
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultStr;
	}
}
