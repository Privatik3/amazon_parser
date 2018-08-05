package manager;

import excel.Handler;
import face.InterfaceParams;
import parser.AmazonItem;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Manager {

    private static Logger log = Logger.getLogger(Manager.class.getName());

    private static HashMap<String, Task> tasks = new HashMap<>();

    public static String initTask(InterfaceParams parameters) {


        String taskID = String.valueOf(new Date().getTime());
        tasks.put(taskID, new Task(parameters));

        log.info("Таск создан и добавлен в лист на выполнения");
        return taskID;
    }


    public static Integer getStatus(String taskID) {
        log.fine("Получаем статус работы таска");
        return tasks.get(taskID).status();
    }

    public static void process(String taskID) {
        log.info("Начинаем работу таска");
        tasks.get(taskID).start();
    }

    public static void saveResultToFile(String taskID) {

        log.info("Сохраняем результат работы в Excel файл");
        List<AmazonItem> result = tasks.get(taskID).getResult();
        Handler.writeResult(result);
    }

    public static void clearTask(String taskID) {

        log.info("Прерываем работу таска если жив, удаляем его с листа тасков");
        Task task = tasks.get(taskID);
        if (task.isAlive())
            task.interrupt();

        tasks.remove(taskID);
    }
}
