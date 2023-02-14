package utils;

public class Logger {

  /**
   * Log messages to the console
   *
   * @param logType : The type of log output
   * @param log:    The log message content
   */
  public static void toConsole(String logType, String log) {
    String formattedLog = String.format("[Status: %s] - %s", logType, log);
    System.out.println(formattedLog);
  }
}
