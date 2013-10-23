package com.grit.GILib.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

/**
 * A Debugging utility with the following features:<br>
 * <ul>
 * <li>The usual features provided by {@link Log} class wrapped around by <b>
 * {@link LogMode}</b>.</li>
 * <li><b>Method Entry-Exit logs</b>: Can be turned off by a switch</li>
 * <li><b>Selective Debugging</b>: Debug specific classes.</li>
 * <li><b>Method Execution-Time Measurement</b>: Measure Execution time for
 * individual methods as well as collective time spent on all methods of a
 * class.</li>
 * </ul>
 * 
 * @author vinaywadhwa
 * 
 */

public class SmartLog {
	/**
	 * This holds the info about the current {@link LogMode}.
	 */
	private static final LogMode currentLogMode = LogMode.DEBUG_UNTOUCHED;
	/**
	 * If this is set to false, Entry-Exit logs, which are printed by<br>
	 * {@link #entry_log()},{@link #entry_log(String)},{@link #exit_log()} or <br>
	 * {@link #exit_log(String)}, are filtered out.
	 */
	private static final boolean isEntryExitLogsEnabled = true;
	/**
	 * If this is set, the calls to {@link #d}, {@link #i}, {@link #v} or
	 * {@link #e} are filtered out <br>
	 * if the TAG passed to these methods is not present in {@link #tagsToDebug}
	 * .
	 */
	private static final boolean isSelectiveDebuggingEnabled = false;
	/**
	 * The calls to {@link #d}, {@link #i}, {@link #v} or {@link #e} with
	 * <i>TAGs</i> which are not present in this list are ignored,if
	 * {@link #isSelectiveDebuggingEnabled}.
	 */
	@SuppressWarnings("serial")
	private static final List<String> tagsToDebug = new ArrayList<String>() {
		{
			// --add the tags you want to debug.
			// add("SampleClassName");
			// add("SampleActivityName");
			// add("SampleTAGName");
		}
	};

	/**
	 * The LogModes you want your app to Log your messages in.<br>
	 * The different Log modes vary the response behavior towards the calls to
	 * Logging methods.<br>
	 * Following are the LogModes
	 * <ul>
	 * <li>{@link #RELEASE}</li>
	 * <li>{@link #DEBUG_PASSIVE}</li>
	 * <li>{@link #DEBUG_UNTOUCHED}</li>
	 * <li>{@link #DEBUG_AGGRESSIVE}</li>
	 * <li>{@link #PERFORMANCE_ANALYSIS}</li>
	 * </ul>
	 * 
	 * @author vinaywadhwa
	 * 
	 */
	private enum LogMode {
		/**
		 * Release mode is the mode where the application is being released to
		 * public.<br>
		 * No Logs are printed and no Exceptions are thrown, on purpose, for
		 * debugging.
		 */
		RELEASE,
		/**
		 * In this mode, only important messages,i.e <i>Error</i> &
		 * <i>Warning</i> messages are logged.<br>
		 * <i>Debug</i>, <i>Info</i> & <i>Verbose</i> messages are filtered out
		 * in this mode.<br>
		 * A simple use case for this mode could be the pre-release testing of
		 * an app, <br>
		 * where one might want to focus only on the major errors and warnings.
		 */
		DEBUG_PASSIVE,
		/**
		 * This mode makes this class behave just like Android's {@link Log}. <br>
		 * No Logs are altered/filtered out and are printed just as expected.
		 */
		DEBUG_UNTOUCHED,
		/**
		 * This mode should be used when you want to fix all the errors and
		 * warnings rightaway.<br>
		 * This mode makes the app crash, i.e throws a RuntimeException, <br>
		 * whenever a Warning or Error message is logged.<br>
		 * It prints the Debug,Info & Verbose logs as usual.
		 */
		DEBUG_AGGRESSIVE,
		/**
		 * Under this mode, no logs are printed. Time is measured for execution<br>
		 * of methods and printed on every paired call to <br>
		 * {@link #entry_log()}/{@link #entry_log(String)}- {@link #exit_log()}/
		 * {@link #exit_log(String)}.<br>
		 * To print the total execution time spent on a class, call
		 * {@link #timeSpentOnClass(String)} <br>
		 * <i>Note:</i> Even though this mode uses the {@link #entry_log()}/
		 * {@link #entry_log(String)}- {@link #exit_log()}/
		 * {@link #exit_log(String)} methods, entry-exit logs are not printed.
		 */
		PERFORMANCE_ANALYSIS;

