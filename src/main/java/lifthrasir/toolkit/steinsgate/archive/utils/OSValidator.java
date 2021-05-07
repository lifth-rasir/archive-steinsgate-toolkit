package lifthrasir.toolkit.steinsgate.archive.utils;

/**
 * OS구분 기능을 제공합니다<br />
 * https://blog.devez.net/214
 */
public class OSValidator {
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * 윈도우인가?
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * 맥인가?
     * @return
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * 유닉스인가?
     * @return
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    /**
     * 솔라리스인가?
     */
    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
}
