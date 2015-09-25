package org.cl.test;
import java.util.HashMap;
import java.util.Map;

import org.cl.utils.Utils;

public class testUtils {
	public static void fullarray(){
		int[] group_size = {2,3,2};
		int[][] fullarray = Utils.recursion_getFullArray(group_size);
		for(int[] array : fullarray){
			for(int a : array){
				System.out.print(a+",");
			}
			System.out.println();
		}
		System.out.println(Utils.array_ToString(group_size));
	}
	public static void map(){
		Map<String,String> map = new HashMap<String,String>();
		System.out.println(map.get("1"));
	}
	public static void main(String args[]){
		map();
	}
}
