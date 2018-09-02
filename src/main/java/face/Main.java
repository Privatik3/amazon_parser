package face;

import face.number.NumberSpinner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import manager.Manager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.*;

public class Main extends Application {

    private static Logger log = Logger.getLogger(Main.class.getName());
    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;

    public static void main(String[] args) {
        Logger system = Logger.getLogger("");
        Handler[] handlers = system.getHandlers();
        system.removeHandler(handlers[0]);

        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return
                        new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + " -> " +
                                record.getMessage() + "\r\n";
            }
        };

        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                try {
                    if (record.getMessage().isEmpty()) return;
                    String message = getFormatter().format(record);
//                    System.out.write(new String(message.getBytes("Cp1251"), "UTF-8").getBytes());
                    System.out.write(message.getBytes());


//                    System.out.println("sdfsfsdf | ываыаыаыва");
//                    System.out.println(new String("TEESSSTTT | ТЕСТ".getBytes("windows-1251"), "UTF-8"));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }

            @Override
            public void close() throws SecurityException {}

            @Override
            public void flush() {}
        };

        handler.setFormatter(formatter);
        system.addHandler(handler);
        system.setUseParentHandlers(false);

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Tabs");
        primaryStage.setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root, 445, 675, Color.WHITE);

//        String css = Main.class.getResource("main.css").toExternalForm();
        String css = "file:/" + Paths.get("main.css").toAbsolutePath().toString().replace("\\", "/");
        System.out.println(css);
        scene.getStylesheets().add(css);

        TabPane tabPane = new TabPane();

        BorderPane borderPane = new BorderPane();

        tabPane.getTabs().add(createMainTab());
        tabPane.getTabs().add(logTabs());


        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab logTabs() {
        Tab tab = new Tab();
        tab.setStyle("-fx-pref-width: 250;");
        tab.setText("Logs");

        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 0;");

        GridPane gridPane = new GridPane();
        TextArea ta = TextAreaBuilder.create().prefWidth(455).prefHeight(655).wrapText(true).build();
//        ta.setFont(Font.loadFont("file:consola.ttf", 10));
        ta.setFont(Font.font("Verdana", 14));
        Console console = new Console(ta);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
//        System.setErr(ps);


        gridPane.add(ta, 0, 0);

        vbox.getChildren().add(gridPane);
        tab.setContent(vbox);
        return tab;
    }

    private Tab createMainTab() {

        Tab tab = new Tab();
        tab.setStyle("-fx-pref-width: 150;");
        tab.setText("Home");

        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-padding: 10 25;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(15);

        gridPane.add(new Label("URL (путь к листингу)"), 0, 1);
        final TextField catalogUrl = new TextField();
        catalogUrl.setId("catalogUrl");
        catalogUrl.setMinWidth(250);
        gridPane.add(catalogUrl, 3, 1, 2, 1);


        // Листинг асинов
        gridPane.add(new Label("Загрузить список\nлистингов из файла"), 0, 2);

        final TextField asinList = new TextField();
        asinList.setId("badSellers");
        asinList.setMinWidth(200);
        gridPane.add(asinList, 3, 2);

        asinList.setText("C:\\Developers\\amazon_parser\\asins.xlsx");
        this.fileChooser.setInitialDirectory(new File("C:\\Developers\\amazon_parser\\"));

        Button asinListButton = new Button("...");
        asinListButton.setMaxWidth(50);
        asinListButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
                asinList.setText(file.toString());
        });
        gridPane.add(asinListButton, 4, 2);

        // Плохие продавцы
        gridPane.add(new Label("Загрузить список\nплохих продавцов"), 0, 3);

        final TextField badSellers = new TextField();
        badSellers.setId("badSellers");
        badSellers.setMinWidth(200);
        gridPane.add(badSellers, 3, 3);

        Button badSellersButton = new Button("...");
        badSellersButton.setMaxWidth(50);
        badSellersButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
                badSellers.setText(file.toString());
        });
        gridPane.add(badSellersButton, 4, 3);
        gridPane.add(new Separator(), 0, 4, 6, 1);

        // Второй блок
        gridPane.add(new Label("Скорость работы (количество потоков)"), 0, 5, 4, 1);
        final NumberSpinner fibersCount = new NumberSpinner();
        fibersCount.setNumber(BigDecimal.valueOf(256));
        fibersCount.setMaxWidth(246);
        fibersCount.setId("fibersCount");
        gridPane.add(fibersCount, 3, 5, 2, 1);

        gridPane.add(new Label("USA zip code"), 0, 6);
        final TextField zipCode = new TextField();
        zipCode.setText("07064");
        zipCode.setMaxWidth(48);
        zipCode.setId("zipCode");
        gridPane.add(zipCode, 4, 6, 2, 1);

        gridPane.add(new Label("Add to the shipping price on condition"), 0, 7, 4, 1);
        final NumberSpinner priceOver = new NumberSpinner(BigDecimal.ZERO, new BigDecimal("0.1"), new DecimalFormat("#,#0.0"));
        priceOver.setMaxWidth(246);
        priceOver.setId("priceOver");
        gridPane.add(priceOver, 3, 7, 2, 1);
        gridPane.add(new Separator(), 0, 8, 6, 1);

        vbox.getChildren().add(gridPane);

        // Третий блок
        ColumnConstraints[] columns = new ColumnConstraints[10];
        for (int i = 0; i < 10; i++)
            columns[i] = new ColumnConstraints(40);

        GridPane gridPane2 = new GridPane();
        gridPane2.setVgap(5);
