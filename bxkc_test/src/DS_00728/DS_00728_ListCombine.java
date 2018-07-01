package DS_00728;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.Utils;

public class DS_00728_ListCombine extends TxtReqRspHandler {
	String url;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}

	public class BranchNew {
		String id;
		String title;
		String date;
		String url;

		@Override
		public String toString() {
			return "BranchNew [id=" + id + ", title=" + title + ", date=" + date + "]";
		}

	}

	@Override
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		// TODO Auto-generated method stub
		IwResponse iwRsp = null;
		try {
			String CategoryNum = iwRequest.getRequestPathParam("CategoryNum");
			String iw_ir_2 = "";
			if (CategoryNum.length() == 12) {
				iw_ir_2 = CategoryNum.substring(0, 6) + "/" + CategoryNum.substring(0, 9) + "/" + CategoryNum;
			}else if (CategoryNum.length() == 9){
				iw_ir_2 = CategoryNum.substring(0, 6) + "/" + CategoryNum;
			}
			String sdParamsUrl = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr()
					+ "&iw-cmd=DS_00728_Params&CategoryNum=" + CategoryNum + "&iw_ir_2=" + iw_ir_2;
			String sdParamsList = Utils.callApi(sdParamsUrl);

			JSONObject jsonobject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonobject.get("params");
			String __VIEWSTATE = params.getString("__VIEWSTATE");
			String __EVENTARGUMENT = iwRequest.getRequestContentParam("__EVENTARGUMENT");
			String listCombine = getNewPathPrefix() + "/?" + getAdditionalLinkParamStr() + "&iw-cmd=DS_00728_List"
					+ "&CategoryNum=" + CategoryNum + "&iw_ir_2=" + iw_ir_2 + "&__VIEWSTATE=" + __VIEWSTATE
					+ "&__EVENTARGUMENT=" + __EVENTARGUMENT;

			String result = Utils.callApi(listCombine);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iwRsp;
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtResContent) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtResContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Gson gson = new Gson();
				Response result = gson.fromJson(originTxtResContent, Response.class);
				response.list = result.list;
				response.pageCount = result.pageCount;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
