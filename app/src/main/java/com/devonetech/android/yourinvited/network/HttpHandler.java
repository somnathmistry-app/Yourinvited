package com.devonetech.android.yourinvited.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

//sslim


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HttpHandler {

	static InputStream is = null;
	static JSONArray jArray = null;
	static JSONObject jObj = null;
	static String json = "";

	public static HttpClient createHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		return new DefaultHttpClient(conMgr, params);
	}

	// constructor
	public HttpHandler() {

	}

	public JSONArray getJSONArray(String url) {
		// try parse the string to a JSON array

		getJSONStringFromUrl(url);

		try {
			Log.v("------JSONArray-----", "In Try");
			jArray = new JSONArray(json);


		} catch (JSONException e) {
			Log.e("JSON Parser Array",
					"Error parsing jsonArray " + e.toString());


		}

		// return JSON String
		return jArray;

	}

	public JSONObject getJSONObject(String url) {
		// try parse the string to a JSON object

		getJSONStringFromUrl(url);
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser Obj-> ",
					"Error parsing jsonObject " + e.toString());
		}

		// return JSON String
		return jObj;


	}

	public String getJSONStringFromUrl(String url) {

		// Making HTTP request
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();

			StrictMode.setThreadPolicy(policy);
			// defaultHttpClient
			/*DefaultHttpClient httpClient = new DefaultHttpClient();*/
			DefaultHttpClient httpClient = new DefaultHttpClient();

			//HttpClient httpClient = createHttpClient();
			//HttpClient httpClient =getNewHttpClient();

			HttpPost httpPost = new HttpPost(url);
			/*HttpGet httpPost = new HttpGet(url);*/

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);

			/*
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							is, "utf-8"), 8);*/
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error ---->", "Error converting result " + e.toString());
		}

		// return JSON String
		Log.i("RETURN JSON :---- > ",json);
		return json;

	}




	public String getJSONStringFromUrl(String url,Map<String, String> map) {

		// Making HTTP request
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();

			StrictMode.setThreadPolicy(policy);


			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				nameValuePair.add(new BasicNameValuePair(key, map.get(key)));
			}
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			//httpClient.getConnectionManager().getSchemeRegistry().register(‌​new Scheme("https",  SSLSocketFactory.getSocketFactory(),443));
			
			
			
			//HttpGet httpPost = new HttpGet(url);

			//HttpClient httpClient = createHttpClient();
			
			
			//HttpClient httpClient =getNewHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());

		}

		// return JSON String
		Log.i("RETURN JSON: ",json);
		return json;

	}



	/*public static HttpClient getTestHttpClient() {
	    try {
	        SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy(){
	            @Override
	            public boolean isTrusted(X509Certificate[] chain,
	                    String authType) throws CertificateException {
	                return true;
	            }
	        }, new AllowAllHostnameVerifier());

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("https",443, sf));
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);
	        return new DefaultHttpClient(ccm);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new DefaultHttpClient();
	    }
	}*/
	
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
	        
	     

	        SSLSocketFactory sf = new SSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
}
