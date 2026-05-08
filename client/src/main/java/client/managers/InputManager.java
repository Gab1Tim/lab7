package client.managers;

import common.models.Address;
import common.models.Coordinates;
import common.models.Organization;
import common.models.OrganizationType;
import java.util.Scanner;

/**
 * Утилиты ввода (консоль/скрипт) и чтение модели {@link Organization}.
 */
public class InputManager {

    private static Scanner scanner = new Scanner(System.in);
    private static final Scanner originalScanner = scanner;
    private static boolean scriptMode = false;

    /** Читает строку (в режиме скрипта без приглашения). */
    public static String readLine(String prompt) {
        if (!scriptMode) {
            System.out.print(prompt);
            System.out.flush();
        }
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        } else {
            return null;
        }
    }

    /** Читает целое число с валидацией. */
    public static int readInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line == null) {
                if (scriptMode) throw new ScriptInputException("Unexpected end of script input.");
                continue;
            }
            try {
                if (line.contains(".")) {
                    double d = Double.parseDouble(line);
                    return (int) d;
                }
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new ScriptInputException("Invalid integer value in script: '" + line + "'");
                }
                System.out.println("Invalid integer. Try again.");
            }
        }
    }

    /** Читает число double с валидацией. */
    public static double readDouble(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line == null) {
                if (scriptMode) throw new ScriptInputException("Unexpected end of script input.");
                continue;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new ScriptInputException("Invalid double value in script: '" + line + "'");
                }
                System.out.println("Invalid double. Try again.");
            }
        }
    }

    /** Считывает организацию из ввода, задавая вопросы по полям. */
    public static Organization readOrganization() {
        String name = readLine("Enter name: ");
        if (name == null || name.isEmpty()) {
            if (scriptMode) throw new ScriptInputException("Name cannot be empty.");
            System.out.println("Name cannot be empty. Try again.");
            return readOrganization();
        }

        double x;
        while (true) {
            x = readDouble("Enter X coordinate (double, must be > -915): ");
            if (x > -915) break;
            if (scriptMode) throw new ScriptInputException("X coordinate must be > -915, got: " + x);
            System.out.println("X must be greater than -915. Try again.");
        }

        Integer y = readInt("Enter Y coordinate (integer): ");
        Coordinates coordinates = new Coordinates(x, y);

        int annualTurnover;
        while (true) {
            annualTurnover = readInt("Enter annual turnover (must be > 0): ");
            if (annualTurnover > 0) break;
            if (scriptMode) throw new ScriptInputException("Annual turnover must be > 0, got: " + annualTurnover);
            System.out.println("Invalid annual turnover. Try again.");
        }

        OrganizationType type = null;
        while (true) {
            String typeStr = readLine(
                    "Enter Organization Type (COMMERCIAL, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY, OPEN_JOINT_STOCK_COMPANY): "
            );
            if (typeStr == null || typeStr.isEmpty()) break;
            try {
                type = OrganizationType.valueOf(typeStr.trim().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                if (scriptMode) throw new ScriptInputException("Invalid OrganizationType in script: '" + typeStr + "'");
                System.out.println("Invalid OrganizationType. Try again.");
            }
        }

        String zip = readLine("Enter zip code (max 19 chars, can be empty): ");
        Address address = new Address((zip == null || zip.isEmpty()) ? null : zip);

        return new Organization(name, coordinates, annualTurnover, type, address);
    }

    /** Переключает ввод на скрипт (Scanner из файла). */
    public static void setFileInput(Scanner fileScanner) {
        scanner = fileScanner;
        scriptMode = true;
    }

    /** Возвращает ввод обратно на консоль. */
    public static void restoreConsoleInput() {
        scanner = originalScanner;
        scriptMode = false;
    }

    /** @return {@code true}, если сейчас читаем из скрипта */
    public static boolean isScriptMode() {
        return scriptMode;
    }

    /** Исключение для ошибок ввода в режиме скрипта. */
    public static class ScriptInputException extends RuntimeException {
        public ScriptInputException(String message) {
            super(message);
        }
    }

    public static Organization readOrganizationForUpdate() {
        return readOrganization();
    }
}