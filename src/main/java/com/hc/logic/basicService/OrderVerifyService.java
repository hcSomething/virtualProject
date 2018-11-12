package com.hc.logic.basicService;


public class OrderVerifyService {

	
	
	
	/**
	 * 用于有一个参数的命令。无论这个参数是什么
	 * @param args
	 * @return
	 */
	public static boolean onePara(String[] args) {
		if(args.length !=2 ) return false;
		return true;
	}
	
	/**
	 * 用于有两个字符串参数的命令，如注册、登陆
	 * @param args
	 * @return
	 */
	public static boolean twoString(String[] args) {
		if(args.length < 3 || args.length > 3) return false;
		return true;
	}
	
	/**
	 * 用于两个参数都是数字的命令
	 * @param args
	 * @return
	 */
	public static boolean twoInt(String[] args) {
		if(args.length < 3 || args.length > 3) return false;
		if(!isDigit(args[1])) return false;
		if(!isDigit(args[2])) return false;
		if(!validInt(args[1]) && !validInt(args[2])) return false;
		return true;
	}
	
	/**
	 * 用于只有一个数字参数的命令
	 * @param args
	 * @return
	 */
	public static boolean ontInt(String[] args) {
		if(args.length < 2 || args.length > 2) return false;
		if(!isDigit(args[1])) return false;
		if(!validInt(args[1])) return false;
		return true;
	}

	/**
	 * 用于没有参数的命令
	 * @param args
	 * @return
	 */
	public static boolean noPara(String[] args) {
		if(args.length > 1) return false;
		return true;
	}
	
	/**
	 * 判断一个字符串是否都由数字组成
	 * @param s
	 * @return
	 */
	public static boolean isDigit(String s) {
		for(int i = s.length(); --i >=0;) {
			if(!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		if(!validInt(s)) return false;
		return true;
	}
	
	/**
	 * 用于三个参数的命令， 无论参数是什么
	 * @param args
	 * @return
	 */
	public static boolean threePara(String[] args) {
		if(args.length < 3) return false;
		return true;
	}
	
	public static boolean validInt(String a) {
		if(a.length() > 9)
			return false;
		return true;
	}
}