//        gridPane2.setGridLinesVisible( true );
        gridPane2.getColumnConstraints().addAll(columns);

        gridPane2.add(new Label("Признак листинга"), 0, 1, 3, 1);
        gridPane2.add(new Label("BSR min"), 4, 1, 2, 1);
        gridPane2.add(new Label("BSR max"), 7, 1, 2, 1);

        gridPane2.add(new Label("Листинг без меток"), 0, 2, 3, 1);

        final TextField param1Min = new TextField();
        param1Min.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param1Min.setMaxWidth(80);
        gridPane2.add(param1Min, 4, 2, 2, 1);

        final TextField param1Max = new TextField();
        param1Max.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param1Max.setMaxWidth(80);
        gridPane2.add(param1Max, 7, 2, 2, 1);

        final CheckBox paramCb1 = new CheckBox();
        paramCb1.setId("paramCb1");
        gridPane2.add(paramCb1, 9, 2);

        gridPane2.add(new Label("Currently unavailable"), 0, 3, 3, 1);
        final TextField param2Min = new TextField();
        param2Min.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param2Min.setMaxWidth(80);
        gridPane2.add(param2Min, 4, 3, 2, 1);

        final TextField param2Max = new TextField();
        param2Max.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param2Max.setMaxWidth(80);
        gridPane2.add(param2Max, 7, 3, 2, 1);

        final CheckBox paramCb2 = new CheckBox();
        paramCb2.setId("paramCb2");
        gridPane2.add(paramCb2, 9, 3);

        gridPane2.add(new Label("Exclusively for Prime"), 0, 4, 4, 1);
        final TextField param3Min = new TextField();
        param3Min.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param3Min.setMaxWidth(80);
        gridPane2.add(param3Min, 4, 4, 2, 1);

        final TextField param3Max = new TextField();
        param3Max.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        param3Max.setMaxWidth(80);
        gridPane2.add(param3Max, 7, 4, 2, 1);

        final CheckBox paramCb3 = new CheckBox();
        paramCb3.setId("paramCb3");
        gridPane2.add(paramCb3, 9, 4);

        gridPane2.add(new Separator(), 0, 5, 10, 3);

        // Четвёртый блок
        gridPane2.add(new Label("Customer Reviews"), 0, 9, 3, 1);
        final NumberSpinner reviewInMin = new NumberSpinner(BigDecimal.ZERO, new BigDecimal("0.1"), new DecimalFormat("#,#0.0"));
        reviewInMin.setPadding(new Insets(5, 0, 0, 0));
        reviewInMin.setMaxWidth(72);
        gridPane2.add(reviewInMin, 4, 9, 2, 1);

        final NumberSpinner reviewInMax = new NumberSpinner(BigDecimal.ZERO, new BigDecimal("0.1"), new DecimalFormat("#,#0.0"));
        reviewInMax.setPadding(new Insets(5, 0, 0, 0));
        reviewInMax.setMaxWidth(72);
        gridPane2.add(reviewInMax, 7, 9, 2, 1);

        final CheckBox reviewCb = new CheckBox();
        reviewCb.setMaxSize(20, 20);
        reviewCb.setId("reviewCb");
        gridPane2.add(reviewCb, 9, 9);

        gridPane2.add(new Label("Дата создания листинга"), 0, 10, 3, 1);

        DatePicker dataFrom = new DatePicker();
        dataFrom.setId("dataFrom");
        gridPane2.add(dataFrom, 4, 10, 2, 1);

        DatePicker dataTo = new DatePicker();
        dataTo.setId("dataTo");
        gridPane2.add(dataTo, 7, 10, 2, 1);

        final CheckBox dataCb = new CheckBox();
        dataCb.setMaxSize(20, 20);
        dataCb.setId("dataCb");
        gridPane2.add(dataCb, 9, 10);


        Button startButton = new Button("Start");
        startButton.setPrefSize(185, 60);
        gridPane2.add(startButton, 5, 14, 5, 5);

        startButton.setOnAction(event -> {
            startButton.setDisable(true);

//            log.info("Читаем все параметры с пользовательского интерфейса.");
            log.info("Reading all parameters from user interface.");
            InterfaceParams parameters = new InterfaceParams();

            parameters.setUrlListing(catalogUrl.getText());
            parameters.setPathToListing(asinList.getText());
            parameters.setPathToBadSellers(badSellers.getText());

            // ----------------------------------
            parameters.setCountOfFibers(fibersCount.getNumber().intValue());
            parameters.setUsaZipCode(zipCode.getText());
            parameters.setAddToPrice((double) priceOver.getNumber().floatValue());

            // ----------------------------------
            List<Filter> filters = new ArrayList<>();
            filters.add(new Filter(FilterType.NONE, getIntParam(param1Min.getText()), getIntParam(param1Max.getText()), paramCb1.isSelected()));
            filters.add(new Filter(FilterType.UNAVALIABLE, getIntParam(param2Min.getText()), getIntParam(param2Max.getText()), paramCb2.isSelected()));
            filters.add(new Filter(FilterType.PRIME, getIntParam(param3Min.getText()), getIntParam(param3Max.getText()), paramCb3.isSelected()));

            parameters.setFilters(filters);

            // ----------------------------------
            filters.add(new Filter(FilterType.RATING, (double) reviewInMin.getNumber().floatValue(), (double) reviewInMax.getNumber().floatValue(), reviewCb.isSelected()));

            try {
                long dateFrom = new SimpleDateFormat("MM/dd/yyyy").parse(dataFrom.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).getTime();
                long dateTo = new SimpleDateFormat("MM/dd/yyyy").parse(dataTo.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).getTime();
                filters.add(new Filter(FilterType.CREATION_DATE, dateFrom, dateTo, dataCb.isSelected()));
            } catch (Exception ignored) {
            }


            new Thread(() -> {
//                log.info("Инициализируем таск, получаем таск ID");
                log.info("Initialize the task, we get the ID");
                String taskID = Manager.initTask(parameters);
                Manager.process(taskID);

                Integer status = 0;
                while ((status = Manager.getStatus(taskID)) < 100) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                }

                Manager.saveResultToFile(taskID);
                Manager.clearTask(taskID);

                startButton.setDisable(false);
            }).start();
        });

        vbox.getChildren().add(gridPane2);

        tab.setContent(vbox);
        return tab;
    }

    private double getIntParam(String num) {

        if (num.isEmpty()) return 0;
        return Integer.parseInt(num.replace(",", ""));
    }

    public static class Console extends OutputStream {

        private TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        @Override
        public void write(int i) throws IOException {
//            output.appendText(String.valueOf((char) i));

            javafx.application.Platform.runLater( () -> output.appendText(String.valueOf((char) i)) );
        }
    }
}