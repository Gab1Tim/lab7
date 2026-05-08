package server.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.models.Organization;
import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Читает/пишет коллекцию в JSON-файл (с поддержкой временного файла).
 */
public class FileManager {

    private String fileName;
    private String tempFileName;
    private final Gson gson = new Gson();
    private boolean usingTempFile = false;

    /**
     * Создаёт менеджер.
     */
    public FileManager(String fileName) {
        this.fileName = fileName;
        this.tempFileName = fileName + ".tmp";
    }

    /**
     * Загружает коллекцию из основного файла.
     */
    public LinkedHashMap<Integer, Organization> load() {
        return loadFromFile(fileName);
    }

    /**
     * Загружает коллекцию из временного файла.
     */
    public LinkedHashMap<Integer, Organization> loadFromTemp() {
        usingTempFile = true;
        return loadFromFile(tempFileName);
    }

    /**
     * Сливает основной и временный файлы (временный имеет приоритет).
     */
    public LinkedHashMap<Integer, Organization> loadAndMerge() {
        LinkedHashMap<Integer, Organization> mainMap = loadFromFile(fileName);
        LinkedHashMap<Integer, Organization> tempMap = loadFromFile(tempFileName);


        for (Map.Entry<Integer, Organization> entry : tempMap.entrySet()) {
            mainMap.put(entry.getKey(), entry.getValue());
        }


        saveToFile(fileName, mainMap);


        new File(tempFileName).delete();
        usingTempFile = false;
        System.out.println("Merged and saved to main file.");

        return mainMap;
    }

    /**
     * Загружает коллекцию из указанного файла.
     */
    private LinkedHashMap<Integer, Organization> loadFromFile(String path) {
        LinkedHashMap<Integer, Organization> map = new LinkedHashMap<>();
        try {
            File file = new File(path);
            if (!file.exists()) return map;

            Scanner scanner = new Scanner(file);
            StringBuilder json = new StringBuilder();
            while (scanner.hasNextLine()) json.append(scanner.nextLine());
            scanner.close();

            Type type = new TypeToken<LinkedHashMap<Integer, Organization>>(){}.getType();
            LinkedHashMap<Integer, Organization> loaded = gson.fromJson(json.toString(), type);
            if (loaded != null) map = loaded;

        } catch (Exception e) {
            System.out.println("Error reading file " + path + ": " + e.getMessage());
        }
        return map;
    }

    /**
     * @return доступен ли основной файл для записи
     */
    public boolean isMainFileAccessible() {
        File file = new File(fileName);
        return file.exists() && file.canWrite();
    }

    /**
     * @return существует ли временный файл
     */
    public boolean tempFileExists() {
        return new File(tempFileName).exists();
    }

    /**
     * Сохраняет коллекцию (при необходимости — во временный файл).
     */
    public boolean saveToMain(LinkedHashMap<Integer, Organization> map) {
        if (!isMainFileAccessible()) {
            return false;
        }
        saveToFile(fileName, map);
        if (usingTempFile) {
            mergeAndSwitch(map);
        }
        return true;
    }

    public void saveToTemp(LinkedHashMap<Integer, Organization> map) {
        saveToFile(tempFileName, map);
        usingTempFile = true;
        System.out.println("Saved to temporary file: " + tempFileName);
    }

    /**
     * Записывает коллекцию в файл.
     */
    private void saveToFile(String path, LinkedHashMap<Integer, Organization> map) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path));
            writer.write(gson.toJson(map));
            writer.flush();
            writer.close();
            System.out.println("Collection saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    /**
     * Сливает данные и переключается обратно на основной файл.
     */
    private void mergeAndSwitch(LinkedHashMap<Integer, Organization> currentMap) {
        System.out.println("Main file is accessible again. Merging...");

        LinkedHashMap<Integer, Organization> mainMap = loadFromFile(fileName);

        for (Map.Entry<Integer, Organization> entry : currentMap.entrySet()) {
            mainMap.put(entry.getKey(), entry.getValue());
        }

        saveToFile(fileName, mainMap);
        new File(tempFileName).delete();
        usingTempFile = false;
        System.out.println("Merge complete. Switched back to main file.");
    }


}