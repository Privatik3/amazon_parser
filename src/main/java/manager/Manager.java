package manager;

import excel.Handler;
import face.InterfaceParams;
import parser.AmazonItem;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Manager {

    private static HashMap<String, Task> tasks = new HashMap<>();

    public static String initTask(InterfaceParams parameters) {

        String taskID = String.valueOf(new Date().getTime());
        tasks.put(taskID, new Task(parameters));

        return taskID;
    }


    public static Integer getStatus(String taskID) {
        return tasks.get(taskID).status();
    }

    public static void process(String taskID) {
        tasks.get(taskID).start();
    }

    public static void saveResultToFile(String taskID) {
        List<AmazonItem> result = tasks.get(taskID).getResult();
        Handler.writeResult(result);
    }

    public static void clearTask(String taskID) {
        Task task = tasks.get(taskID);
        if (task.isAlive())
            task.interrupt();

        tasks.remove(taskID);
    }
}
