package ezscaner.uniview.app.ezscan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册常用规则
 * 
 * @author dongliang
 * @date 2015-1-20
 * @version 1.0
 */
public class AbInputRules {
	
	/**
	 * 判断是否包含正整数
	 * 
	 * @param str
	 * @return
	 */

	public static Boolean isContainNumber(String str) {
		Boolean bIsContainNumber = false;
		try {
			Pattern p = Pattern.compile(".*\\d+.*");
			Matcher m = p.matcher(str);
			bIsContainNumber = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bIsContainNumber;
	}
	
	/**
	 * 判断是否包含字母
	 * 
	 * @param str
	 * @return
	 */

	public static Boolean isContainLetter(String str) {
		Boolean bIsContainLetter = false;
		try {
			Pattern p = Pattern.compile(".*[A-Za-z]+.*");
			Matcher m = p.matcher(str);
			bIsContainLetter = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bIsContainLetter;
	}

	/**
	 * 判断输入的手机号是否正确
	 * 长度64.
	 * @param mobile
	 * @return
	 */
	public static Boolean isMobileNumber(String mobile) {
		Boolean bIsMobileNo = false;
		try {
			//Pattern p = Pattern.compile("^(13[0-9]|14[5|7]|15[^4]|17[0|6|7|8]|18[0-9]|169)\\d{8}$");
			Pattern p = Pattern.compile("^[1][34578][0-9]{9}$");
			Matcher m = p.matcher(mobile);
			bIsMobileNo = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bIsMobileNo;
	}

	/**
	 * 是否为正确的密码格式
	 * 6~20个大小写英文字母、数字、符号，不能全为字母或数字
	 * 符号指键盘可输入字符
	 * @param password
	 * @return
	 */
	public static Boolean isCorectPassWord(String password) {
		Boolean bIsPassword = false;
		try {
			Pattern p = Pattern.compile("(?=^.{6,20}$)((?=.*\\d)(?=.*[A-Za-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])|(?=.*[^A-Za-z0-9]))^.*");
			Matcher m = p.matcher(password);
			bIsPassword = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bIsPassword;
	}
	
	/**
	 * 用户名 命名规范
	 * 1~20个大小写英文字母、数字、下划线，必须包含英文字符
	 * 中文算两个字符
	 * @param sUserName
	 * @return
	 */
	public static Boolean isUsername(String sUserName) {
		Boolean bIsUserName = false;
		try {
			Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![_]+$)(?![0-9_]+$)[0-9A-Za-z_]{1,20}$");
			Matcher matcher = pattern.matcher(sUserName);
			bIsUserName = matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bIsUserName;
	}
	
	/**
	 * 描述：是否是邮箱.
	 * 
	 * @param str
	 *            指定的字符串
	 * @return 是否是邮箱:是为true，否则false
	 */
	public static Boolean isEmail(String str) {
		Boolean bIsEmail = false;
		//String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String expr = "^(?=\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$).{0,64}$";
		if (str.matches(expr)) {
			bIsEmail = true;
		}
		return bIsEmail;
	}

	/**
	 * 判断端口号是否合法
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isMatchhportSize(int port) {

		if (port > 65535 || port < 0) {
			return true;
		}
		return false;
	}

	/**
	 * 检测注册码的合法性 是字母、数字组合。并且长度在0~30 Registration code
	 * 
	 * @param str
	 * @return
	 */
	public static Boolean isRegistratioCode(String str) {
		String expr = "^[A-Za-z0-9]+$";
		if (str.matches(expr) && judgeInputLength(str, 0, 30) == false) {
			return true;
		}
		return false;
	}

	/**
	 * 检测ip的合法性
	 * 
	 * @param str
	 * @return
	 */
	public static Boolean isIpAddress(String str) {
		String expr = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
		if (str.matches(expr)) {
			return true;
		}
		return false;
	}
	

	/**
	 * 判断当前输入字符串是否在规定范围内
	 * 
	 * @param input
	 * @param minLength
	 *            最小值
	 * @param maxLength
	 *            最大值
	 * @return
	 */
	public static boolean judgeInputLength(String input, int minLength,
			int maxLength) {
		// 返回true为不符合要求
		int length = input.length();
		if (length < minLength || length > maxLength) {
			return true;
		}
		return false;
	}

}
