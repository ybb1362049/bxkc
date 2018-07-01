package DX000604;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class DX000604_List extends TxtRspHandler {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
	String parms[] = new String[] { "id", "columnId", "method", "page", "start" };

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String nextPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response = new Response();
		if (arg0 == RspState.Login) {
			try {
				Document xml = Jsoup.parse(arg1);
				Elements as = xml.select("div.n_list_r_bt>a");
				String regex = "'.*?'";
				Pattern pattern = Pattern.compile(regex);
				for (Element a : as) {
					BranchNew bn = new BranchNew();
					bn.title = a.text().trim();
					Matcher matcher = pattern.matcher(a.attr("href"));
					String id = "";
					int i = 0;
					while (matcher.find()) {
						id += ("&" + parms[i] + "=" + matcher.group().replaceAll("'", ""));
						i++;
					}
					bn.id = id;
					response.list.add(bn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
