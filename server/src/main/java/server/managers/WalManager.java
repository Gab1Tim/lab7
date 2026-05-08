package server.managers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WAL: запись/чтение журнала операций.
 */
public class WalManager {

    private final String walFileName;

    /**
     * Создаёт менеджер.
     */
    public WalManager(String walFileName) {
        this.walFileName = walFileName;
    }

    /**
     * Добавляет строку в WAL.
     */
    public void log(String operation) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(walFileName, true)
            );
            writer.write(operation + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("WAL write error: " + e.getMessage());
        }
    }

    /**
     * @return строки WAL
     */
    public List<String> readLog() {
        List<String> lines = new ArrayList<>();
        File file = new File(walFileName);
        if (!file.exists()) return lines;

        try {
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) lines.add(line);
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("WAL read error: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Очищает WAL.
     */
    public void clear() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(walFileName, false)
            );
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("WAL clear error: " + e.getMessage());
        }
    }

    /**
     * @return существует ли WAL и не пуст
     */
    public boolean exists() {
        File file = new File(walFileName);
        return file.exists() && file.length() > 0;
    }
}