		String TAG_PERFORMANCE_ANALYSIS = "PerformanceAnalysis";

		/**
		 * This map will store the time-stamp of beginning of a method's<br>
		 * execution (marked by the call to {@link #entry_log()}) mapped with
		 * the method name.<br>
		 * The mappings will be used to calculate the total execution time for
		 * method when the execution of the method ends(marked by the call to
		 * {@link #exit_log()})
		 */
		HashMap<String, Long> methodEntryTimestamps = new HashMap<String, Long>();

		/**
		 * Holds the collective execution time spent on a class by all of its
		 * methods.
		 */
		HashMap<String, Long> classExecutionTime = new HashMap<String, Long>();

		/**
		 * Send a DEBUG log message.Does nothing in {@link #RELEASE}and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 */
		public void d(String tag, String message) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.d(tag, message);
				break;

			}
		}

		/**
		 * Send a DEBUG log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode, {@link #RELEASE}and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 * @param throwable
		 *            An exception to log
		 */
		public void d(String tag, String message, Throwable throwable) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.d(tag, message, throwable);
				break;

			}
		}

		/**
		 * Send an INFO log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode, {@link #RELEASE} and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 */
		public void i(String tag, String message) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.i(tag, message);
				break;

			}
		}

		/**
		 * Send an INFO log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode, {@link #RELEASE} and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 * @param throwable
		 *            An exception to log
		 */
		public void i(String tag, String message, Throwable throwable) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.d(tag, message, throwable);
				break;

			}
		}

		/**
		 * Send a VERBOSE log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode, {@link #RELEASE} and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 */
		public void v(String tag, String message) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.v(tag, message);
				break;

			}
		}

		/**
		 * Send a VERBOSE log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode, {@link #RELEASE} and
		 * {@link #DEBUG_PASSIVE}.
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 * @param throwable
		 *            An exception to log
		 */
		public void v(String tag, String message, Throwable throwable) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.v(tag, message, throwable);
				break;

			}
		}

		/**
		 * Send a WARNING log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode and {@link #RELEASE} mode. In
		 * {@link #DEBUG_PASSIVE} mode and {@link #DEBUG_UNTOUCHED} mode, it
		 * prints the warning message as usual. In {@link #DEBUG_AGGRESSIVE}
		 * mode, it throws an exception[RuntimeException].
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 */
		public void w(String tag, String message) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
				break;
			case DEBUG_PASSIVE:
			case DEBUG_UNTOUCHED:
				Log.w(tag, message);
				break;
			case DEBUG_AGGRESSIVE:
				throwException(tag, message);
				break;
			}
		}

		/**
		 * Send a WARNING log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode and {@link #RELEASE} mode. In
		 * {@link #DEBUG_PASSIVE} mode and {@link #DEBUG_UNTOUCHED} mode, it
		 * prints the warning message as usual. In {@link #DEBUG_AGGRESSIVE}
		 * mode, it throws an exception[RuntimeException].
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 * @param throwable
		 *            An exception to log
		 */
		public void w(String tag, String message, Throwable throwable) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
				break;
			case DEBUG_PASSIVE:
			case DEBUG_UNTOUCHED:
				Log.w(tag, message, throwable);
				break;
			case DEBUG_AGGRESSIVE:
				throwException(tag, message, throwable);
			}
		}

		/**
		 * Send a ERROR log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode and {@link #RELEASE} mode. In
		 * {@link #DEBUG_PASSIVE} mode and {@link #DEBUG_UNTOUCHED} mode, it
		 * prints the warning message as usual. In {@link #DEBUG_AGGRESSIVE}
		 * mode, it throws an exception[RuntimeException].
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 */
		public void e(String tag, String message) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
				break;
			case DEBUG_PASSIVE:
			case DEBUG_UNTOUCHED:
				Log.e(tag, message);
				break;

			case DEBUG_AGGRESSIVE:
				throwException(tag, message);

			}
		}

		/**
		 * Send a ERROR log message.Does nothing in
		 * {@link #PERFORMANCE_ANALYSIS} mode and {@link #RELEASE} mode. In
		 * {@link #DEBUG_PASSIVE} mode and {@link #DEBUG_UNTOUCHED} mode, it
		 * prints the warning message as usual. In {@link #DEBUG_AGGRESSIVE}
		 * mode, it throws an exception[RuntimeException].
		 * 
		 * @param tag
		 *            Used to identify the source of a log message. It usually
		 *            identifies the class or activity where the log call
		 *            occurs.
		 * @param message
		 *            The message you would like logged.
		 * @param throwable
		 *            An exception to log
		 */
		public void e(String tag, String message, Throwable throwable) {
			switch (this) {
			case PERFORMANCE_ANALYSIS:
			case RELEASE:
				break;
			case DEBUG_PASSIVE:
			case DEBUG_UNTOUCHED:
				Log.e(tag, message, throwable);
				break;

			case DEBUG_AGGRESSIVE:
				throwException(tag, message, throwable);

			}
		}

		/**
		 * Call this at the first line in the methods you want to debug. It Logs
		 * a simple message using Android's {@link Log#v} with tag <i>ENTRY</i>
		 * in the format: <br>
		 * <b> &lt class_name &gt . &lt method_name &gt : &lt line_number &gt
		 * </b>.<br>
		 * The output of this method is filtered out in {@link #RELEASE} mode
		 * and {@link #DEBUG_PASSIVE} mode.<br>
		 * <br>
		 * <i>TIP:</i> Pair the calls to this method with {@link #exit_log()}/
		 * {@link #exit_log(String)} for Performance analytics to work in
		 * {@link #PERFORMANCE_ANALYSIS} mode.
		 */
		public void entry_log() {
			Throwable stack = new Throwable().fillInStackTrace();
			StackTraceElement[] trace = stack.getStackTrace();
			switch (this) {
			case PERFORMANCE_ANALYSIS:
				methodEntryTimestamps.put(trace[2].getMethodName(),
						System.currentTimeMillis());
				break;
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:

				Log.v("ENTRY",
						trace[2].getClassName() + "."
								+ trace[2].getMethodName() + ":"
								+ trace[2].getLineNumber());
				break;

			}
		}

		/**
		 * Call this at the last line in the methods you want to debug. It Logs
		 * a simple message using Android's {@link Log#v} with tag <i>EXIT</i>
		 * in the format: <br>
		 * <b> &lt class_name &gt . &lt method_name &gt : &lt line_number &gt
		 * </b>.<br>
		 * The output of this method is filtered out in {@link #RELEASE} mode
		 * and {@link #DEBUG_PASSIVE} mode.<br>
		 * <br>
		 * <i>TIP:</i> Pair the calls to this method with {@link #entry_log()}/
		 * {@link #entry_log(String)} for Performance analytics to work in
		 * {@link #PERFORMANCE_ANALYSIS} mode.
		 */
		public void exit_log() {
			Throwable stack = new Throwable().fillInStackTrace();
			StackTraceElement[] trace = stack.getStackTrace();
			switch (this) {
			case PERFORMANCE_ANALYSIS:
				if (!methodEntryTimestamps
						.containsKey(trace[2].getMethodName())) {
					Log.e(TAG_PERFORMANCE_ANALYSIS,
							"entry() not called before exit() in method:"
									+ trace[2].getMethodName());
				} else {
					// print method execution time
					Long timeSpentOnMethod = System.currentTimeMillis()
							- methodEntryTimestamps.get(trace[2]
									.getMethodName());
					Log.i(TAG_PERFORMANCE_ANALYSIS,
							"methodName:" + trace[2].getMethodName()
									+ "|Executed in:" + timeSpentOnMethod
									+ " ms");

					// update class execution time total
					Long timeSpentOnClass = classExecutionTime
							.containsKey(trace[2].getClassName()) ? classExecutionTime
							.get(trace[2].getClassName()) : 0;
					timeSpentOnClass += timeSpentOnMethod;
					classExecutionTime.put(trace[2].getClassName(),
							timeSpentOnClass);
				}
				break;
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:

				Log.v("EXIT",
						trace[2].getClassName() + "."
								+ trace[2].getMethodName() + ":"
								+ trace[2].getLineNumber());
				break;

			}
		}

		/**
		 * Call this at the first line in the methods you want to debug. It Logs
		 * a simple message using Android's {@link Log#v} with tag <i>ENTRY</i>
		 * in the format: <b> &lt id &gt - &lt class_name &gt . &lt method_name
		 * &gt : &lt line_number &gt </b>.<br>
		 * The output of this method is filtered out in {@link #RELEASE} mode
		 * and {@link #DEBUG_PASSIVE} mode.<br>
		 * <br>
		 * <i>TIP:</i> Pair the calls to this method with {@link #exit_log()}/
		 * {@link #exit_log(String)} for Performance analytics to work in
		 * {@link #PERFORMANCE_ANALYSIS} mode.
		 * 
		 * @param id
		 *            The id in the entry message to be logged in the format <br>
		 *            <b> &lt id &gt - &lt class_name &gt . &lt method_name &gt
		 *            : &lt line_number &gt </b>
		 */
		public void entry_log(String id) {
			Throwable stack = new Throwable().fillInStackTrace();
			StackTraceElement[] trace = stack.getStackTrace();
			switch (this) {
			case PERFORMANCE_ANALYSIS:
				methodEntryTimestamps.put(trace[2].getMethodName(),
						System.currentTimeMillis());
				break;
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:
				Log.v("ENTRY",
						id + " - " + trace[2].getClassName() + "."
								+ trace[2].getMethodName() + ":"
								+ trace[2].getLineNumber());
				break;

			}
		}

		/**
		 * Call this at the last line in the methods you want to debug. It Logs
		 * a simple message using Android's {@link Log#v} with tag <i>EXIT</i>
		 * in the format: <b> &lt id &gt - &lt class_name &gt . &lt method_name
		 * &gt : &lt line_number &gt </b>.<br>
		 * The output of this method is filtered out in {@link #RELEASE} mode
		 * and {@link #DEBUG_PASSIVE} mode.<br>
		 * <br>
		 * <i>TIP:</i> Pair the calls to this method with {@link #entry_log()}/
		 * {@link #entry_log(String)} for Performance analytics to work in
		 * {@link #PERFORMANCE_ANALYSIS} mode.
		 * 
		 * @param id
		 *            The id in the exit message to be logged in the format <br>
		 *            <b> &lt id &gt - &lt class_name &gt . &lt method_name &gt
		 *            : &lt line_number &gt </b>
		 */
		public void exit_log(String id) {
			Throwable stack = new Throwable().fillInStackTrace();
			StackTraceElement[] trace = stack.getStackTrace();

			switch (this) {
			case PERFORMANCE_ANALYSIS:
				if (!methodEntryTimestamps
						.containsKey(trace[2].getMethodName())) {
					Log.e(TAG_PERFORMANCE_ANALYSIS,
							"entry() not called before exit() in method:"
									+ trace[2].getMethodName());
				} else {
					// print method execution time
					Long timeSpentOnMethod = System.currentTimeMillis()
							- methodEntryTimestamps.get(trace[2]
									.getMethodName());
					Log.i(TAG_PERFORMANCE_ANALYSIS,
							"methodName:" + trace[2].getMethodName()
									+ "|Executed in:" + timeSpentOnMethod
									+ " ms");

					// update class execution time total
					Long timeSpentOnClass = classExecutionTime
							.containsKey(trace[2].getClassName()) ? classExecutionTime
							.get(trace[2].getClassName()) : 0;
					timeSpentOnClass += timeSpentOnMethod;
					classExecutionTime.put(trace[2].getClassName(),
							timeSpentOnClass);
				}
				break;
			case RELEASE:
			case DEBUG_PASSIVE:
				break;
			case DEBUG_UNTOUCHED:
			case DEBUG_AGGRESSIVE:

				Log.v("EXIT",
						id + " - " + trace[2].getClassName() + "."
								+ trace[2].getMethodName() + ":"
								+ trace[2].getLineNumber());
				break;

			}
		}

		private void throwException(String tag, String message) {
			throw new RuntimeException("[SelfThrown] Error at '" + tag + "':"
					+ message);
		}

		private void throwException(String tag, String message,
				Throwable throwable) {
			throw new RuntimeException("[SelfThrown] Error at '" + tag + "':"
					+ message + "->" + throwable);
		}

		/**
		 * Returns the total execution time spent on the specified class, if in
		 * {@link #PERFORMANCE_ANALYSIS} mode. Call this at an exit point of the
		 * class to get appropriate/realistic results.
		 * 
		 * @param fullyQualifiedClassName
		 *            Name of the class
		 * @return Total execution time spent on the specified class, if in
		 *         {@link #PERFORMANCE_ANALYSIS} mode. Returns 0 if not in
		 *         {@link #PERFORMANCE_ANALYSIS} mode or {@link #entry_log()}-
		 *         {@link #exit_log()} pairs are not used appropriately.
		 */
		public long timeSpentOnClass(String fullyQualifiedClassName) {
			return classExecutionTime.containsKey(fullyQualifiedClassName) ? classExecutionTime
					.get(fullyQualifiedClassName) : 0;
		}

	}

	/**
	 * * Uses the {@link LogMode#entry_log()} method under a switch -
	 * {@link #isEntryExitLogsEnabled}.
	 */
	public static void entry_log() {
		if (isEntryExitLogsEnabled) {
			currentLogMode.entry_log();
		}
	}

	/**
	 * * Uses the {@link LogMode#exit_log()} method under a switch -
	 * {@link #isEntryExitLogsEnabled}.
	 */
	public static void exit_log() {
		if (isEntryExitLogsEnabled)
			currentLogMode.exit_log();

	}

	/**
	 * Uses the {@link LogModes#entry_log(String)()} method under a switch -
	 * {@link #isEntryExitLogsEnabled}.
	 * 
	 * @param id
	 *            passed to {@link LogModes#entry_log(String)()}.
	 */
	public static void entry_log(String id) {

		if (isEntryExitLogsEnabled)
			currentLogMode.entry_log(id);

	}

	/**
	 * Uses the {@link LogMode#exit_log()} method under a switch -
	 * {@link #isEntryExitLogsEnabled}.
	 * 
	 * @param id
	 *            passed to {@link LogMode#exit_log(String)}.
	 */
	public static void exit_log(String id) {
		if (isEntryExitLogsEnabled)
			currentLogMode.exit_log(id);
	}

	/**
	 * Uses the {@link LogMode#exit_log()} method under a switch -
	 * {@link #isEntryExitLogsEnabled} and returns what is passed to it after
	 * printing the exit log. To be used in methods with return types.<br>
	 * <b>Eg:</b> Use<br><br>
	 * <i>return (String)GILog.exit_log_return("hello");</i> <br>
	 * <br>
	 * instead of <br><br>
	 * <i>GILog.exit();<br>
	 * return "hello";</i>
	 * 
	 * 
	 * @param id
	 *            passed to {@link LogMode#exit_log(String)}.
	 */
	public static Object exit_log_return(Object toReturnBack) {
		if (isEntryExitLogsEnabled)
			currentLogMode.exit_log();
		return toReturnBack;
	}

	/**
	 * Uses {@link LogMode#d(String, String)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#d(String, String)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#d(String, String)}.
	 */
	public static void d(String tag, String message) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.d(tag, message);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.d(tag, message);

	}

	/**
	 * Uses {@link LogMode#i(String, String)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#i(String, String)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#i(String, String)}.
	 */
	public static void i(String tag, String message) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.i(tag, message);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.i(tag, message);
	}

	/**
	 * Uses {@link LogMode#e(String, String)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#e(String, String)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#e(String, String)}.
	 */
	public static void e(String tag, String message) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.e(tag, message);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.e(tag, message);
	}

	/**
	 * Uses {@link LogMode#w(String, String)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#w(String, String)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#w(String, String)}.
	 */
	public static void w(String tag, String message) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.w(tag, message);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.w(tag, message);
	}

	/**
	 * Uses {@link LogMode#v(String, String)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#v(String, String)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#v(String, String)}.
	 */
	public static void v(String tag, String message) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.v(tag, message);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.v(tag, message);
	}

	/**
	 * Uses {@link LogMode#d(String, String,Throwable)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#d(String, String,Throwable)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#d(String, String,Throwable)}.
	 */
	public static void d(String tag, String message, Throwable throwable) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.d(tag, message, throwable);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.d(tag, message, throwable);

	}

	/**
	 * Uses {@link LogMode#i(String, String,Throwable)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#i(String, String,Throwable)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#i(String, String,Throwable)}.
	 * @param throwable
	 *            To be passed as a parameter to
	 *            {@link LogMode#i(String, String,Throwable)}.
	 */
	public static void i(String tag, String message, Throwable throwable) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.i(tag, message, throwable);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.i(tag, message, throwable);
	}

	/**
	 * Uses {@link LogMode#e(String, String,Throwable)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#e(String, String,Throwable)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#e(String, String,Throwable)}.
	 * @param throwable
	 *            To be passed as a parameter to
	 *            {@link LogMode#e(String, String,Throwable)}.
	 */
	public static void e(String tag, String message, Throwable throwable) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.e(tag, message, throwable);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.e(tag, message, throwable);
	}

	/**
	 * Uses {@link LogMode#w(String, String, Throwable)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#w(String, String,Throwable)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#w(String, String,Throwable)}.
	 * @param throwable
	 *            To be passed as a parameter to
	 *            {@link LogMode#w(String, String,Throwable)}.
	 */
	public static void w(String tag, String message, Throwable throwable) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.w(tag, message, throwable);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.w(tag, message, throwable);
	}

	/**
	 * Uses {@link LogMode#v(String, String, Throwable)} . If
	 * {@link #isSelectiveDebuggingEnabled}, <br>
	 * Logs with Tags other than which are present in {@link #tagsToDebug} are
	 * filtered out.
	 * 
	 * @param tag
	 *            To be passed as a parameter to
	 *            {@link LogMode#v(String, String, Throwable)}.
	 * @param message
	 *            To be passed as a parameter to
	 *            {@link LogMode#v(String, String, Throwable)}.
	 * @param throwable
	 *            To be passed as a parameter to
	 *            {@link LogMode#v(String, String, Throwable)}.
	 */
	public static void v(String tag, String message, Throwable throwable) {
		if (!isSelectiveDebuggingEnabled)
			currentLogMode.v(tag, message, throwable);
		else if (isSelectiveDebuggingEnabled && tagsToDebug.contains(tag))
			currentLogMode.v(tag, message, throwable);
	}

	/**
	 * Uses the {@link LogMode#timeSpentOnClass(String)} method.
	 * 
	 * @param fullyQualifiedClassName
	 *            passed as a parameter to
	 *            {@link LogMode#timeSpentOnClass(String)}
	 * @return returns the result of {@link LogMode#timeSpentOnClass(String)}
	 */
	public static long timeSpentOnClass(String fullyQualifiedClassName) {
		return currentLogMode.timeSpentOnClass(fullyQualifiedClassName);
	}
}
