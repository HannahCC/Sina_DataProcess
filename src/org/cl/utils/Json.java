package org.cl.utils;

import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;

public class Json {
	public static JSONObject string2Json(String str){
		JSONObject json = null;
		try{
			json = JSONObject.fromObject(str);
		} catch (Exception e){
			e.printStackTrace();
		}
		return json;
	}

	public static String json2String(JSONObject json){
		JSONStringer js = new JSONStringer();
		try{
			js.object();
			@SuppressWarnings("unchecked")
			Set<String> key_set = json.keySet();
			for(String key : key_set){
				js.key(key);
				js.value(json.get(key));
			}
			js.endObject();
		}catch (Exception e){
			e.printStackTrace();
		}
		return js.toString();
	}



	public static void main(String args[]){
		JSONObject json = new JSONObject();
		json.put("name", "test");
		json.put("gender","female");
		json.put("num", 15);

		JSONObject json1 = new JSONObject();
		json1.put("kemu", "C++");
		json1.put("mark",96);
		JSONObject json2 = new JSONObject();
		json2.put("kemu", "java");
		json2.put("mark",97);
		JSONArray jsonarray = new JSONArray();
		jsonarray.add(json1);
		jsonarray.add(json2);


		JSONObject bjson = new JSONObject();
		bjson.put("user", json);
		bjson.put("menshu", 2);
		bjson.put("kecheng", jsonarray);
		String str = json2String(bjson);
		
		JSONObject obj = string2Json(str);
		
		System.out.println(obj.get("user").toString());
		System.out.println(obj.get("menshu").toString());
		System.out.println(obj.get("kecheng").toString());
	}
}
