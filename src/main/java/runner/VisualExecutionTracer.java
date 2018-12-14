package runner;

import java.io.IOException;
import java.util.Set;

import org.reflections.*;

import config.Settings;
import utils.UtilsRunner;

/**
 * The VisualExecutionTracer class runs a JUnit Selenium test suites and
 * collects the DOM and GUI information pertaining to each statement
 * 
 * @author astocco
 * @author yrahulkr
 *
 */
public class VisualExecutionTracer {

	public static void main(String[] args) throws IOException {

		/* enable the AspectJ module. */
		Settings.aspectActive = true;

		/* Claroline example. */
		/* Run all testcases in the testsuite */
//		Reflections reflections = new Reflections("src.addressbook");
//		Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
//		for(Class<? extends Object> c: allClasses) {
//			System.out.println(c);
//		}
		
		UtilsRunner.runTest(Settings.testSuiteCorrect, "TextElementDelete");

	}

}
