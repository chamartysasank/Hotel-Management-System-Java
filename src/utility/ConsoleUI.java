package utility;

import model.Booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Centralized console presentation layer: colors, animations, boxed
 * tables, and receipt formatting. Keeping all visual logic here keeps
 * the business logic classes (service/repository) clean.
 *
 * All borders and symbols are plain ASCII so the output renders
 * identically in Windows CMD, PowerShell, and Linux/Mac terminals
 * without any code page or UTF-8 configuration required.
 */
public final class ConsoleUI {

    private static final int WIDTH = 72;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");

    private ConsoleUI() {
    }

    // ---------------------------------------------------------------
    // Title screen
    // ---------------------------------------------------------------
    public static void printTitleScreen() {
        String cyan = ConsoleColors.BRIGHT_CYAN;
        String r = ConsoleColors.RESET;
        String bold = ConsoleColors.BOLD;
        System.out.println(cyan + topBorder() + r);
        System.out.println(cyan + emptyLine() + r);
        System.out.println(cyan + centeredInBox(bold + "HOTEL ROOM BOOKING" + r + cyan) + r);
        System.out.println(cyan + centeredInBox("CONSOLE APPLICATION") + r);
        System.out.println(cyan + emptyLine() + r);
        System.out.println(cyan + centeredInBox(ConsoleColors.BRIGHT_WHITE + "Premium Booking - Billing - Guest Management" + r + cyan) + r);
        System.out.println(cyan + emptyLine() + r);
        System.out.println(cyan + centeredInBox(ConsoleColors.YELLOW + "Developer : Sasank" + r + cyan) + r);
        System.out.println(cyan + centeredInBox(ConsoleColors.DIM + "Java 17  |  OOP  |  Collections  |  File Handling" + r + cyan) + r);
        System.out.println(cyan + emptyLine() + r);
        System.out.println(cyan + bottomBorder() + r);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Loading / startup animation
    // ---------------------------------------------------------------
    public static void loadingAnimation(String message, int totalSteps) {
        String c = ConsoleColors.BRIGHT_GREEN;
        int barWidth = 36;
        for (int step = 1; step <= totalSteps; step++) {
            int filled = (int) ((double) step / totalSteps * barWidth);
            StringBuilder bar = new StringBuilder();
            bar.append("\r").append(c).append(message).append(" [");
            for (int i = 0; i < barWidth; i++) {
                bar.append(i < filled ? "#" : "-");
            }
            int pct = (int) ((double) step / totalSteps * 100);
            bar.append("] ").append(pct).append("%").append(ConsoleColors.RESET);
            System.out.print(bar);
            sleep(70);
        }
        System.out.println();
    }

    public static void spinnerStep(String message, int cycles) {
        char[] frames = {'|', '/', '-', '\\'};
        for (int i = 0; i < cycles; i++) {
            System.out.print("\r" + ConsoleColors.CYAN + message + " " + frames[i % frames.length] + ConsoleColors.RESET);
            sleep(80);
        }
        System.out.println("\r" + ConsoleColors.GREEN + message + " done." + ConsoleColors.RESET + "     ");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------------------------------------------------------
    // Date / time
    // ---------------------------------------------------------------
    public static void printCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String msg = "Date: " + now.format(DATE_FMT) + "     Time: " + now.format(TIME_FMT);
        System.out.println(ConsoleColors.BRIGHT_PURPLE + centered(msg) + ConsoleColors.RESET);
        System.out.println();
    }

    public static String formattedNow() {
        return LocalDateTime.now().format(DATETIME_FMT);
    }

    // ---------------------------------------------------------------
    // Borders / boxes (plain ASCII)
    // ---------------------------------------------------------------
    public static String topBorder() {
        return "+" + "=".repeat(WIDTH - 2) + "+";
    }

    public static String bottomBorder() {
        return "+" + "=".repeat(WIDTH - 2) + "+";
    }

    public static String divider() {
        return "+" + "-".repeat(WIDTH - 2) + "+";
    }

    private static String emptyLine() {
        return "|" + " ".repeat(WIDTH - 2) + "|";
    }

    private static String centeredInBox(String text) {
        String plain = stripAnsi(text);
        int pad = WIDTH - 2 - plain.length();
        int left = pad / 2;
        int right = pad - left;
        return "|" + " ".repeat(Math.max(left, 0)) + text + " ".repeat(Math.max(right, 0)) + "|";
    }

    private static String centered(String text) {
        String plain = stripAnsi(text);
        int pad = WIDTH - plain.length();
        int left = Math.max(pad / 2, 0);
        return " ".repeat(left) + text;
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public static void sectionHeader(String title) {
        System.out.println(ConsoleColors.BRIGHT_BLUE + topBorder() + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BRIGHT_BLUE + centeredInBox(ConsoleColors.BOLD + title + ConsoleColors.RESET + ConsoleColors.BRIGHT_BLUE) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BRIGHT_BLUE + bottomBorder() + ConsoleColors.RESET);
    }

    public static void printBookingIdHighlight(String bookingId) {
        String label = "YOUR BOOKING ID: " + bookingId;
        int boxWidth = Math.max(label.length() + 4, 30);
        String border = "*".repeat(boxWidth);
        int pad = boxWidth - 2 - label.length();
        int left = pad / 2;
        int right = pad - left;
        System.out.println();
        System.out.println(border);
        System.out.println("*" + " ".repeat(left) + label + " ".repeat(right) + "*");
        System.out.println(border);
        System.out.println("Write this down - you need it for check-in, check-out,");
        System.out.println("cancelling, or viewing this booking again.");
        System.out.println();
    }

    public static void success(String msg) {
        System.out.println(ConsoleColors.BRIGHT_GREEN + "[OK] " + msg + ConsoleColors.RESET);
    }

    public static void error(String msg) {
        System.out.println(ConsoleColors.BRIGHT_RED + "[ERROR] " + msg + ConsoleColors.RESET);
    }

    public static void info(String msg) {
        System.out.println(ConsoleColors.BRIGHT_CYAN + "[INFO] " + msg + ConsoleColors.RESET);
    }

    public static void warn(String msg) {
        System.out.println(ConsoleColors.YELLOW + "[WARNING] " + msg + ConsoleColors.RESET);
    }

    // ---------------------------------------------------------------
    // Table rendering (plain ASCII)
    // ---------------------------------------------------------------
    public static void printTable(String[] headers, List<String[]> rows) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }
        printTableBorder(widths);
        printTableRow(headers, widths, true);
        printTableBorder(widths);
        for (String[] row : rows) {
            printTableRow(row, widths, false);
        }
        printTableBorder(widths);
    }

