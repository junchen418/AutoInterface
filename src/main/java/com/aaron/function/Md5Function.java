package com.aaron.function;

import com.aaron.encrypt.MD5Util;

public class Md5Function implements Function {

	@Override
	public String execute(String[] args) {
		return MD5Util.MD5(args[0]);
	}

	@Override
	public String getFunctionKey() {
		return "md5";
	}

}
