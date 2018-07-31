package excel;

import parser.AmazonItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Handler {

    public static List<String> readListOfAsin(String pathToListing) {

        List<String> result = new ArrayList<>();
        try {
            result = Files.readAllLines(Paths.get(pathToListing));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить листинг");
            e.printStackTrace();
        }

        return result;
    }

    public static void writeResult(List<AmazonItem> result) {

        String saveFile = "result.txt";

        try {
            for (AmazonItem item : result)
                Files.write(Paths.get(saveFile), (item.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Не удалось сохранить отчёт");
            e.printStackTrace();
        }
    }
}
