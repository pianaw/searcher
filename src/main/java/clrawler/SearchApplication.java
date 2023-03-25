package clrawler;


import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class SearchApplication extends Application {

    private BooleanVectorizedSearch booleanVectorizedSearch = new BooleanVectorizedSearch();
    private TableView<Page> table = new TableView<>();

    public static void main(String[] args) {
        SearchApplication.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Поиск на сайте " + Main.SITE);
        stage.setMaximized(true);

        final Label label = new Label("Введите Ваш запрос");
        label.setFont(new Font("Arial", 20));

        TextField textField = new TextField();
        textField.setPromptText("Введите Ваш запрос");
        textField.setPrefWidth(400);

        Button button = new Button();
        button.setStyle("-fx-background-color: #1d73cf;");
        button.setText("Найти");
        button.setPrefWidth(100);

        HBox hBox = new HBox(textField, button);
        hBox.setSpacing(5);

        final VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 0, 0, 10));

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        TableColumn firstNameCol = new TableColumn("Ссылка");
        firstNameCol.setMinWidth(600);
        firstNameCol.setResizable(true);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Page, String>("link")
        );

        TableColumn lastNameCol = new TableColumn("Tf-idf");
        lastNameCol.setMinWidth(400);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Page, String>("tfidf")
        );

        table.getColumns().addAll(firstNameCol, lastNameCol);
        table.getSelectionModel().setCellSelectionEnabled(true);
        MenuItem item = new MenuItem("Copy");
        item.setOnAction(event -> {
            ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
            int old_r = -1;
            StringBuilder clipboardString = new StringBuilder();
            for (TablePosition p : posList) {
                int r = p.getRow();
                int c = p.getColumn();
                Object cell = table.getColumns().get(c).getCellData(r);
                if (cell == null)
                    cell = "";
                if (old_r == r)
                    clipboardString.append('\t');
                else if (old_r != -1)
                    clipboardString.append('\n');
                clipboardString.append(cell);
                old_r = r;
            }
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        table.setContextMenu(menu);

        vbox.getChildren().addAll(label, hBox);
        stage.setScene(scene);
        stage.show();

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Map<Integer, Double> mapNumToTfidf = booleanVectorizedSearch.execute(textField.getText());
            System.out.println(mapNumToTfidf);
            Map<Integer, String> mapNumToLinks = Utils.findLinksForDocumentNumbers(new ArrayList<>(mapNumToTfidf.keySet()));
            System.out.println(mapNumToLinks);
            List<Page> pages = new ArrayList<>();
            for (Map.Entry<Integer, Double> entry : mapNumToTfidf.entrySet()) {
                pages.add(new Page(
                        mapNumToLinks.get(entry.getKey()),
                        entry.getValue().toString(),
                        entry.getKey()
                ));
            }
            pages.sort((page, t1) -> {
                if (Double.parseDouble(page.tfidf.get()) - Double.parseDouble(t1.tfidf.get()) > 0) {
                    return -1;
                } else if (Double.parseDouble(page.tfidf.get()) - Double.parseDouble(t1.tfidf.get()) < 0) {
                    return 1;
                } else {
                    return 0;
                }
            });
            createTable(pages);
            if (!vbox.getChildren().contains(table)) {
                vbox.getChildren().add(table);
            }
        });
    }

    private void createTable(List<Page> pages) {
        table.getItems().clear();
        table.setTableMenuButtonVisible(true);
        ObservableList<Page> data = FXCollections.observableArrayList(pages);
        table.setItems(data);
    }

    public static class Page {

        private final SimpleStringProperty link;
        private final SimpleStringProperty tfidf;
        private int numOfDocument;

        private Page(String link, String tfidf, int numOfDocument) {
            this.link= new SimpleStringProperty(link);
            this.tfidf = new SimpleStringProperty(tfidf);
            this.numOfDocument = numOfDocument;
        }

        public String getLink() {
            return link.get();
        }

        public void setLink(String link) {
            this.link.set(link);
        }

        public String getTfidf() {
            return tfidf.get();
        }

        public void setTfidf(String tfidf) {
            this.tfidf.set(tfidf);
        }

        public int getNumOfDocument() {
            return numOfDocument;
        }

        public void setNumOfDocument(int numOfDocument) {
            this.numOfDocument = numOfDocument;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Page page = (Page) o;
            return numOfDocument == page.numOfDocument && Objects.equals(link, page.link) && Objects.equals(tfidf, page.tfidf);
        }

        @Override
        public int hashCode() {
            return Objects.hash(link, tfidf, numOfDocument);
        }
    }
}