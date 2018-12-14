package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import config.Settings;

public class UtilsInsertMethod {
	public static void insertGetDriverMethod(String packageName, String className) {
		String fileName;
		String middleName = "src" + Settings.sep + "main" + Settings.sep + "resources";
		if(packageName != null || packageName != "") {
			fileName = System.getProperty("user.dir") + Settings.sep + middleName + Settings.sep + packageName + Settings.sep + className + ".java";
		}
		else {
			fileName = className;
		}
			
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
			String writeStr = "\n\t" +
					"public WebDriver getDriver() {\n" +
					"\t\treturn this.driver;\n" +
					"\t}\n";
			long originLen = randomAccessFile.length();
			byte[] b = writeStr.getBytes();
			randomAccessFile.setLength(randomAccessFile.length() + b.length);
			for(long i = randomAccessFile.length() - 1; i > b.length; i--) {
				randomAccessFile.seek(i - b.length);
				byte temp = randomAccessFile.readByte();
				randomAccessFile.seek(i);
				randomAccessFile.writeByte(temp);
			}
			randomAccessFile.seek(originLen - 1);
			randomAccessFile.write(b);
			randomAccessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Writing getDriver() method in class " + fileName.replace(Settings.sep, ".") + " has problem!");
			System.exit(1);
		}
	}
}
