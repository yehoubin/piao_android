package com.wenbo.piao.task;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.Fragment.RobitOrderFragment;
import com.wenbo.piao.domain.UserInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 获取账号联系人
 * @author wenbo
 *
 */
public class GetPersonConstanct extends AsyncTask<String,Integer,String>{
	
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	private Map<String,UserInfo> userInfoMap;	
	
	private RobitOrderFragment robitOrderFragment;
	
	public GetPersonConstanct(Activity activity,Map<String,UserInfo> userInfoMap
			,RobitOrderFragment robitOrderFragment){
		this.activity = activity;
		this.userInfoMap = userInfoMap;
		this.robitOrderFragment = robitOrderFragment;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		if(userInfoMap.isEmpty()){
			String info = getOrderPerson();
	    	JSONObject jsonObject = JSON.parseObject(info);
	    	List<UserInfo>  userInfos = JSONArray.parseArray(
					jsonObject.getString("rows"), UserInfo.class);
			if (userInfos != null && !userInfos.isEmpty()) {
				UserInfo userInfo = null;
				for (int i = 0; i < userInfos.size(); i++) {
					userInfo = userInfos.get(i);
					if (userInfo != null) {
						userInfo.setIndex(i);
						userInfoMap.put(userInfo.getPassenger_name(), userInfo);
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		robitOrderFragment.showDialog();
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		 progressDialog = ProgressDialog.show(activity,"获取联系人","正在获取联系人...",true,false);
		 super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
	/**
	 * 获取登录账号用户信息
	 * 
	 * @throws URISyntaxException
	 */
	private String getOrderPerson() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(uri,
					UrlEnum.GET_ORDER_PERSON);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "getPagePassengerAll"));
			parameters.add(new BasicNameValuePair("pageIndex",
					"0"));
			parameters.add(new BasicNameValuePair("pageSize",
					"100"));
			parameters.add(new BasicNameValuePair("passenger_name",
					"请输入汉字或拼音首字母"));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			Log.e("GetPersonConstanct","getOrderPerson error!",e);
		} finally {
			Log.i("GetPersonConstanct","close getOrderPerson");
		}
		return null;
	}

}