    private static void printTableBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BRIGHT_BLACK).append("+");
        for (int i = 0; i < widths.length; i++) {
            sb.append("-".repeat(widths[i] + 2));
            sb.append("+");
        }
        sb.append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static void printTableRow(String[] cells, int[] widths, boolean header) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BRIGHT_BLACK).append("|").append(ConsoleColors.RESET);
        for (int i = 0; i < cells.length; i++) {
            String cell = header ? ConsoleColors.BOLD + ConsoleColors.BRIGHT_WHITE + cells[i] + ConsoleColors.RESET : cells[i];
            int pad = widths[i] - cells[i].length();
            sb.append(" ").append(cell).append(" ".repeat(Math.max(pad, 0))).append(" ");
            sb.append(ConsoleColors.BRIGHT_BLACK).append("|").append(ConsoleColors.RESET);
        }
        System.out.println(sb);
    }

    // ---------------------------------------------------------------
    // Receipt / bill printing
    // ---------------------------------------------------------------
    public static void printReceipt(Booking b) {
        String c = ConsoleColors.BRIGHT_YELLOW;
        String r = ConsoleColors.RESET;
        System.out.println();
        System.out.println(c + topBorder() + r);
        System.out.println(c + centeredInBox(ConsoleColors.BOLD + "SASANK GRAND HOTEL" + r + c) + r);
        System.out.println(c + centeredInBox("Official Booking Receipt") + r);
        System.out.println(c + divider() + r);
        printReceiptLine("Booking ID", b.getBookingId());
        printReceiptLine("Guest Name", b.getGuest().getName());
        printReceiptLine("Phone", b.getGuest().getPhone());
        printReceiptLine("Email", b.getGuest().getEmail());
        printReceiptLine("Room Number", String.valueOf(b.getRoomNumber()));
        printReceiptLine("Room Type", b.getRoomType().getLabel());
        printReceiptLine("Check-in", b.getCheckIn().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        printReceiptLine("Check-out", b.getCheckOut().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        printReceiptLine("Nights", String.valueOf(b.getNights()));
        printReceiptLine("Status", b.getStatus().toString());
        System.out.println(c + divider() + r);
        printReceiptLine("Nightly Rate", money(b.getNightlyRate()));
        printReceiptLine("Room Charge", money(b.getRoomCharge()));
        printReceiptLine("Tax (10%)", money(b.getTax()));
        System.out.println(c + divider() + r);
        printReceiptLine(ConsoleColors.BOLD + "TOTAL AMOUNT" + r + c, ConsoleColors.BOLD + ConsoleColors.BRIGHT_GREEN + money(b.getTotal()) + r + c);
        System.out.println(c + bottomBorder() + r);
        System.out.println(ConsoleColors.DIM + centered("Thank you for choosing Sasank Grand Hotel. Visit again!") + ConsoleColors.RESET);
        System.out.println();
    }

    private static void printReceiptLine(String label, String value) {
        String c = ConsoleColors.BRIGHT_YELLOW;
        String r = ConsoleColors.RESET;
        String plainLabel = stripAnsi(label);
        String plainValue = stripAnsi(value);
        int pad = (WIDTH - 4) - plainLabel.length() - plainValue.length();
        String line = " " + label + " ".repeat(Math.max(pad, 1)) + value + " ";
        System.out.println(c + "|" + r + line + c + "|" + r);
    }

    public static String money(java.math.BigDecimal v) {
        return "Rs. " + v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    public static void clearScreenSoft() {
        // Prints spacing rather than a true clear, since console clearing
        // behaves inconsistently across terminals/IDE consoles.
        System.out.println("\n".repeat(1));
    }
}
