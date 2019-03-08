package com.aaron.function;

import com.aaron.util.RandomUtil;

public class RandomFunction implements Function {

	@Override
	public String execute(String[] args) {
		int len = args.length;
		int length = 6;
		boolean flag = false;
		if (len > 0) {
			length = Integer.valueOf(args[0]);
		}
		if (len > 1) {
			flag = Boolean.valueOf(args[1]);
		}
		return RandomUtil.getRandom(length, flag);
	}

	@Override
	public String getFunctionKey() {
		return "random";
	}

}
