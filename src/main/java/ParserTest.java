import manager.Task;
import parser.Amazon;
import parser.AmazonItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ParserTest {

    public static void main(String[] args) throws IOException {

        ArrayList<Task> tasks = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get("html/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Task task = new Task(file.getFileName().toString());
                            task.setHtml(String.join("\n", Files.readAllLines(file)));
                            tasks.add(task);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        ArrayList<AmazonItem> amazonItems = Amazon.parseItems(tasks);

        for (AmazonItem item : amazonItems)
            System.out.println(item);
    }
}
