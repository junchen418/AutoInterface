package com.aaron.function;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aaron.util.StringUtil;

public class DateFunction implements Function {

	@Override
	public String execute(String[] args) {
		if (args.length == 0 || StringUtil.isEmpty(args[0])) {
			return String.format("%s", new Date().getTime());
		} else {
			SimpleDateFormat format = new SimpleDateFormat(args[0]);
			return format.format(new Date());
		}
	}

	@Override
	public String getFunctionKey() {
		return "date";
	}

}
