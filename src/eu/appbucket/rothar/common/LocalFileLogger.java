package eu.appbucket.rothar.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class LocalFileLogger {
	
	//private static String LOG_TAG = "LocalFileLogger";
	private static final String LOG_FILE = "log-"+ buildFileTimeStamp() + ".txt";
	private static final File logFile = new File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), LOG_FILE);
	
	public static void e(String tag, String message) {
		log("ERROR", tag, message);
	}
	
	public static void d(String tag, String message) {
		log("DEBUG", tag, message);
	}
	
	private static void log(String level, String tag, String message) {
		BufferedWriter logFileWriter = null;
		try {
			logFileWriter = new BufferedWriter(new FileWriter(logFile, true));
			logFileWriter.append(buildLogMessage(level, tag, message));
			logFileWriter.newLine();
			logFileWriter.flush();
		} catch (IOException e) {
			//Log.e(LOG_TAG, "Can't write to file: " + e.getMessage());
		} finally {
			try {
				if(logFileWriter != null) {
					logFileWriter.close();	
				}
			} catch (IOException e) {
				//Log.e(LOG_TAG, "Can't close to file: " + e.getMessage());
			}
		}
	}
	
	private static String buildLogMessage(String level, String tag, String message) {
		StringBuilder log = new StringBuilder(buildLogTimeStamp());
		log.append(" " + level + " ");
		log.append("[" + tag + "]");
		log.append(" - " + message);
		return log.toString();
	}
	
	private static String buildLogTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.UK);
		return formatter.format(new Date());
	}
	
	private static String buildFileTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
		return formatter.format(new Date());
	}
}
