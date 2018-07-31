import manager.RequestTask;
import parser.Amazon;
import parser.AmazonItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParserTest {

    public static void main(String[] args) throws IOException {

        ArrayList<RequestTask> requestTasks = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get("html/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            RequestTask requestTask = new RequestTask(file.getFileName().toString());
                            requestTask.setHtml(String.join("\n", Files.readAllLines(file)));
                            requestTasks.add(requestTask);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        List<AmazonItem> amazonItems = Amazon.parseItems(requestTasks);

        for (AmazonItem item : amazonItems)
            System.out.println(item);
    }
}
