package utility;

/**
 * ANSI escape code constants used to colorize console output.
 *
 * Plain "cmd.exe" (old-style Windows Command Prompt) does NOT interpret
 * these escape codes by default - it prints them as literal garbage
 * text right in front of whatever they were supposed to colorize
 * (e.g. important text like a Booking ID can become hard to read,
 * buried behind junk characters). Windows Terminal, PowerShell 7+,
 * IntelliJ/VS Code terminals, and Linux/Mac terminals all handle
 * them fine.
 *
 * To guarantee the app is always readable, we detect at startup
 * whether the current terminal is likely to support ANSI colors.
 * If not, every constant below simply becomes an empty string, so
 * all the colorized print calls throughout the app silently become
 * plain, always-readable text - no code changes needed anywhere else.
 */
public final class ConsoleColors {
    private ConsoleColors() {
    }

    private static final boolean SUPPORTS_ANSI = detectAnsiSupport();

    private static boolean detectAnsiSupport() {
        // Explicit override: set env var HOTEL_APP_FORCE_COLOR=true/false
        // or run with -Dhotel.color=true/false to force one way or the other.
        String forced = System.getProperty("hotel.color");
        if (forced != null) {
            return Boolean.parseBoolean(forced);
        }
        String forcedEnv = System.getenv("HOTEL_APP_FORCE_COLOR");
        if (forcedEnv != null) {
            return Boolean.parseBoolean(forcedEnv);
        }
        // Windows Terminal sets WT_SESSION.
        if (System.getenv("WT_SESSION") != null) {
            return true;
        }
        // Most Unix-like shells (Linux, Mac, Git Bash, WSL) set TERM.
        if (System.getenv("TERM") != null) {
            return true;
        }
        // ConEmu / Cmder set ANSICON or ConEmuANSI.
        if (System.getenv("ANSICON") != null || "ON".equalsIgnoreCase(System.getenv("ConEmuANSI"))) {
            return true;
        }
        String os = System.getProperty("os.name", "");
        // Plain old Windows cmd.exe: no known ANSI-friendly env vars present.
        // Safest default is to disable colors so output is always readable.
        return !os.toLowerCase().contains("win");
    }

    public static final String RESET = SUPPORTS_ANSI ? "\u001B[0m" : "";
    public static final String BOLD = SUPPORTS_ANSI ? "\u001B[1m" : "";
    public static final String DIM = SUPPORTS_ANSI ? "\u001B[2m" : "";
    public static final String ITALIC = SUPPORTS_ANSI ? "\u001B[3m" : "";
    public static final String UNDERLINE = SUPPORTS_ANSI ? "\u001B[4m" : "";

    // Foreground colors
    public static final String BLACK = SUPPORTS_ANSI ? "\u001B[30m" : "";
    public static final String RED = SUPPORTS_ANSI ? "\u001B[31m" : "";
    public static final String GREEN = SUPPORTS_ANSI ? "\u001B[32m" : "";
    public static final String YELLOW = SUPPORTS_ANSI ? "\u001B[33m" : "";
    public static final String BLUE = SUPPORTS_ANSI ? "\u001B[34m" : "";
    public static final String PURPLE = SUPPORTS_ANSI ? "\u001B[35m" : "";
    public static final String CYAN = SUPPORTS_ANSI ? "\u001B[36m" : "";
    public static final String WHITE = SUPPORTS_ANSI ? "\u001B[37m" : "";

    // Bright foreground colors
    public static final String BRIGHT_BLACK = SUPPORTS_ANSI ? "\u001B[90m" : "";
    public static final String BRIGHT_RED = SUPPORTS_ANSI ? "\u001B[91m" : "";
    public static final String BRIGHT_GREEN = SUPPORTS_ANSI ? "\u001B[92m" : "";
    public static final String BRIGHT_YELLOW = SUPPORTS_ANSI ? "\u001B[93m" : "";
    public static final String BRIGHT_BLUE = SUPPORTS_ANSI ? "\u001B[94m" : "";
    public static final String BRIGHT_PURPLE = SUPPORTS_ANSI ? "\u001B[95m" : "";
    public static final String BRIGHT_CYAN = SUPPORTS_ANSI ? "\u001B[96m" : "";
    public static final String BRIGHT_WHITE = SUPPORTS_ANSI ? "\u001B[97m" : "";

    // Background colors
    public static final String BG_BLUE = SUPPORTS_ANSI ? "\u001B[44m" : "";
    public static final String BG_PURPLE = SUPPORTS_ANSI ? "\u001B[45m" : "";
    public static final String BG_BLACK = SUPPORTS_ANSI ? "\u001B[40m" : "";
}
