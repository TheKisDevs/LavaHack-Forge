package the.kis.devs.discordbot.util;

/**
 * @author _kisman_
 * @since 14:25 of 23.06.2022
 */
public class StackTraceUtil {
    public static String getStackTrace(Exception e) {
        StringBuilder stackTrace = new StringBuilder();

        stackTrace.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");

        for(int i = 0; i < e.getStackTrace().length; i++) {
            stackTrace.append("\tat ").append(e.getStackTrace()[i].toString()).append((i != e.getStackTrace().length - 1) ? "\n" : "");
        }

        return stackTrace.toString();
    }
}
