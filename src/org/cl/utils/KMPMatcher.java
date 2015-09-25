package org.cl.utils;

public class KMPMatcher {
	public static void main(String[] args) {  
		String s = "abbabbcabbbbbcab"; // 主串  
		String t = "bbaab"; // 模式串  
		System.out.println(KMP_Index(s, t)); // KMP匹配字符串  
	}  

	/** 
	 * 获得字符串的next函数值  
	 * @param t  字符串 
	 * @return next函数值 
	 */  
	public static int[] next(char[] t) {  
		int[] next = new int[t.length];  
		next[0] = -1;  
		int i = 0;  
		int j = -1;  
		while (i < t.length - 1) {  
			if (j == -1 || t[i] == t[j]) {  
				i++;  
				j++;  
				if (t[i] != t[j]) {  
					next[i] = j;  
				} else {  
					next[i] = next[j];  
				}  
			} else {  
				j = next[j];  
			}  
		}  
		return next;  
	}  

	/** 
	 * KMP匹配字符串 
	 * @param ss 主串 
	 * @param tt 模式串 
	 * @return 若匹配成功，返回下标，否则返回-1 
	 */  
	public static int KMP_Index(String ss, String tt) {
		char[] s = ss.toCharArray();  
		char[] t = tt.toCharArray();  
		int[] next = next(t);  
		int i = 0;  
		int j = 0;  
		while (i <= s.length - 1 && j <= t.length - 1) {  
			if (j == -1 || s[i] == t[j]) {  
				i++;  
				j++;  
			} else {  
				j = next[j];  
			}  
		}  
		if (j < t.length) {  
			return -1;  
		} else  
			return i - t.length; // 返回模式串在主串中的头下标  
	}  

}
