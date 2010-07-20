/*
 **TestMethod.java
 */
package com.redhorse.netfox;

public class TestMethod {
	public TestMethod() { // /xx/weblogic60b2_win.exe
		try {
//			SiteInfoBean bean = new SiteInfoBean("http://localhost/www.zip",
//					"C:\\temp", "www.zip", 5);
			// SiteInfoBean bean = new
			// SiteInfoBean("http://localhost:8080/down.zip","L:\\temp","weblogic60b2_win.exe",5);
//			SiteFileFetch fileFetch = new SiteFileFetch(bean);
//			fileFetch.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestMethod();
	}
}