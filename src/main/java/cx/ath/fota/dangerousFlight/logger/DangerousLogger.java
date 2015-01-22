package cx.ath.fota.dangerousFlight.logger;

public class DangerousLogger {
    final static boolean debug = true;

    public static void info(String s) {
        System.out.println(s);
    }

    public static void debug(String s) {
        if (debug) System.out.println(s);
    }
}
