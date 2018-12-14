package aspects;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.opencv.core.Core;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import config.Settings;
import utils.UtilsAspect;
import utils.UtilsComputerVision;
import utils.UtilsParser;

public aspect VisualTraceGenerator {
	
	static WebDriver d;
	static String testFolder;
	static String mainPage;
	
	/* statement information. */
	static String statementName;
	static int line;
	
	/* visual information. */
	static String screenshotBefore;
	static String annotatedScreenshot;
	static String visualLocator;
	
	/* DOM inforamtion. */
	static String htmlPath;
	static String domInfoJsonFile;
	
	/* OpenCV bindings. */
	static {
		try {
//			nu.pattern.OpenCV.loadShared();
//			nu.pattern.OpenCV.loadLocally();
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/* Pointcuts definition. */
	
	/* intercept the calls to findElement methods. */
	pointcut logFindElementCalls() : call(* org.openqa.selenium.WebDriver.findElement(..));
	
	/* intercept the executions of findElement methods. */
	pointcut catchFindElementExecutions() : execution(* org.openqa.selenium.WebDriver.findElement(..));
	
	/* intercept the calls to WebElement methods. */
	pointcut logSeleniumCommands() : call(* org.openqa.selenium.WebElement.click()) || 
									call(* org.openqa.selenium.WebElement.sendKeys(..)) ||
									call(* org.openqa.selenium.WebElement.getText()) ||
									call(* org.openqa.selenium.WebElement.clear()) ||
									call(* org.openqa.selenium.support.ui.Select.selectByVisibleText(..));
	
	/* create output folders before calling the method. */
	before() : logFindElementCalls() {
//	before() : catchFindElementExecutions() {
		if (Settings.aspectActive) {
			System.out.println("execute logFindElementCalls...");

			/*
			 * IMPORTANT: it is NOT possible to capture web element in this aspect, lead to
			 * infinite recursive calls.
			 */

			d = (WebDriver) thisJoinPoint.getTarget();

			String withinType = thisJoinPoint.getStaticPart().getSourceLocation().getWithinType().toString();
			String testSuiteName = UtilsParser.getTestSuiteNameFromWithinType(withinType);

			UtilsAspect.createTestFolder(Settings.outputDir + testSuiteName);

			testFolder = Settings.outputDir + testSuiteName + Settings.sep
					+ thisJoinPoint.getStaticPart().getSourceLocation().getFileName().replace(Settings.JAVA_EXT, "");

			UtilsAspect.createTestFolder(testFolder);

		}
	}
	
	/* save DOM and visual information before executing the method. */
	before() : logSeleniumCommands() {
		if (Settings.aspectActive) {

			WebElement we = null;
			Select sel = null;

			if (thisJoinPoint.getTarget() instanceof WebElement) {
				we = (WebElement) thisJoinPoint.getTarget();
			} else if (thisJoinPoint.getTarget() instanceof Select) {
				sel = (Select) thisJoinPoint.getTarget();
				we = (WebElement) sel.getOptions().get(0);
			}

			statementName = UtilsAspect.getStatementNameFromJoinPoint(thisJoinPoint);

			line = UtilsAspect.getStatementLineFromJoinPoint(thisJoinPoint);

			screenshotBefore = testFolder + Settings.sep + line + "-1before-" + statementName + Settings.PNG_EXT;
			
			System.out.println(screenshotBefore.toString());

			annotatedScreenshot = testFolder + Settings.sep + line + "-Annotated-" + statementName + Settings.PNG_EXT;

			visualLocator = testFolder + Settings.sep + line + "-visualLocator-" + statementName + Settings.PNG_EXT;

			htmlPath = testFolder + Settings.sep + line + "-1before-" + statementName;

			domInfoJsonFile = testFolder + Settings.sep + line + "-domInfo-" + statementName + Settings.JSON_EXT;

			mainPage = d.getWindowHandle();

			UtilsComputerVision.saveScreenshot(d, screenshotBefore);

			try {

				UtilsComputerVision.saveVisualLocator(d, screenshotBefore, we, visualLocator);

				UtilsComputerVision.saveAnnotatedScreenshot(screenshotBefore, visualLocator, annotatedScreenshot);

				UtilsAspect.saveHTMLPage(d.getCurrentUrl(), htmlPath);

				UtilsParser.saveDOMInformation(d, we, domInfoJsonFile);

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (Settings.VERBOSE)
				System.out.println("[LOG]\t@Before " + statementName);

		}
	}
}
