package com.aaron.function;

public interface Function {

	/**
	 * 执行函数
	 * 
	 * @param args
	 * @return
	 */
	String execute(String[] args);

	/**
	 * 函数key值，全局唯一
	 * 
	 * @return
	 */
	String getFunctionKey();
}
