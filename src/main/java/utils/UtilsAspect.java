package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.openqa.selenium.WebDriver;

import config.Settings;

public class UtilsAspect {

	/**
	 * return an identifier for the statement in the form <testname>-<line> from a
	 * joinPoint of type WebElement
	 * 
	 * @param joinPoint
	 * @return String
	 */
	public static String getStatementNameFromJoinPoint(JoinPoint joinPoint) {

		String name = "";

		name = joinPoint.getStaticPart().getSourceLocation().getFileName().replace(".java", "");
		name = name.concat("-");
		name = name.concat(Integer.toString(joinPoint.getStaticPart().getSourceLocation().getLine()));

		return name;
	}

	/**
	 * return the statement line from a joinPoint of type WebElement
	 * 
	 * @param joinPoint
	 * @return int
	 */
	public static int getStatementLineFromJoinPoint(JoinPoint joinPoint) {
		return joinPoint.getStaticPart().getSourceLocation().getLine();
	}

	/**
	 * creates a directory in the project workspace
	 * 
	 * @param joinPoint
	 * @return int
	 */
	public static void createTestFolder(String path) {

		File theDir = new File(path);
		if (!theDir.exists()) {

			if (Settings.VERBOSE)
				System.out.print("[LOG]\tcreating directory " + path + "...");

			boolean result = theDir.mkdirs();
			if (result) {
				if (Settings.VERBOSE)
					System.out.println("done");
			} else {
				if (Settings.VERBOSE)
					System.out.print("failed!");
				System.exit(1);
			}
		}

	}

	/**
	 * save an HTML file of the a WebDriver instance
	 * 
	 * @param d
	 * @param filePath
	 */
	public static void saveDOM(WebDriver d, String filePath) {

		try {
			FileUtils.writeStringToFile(new File(filePath), d.getPageSource());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Save rendered webpage path = where to save the html file
	 */
	public static File saveHTMLPage(String urlString, String path) throws IOException {

		File savedHTML = new File(path);

		/* necessary to avoid garbage. */
		if (savedHTML.exists()) {
			FileUtils.deleteDirectory(savedHTML);
		}

		/* wget to save html page. */
		Runtime runtime = Runtime.getRuntime();
		Process p = runtime.exec("E:/learn/vista/Ryan Addition/wget-1.19.4-win64/wget -p -k -E -nd -P " + path + " " + urlString);
		
		// handler the pitfall of runtime.exec()
		// Ryan 2018-11-27 reference: https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERR");
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUT");
		errorGobbler.start();
		outputGobbler.start();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}

		return savedHTML;
	}

}
