package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Wraps a Scanner with validated read methods so the rest of the
 * application never has to deal with parsing/format errors directly.
 */
public class InputReader {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(ConsoleColors.CYAN + prompt + ConsoleColors.RESET + " ");
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    ConsoleUI.warn("Enter a number between " + min + " and " + max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                ConsoleUI.warn("Please enter a valid whole number.");
            }
        }
    }

    public int readIntFromSet(String prompt, List<Integer> allowed) {
        Set<Integer> set = new HashSet<>(allowed);
        while (true) {
            System.out.print(ConsoleColors.CYAN + prompt + ConsoleColors.RESET + " ");
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (!set.contains(v)) {
                    ConsoleUI.warn("Please pick one of: " + allowed);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                ConsoleUI.warn("Please enter a valid whole number.");
            }
        }
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(ConsoleColors.CYAN + prompt + ConsoleColors.RESET + " ");
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            ConsoleUI.warn("This field cannot be empty.");
        }
    }

    public String readPhone(String prompt) {
        while (true) {
            String s = readNonEmpty(prompt);
            if (PHONE_PATTERN.matcher(s).matches()) {
                return s;
            }
            ConsoleUI.warn("Enter a valid phone number (7-15 digits, may include + or -).");
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String s = readNonEmpty(prompt);
            if (EMAIL_PATTERN.matcher(s).matches()) {
                return s;
            }
            ConsoleUI.warn("Enter a valid email address, e.g. name@example.com");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(ConsoleColors.CYAN + prompt + " (yyyy-MM-dd)" + ConsoleColors.RESET + " ");
            String s = scanner.nextLine().trim();
            try {
                return LocalDate.parse(s, DATE_FMT);
            } catch (DateTimeParseException e) {
                ConsoleUI.warn("Enter a valid date in format yyyy-MM-dd, e.g. 2026-08-15");
            }
        }
    }

    public String readLine(String prompt) {
        System.out.print(ConsoleColors.CYAN + prompt + ConsoleColors.RESET + " ");
        return scanner.nextLine().trim();
    }
}
