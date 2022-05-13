package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.myShapes.MyEllipse;
import sample.myShapes.MyLine;
import sample.myShapes.MyPolygon;
import sample.myShapes.MyRectangle;
import sample.substances.Chlorine;
import sample.substances.Ftor;
import sample.substances.Substance;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.sql.Connection;

public class Controller {
    @FXML private Button openFileButton;
    @FXML private Canvas mapImage, canvasGo, mapCanvas;
    @FXML private TextField mapX;
    @FXML private TextField mapY;
    @FXML private Button cordSaveBtn;
    @FXML private Button cordClearBtn;
    @FXML private GridPane cordGridPane;
    @FXML private Text degreeXCordText;
    @FXML private Text degreeYCordText;
    @FXML private Group objectsGroup;

    @FXML private Button infectionSaveBtn;
    @FXML private TextField weightField;
    @FXML private MenuButton substances;
    @FXML private MenuItem substanceChlorine, substanceFtor;
    @FXML private MenuButton windSpeedChoose;
    @FXML private MenuItem speedOne, speedTwo, speedThree, speedFour, speedFive;
    Substance substance;
    Integer windSpeed;
    Double currentWeight;

    @FXML private TableView<Object> dataTable;
    @FXML private TableColumn<Object, Integer> idColumn;
    @FXML private TableColumn<Object, String> nameColumn;
    @FXML private TableColumn<Object, String> addressColumn;
    Connection connection;
    DbConnector connector;

    private boolean infectionChlorine = false, infectionFtor = false;

    private final Random random = new Random();
    private String xDegree, yDegree;
    private GraphicsContext gcB,gcF;
    private boolean drawline = false, drawoval = false,
            drawrectangle = false, drawpolygon = false;
    double startX, startY, lastX, lastY, oldX, oldY;
    MyPolygon polygon;
    private final FileChooser fileChooser = new FileChooser();
    private Object object;

    public void initialize() throws SQLException, ClassNotFoundException {
        gcB = mapImage.getGraphicsContext2D();
        gcF = canvasGo.getGraphicsContext2D();
        connector = new DbConnector();
        buildData();
    }

    @FXML
    public void openFileAction() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            gc.drawImage(image, 0, 0, 440, 570);

            mapX.setDisable(false);mapY.setDisable(false);
            cordSaveBtn.setDisable(false);
        }
    }
    @FXML
    public void saveCord() {
        if (!mapX.getText().equals("") && !mapY.getText().equals("")) {
            canvasGo.setOnMouseMoved(event -> {
                try {
                    double finalCordX = (event.getX() * (0.50 / canvasGo.getWidth()))
                            + (Double.parseDouble(xDegree));

                    double heightInDegree = 0.33;
                    double finalCordY = Math.abs((event.getY() * (heightInDegree / canvasGo.getHeight()))
                            - (Double.parseDouble(yDegree))) + heightInDegree;

                    degreeXCordText.setText(String.format("%.2f", finalCordX));
                    degreeYCordText.setText(String.format("%.2f", finalCordY));

                } catch (Exception ignored) { }
            });

            xDegree = mapX.getText(); yDegree = mapY.getText();
            cordSaveBtn.setDisable(true); cordClearBtn.setDisable(false);
            mapX.setDisable(true); mapY.setDisable(true);
            cordGridPane.setVisible(true);
        }
    }
    @FXML
    public void clearSavedCord() {
        mapX.setText(""); mapY.setText("");
        mapY.setDisable(false);mapX.setDisable(false);
        cordSaveBtn.setDisable(false); cordClearBtn.setDisable(true);
        cordGridPane.setVisible(false);
    }

    @FXML
    private void onMousePressedListener(MouseEvent e){
        this.startX = e.getX();
        this.startY = e.getY();
        this.oldX = e.getX();
        this.oldY = e.getY();

        try {
            if (polygon.getPoints().isEmpty() && drawpolygon) {
                polygon.getPoints().add(e.getX());polygon.getPoints().add(e.getY());
            }
        } catch (Exception ignored) { }

        if(drawpolygon && e.getClickCount()>=2) {
            polygon.setFill(Color.color(this.random.nextDouble(), this.random.nextDouble(),
                    this.random.nextDouble()));

            for(int i = 0; i < polygon.getPoints().size(); i++) {
                if (i % 2 == 0) {
                    polygon.getPoints().set(i, polygon.getPoints().get(i)-15);
                } else {
                    polygon.getPoints().set(i, polygon.getPoints().get(i)-7);
                }
            }
            MyPolygon polygon = this.polygon;
            MyPolygon finalPolygon = polygon;
            try {
                finalPolygon.setDbId(connector.setObjectWithReturning(
                        new Object(0, "Полигон", "Адрес полигона")));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            buildData();
            polygon.setOnMouseClicked(event -> {
                showModal(finalPolygon);
            });
            objectsGroup.getChildren().add(polygon);
            this.polygon = new MyPolygon();
            reset();
        }
    }

    @FXML
    private void onMouseDraggedListener(MouseEvent e){
        this.lastX = e.getX();
        this.lastY = e.getY();

        if(drawrectangle)
            drawRectEffect();
        if(drawoval)
            drawOvalEffect();
        if(drawline)
            drawLineEffect();
        if(drawpolygon)
            drawLineEffect();

    }

    @FXML
    private void onMouseReleaseListener(MouseEvent e){
        if(drawrectangle){
            drawRect();
            reset();
        }
        if(drawoval) {
            drawOval();
            reset();
        }
        if(drawline) {
            drawLine();
            reset();
        }
        if(drawpolygon)
            try {
                if (!polygon.getPoints().isEmpty()) {
                    polygon.getPoints().add(e.getX());polygon.getPoints().add(e.getY());
                }
            } catch (Exception ignored){}
        if (infectionChlorine || infectionFtor) drawInfection();
        gcB.clearRect(0, 0, mapImage.getWidth(), mapImage.getHeight());
        gcF.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
    }

    @FXML
    private void onMouseExitedListener(MouseEvent event) {
//        System.out.println("No puedes dibujar fuera del canvas");
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>Draw methods<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private void drawOval() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        
        MyEllipse ellipse = new MyEllipse();
        ellipse.setCenterX(((lastX+startX)/2)-15);ellipse.setCenterY(((lastY+startY)/2)-7);
        ellipse.setRadiusX(wh/2);ellipse.setRadiusY(hg/2);
        ellipse.setFill(Color.color(this.random.nextDouble(), this.random.nextDouble(),
                this.random.nextDouble()));
        try {
            ellipse.setDbId(connector.setObjectWithReturning(
                    new Object(0, "Элипс", "Адрес элипса")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        buildData();
        ellipse.setOnMouseClicked(e -> {
            showModal(ellipse);
        });
        objectsGroup.getChildren().add(ellipse);
    }

    private void drawRect() {
        double wh = lastX - startX;
        double hg = lastY - startY;

        MyRectangle rectangle = new MyRectangle();
        rectangle.setX(startX-15);rectangle.setY(startY-7);
        rectangle.setHeight(hg);rectangle.setWidth(wh);
        rectangle.setFill(Color.color(this.random.nextDouble(), this.random.nextDouble(),
                this.random.nextDouble()));
        try {
            rectangle.setDbId(connector.setObjectWithReturning(
                    new Object(0, "Квадрат", "Адрес квадрата")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        buildData();
        rectangle.setOnMouseClicked(e -> {
            showModal(rectangle);
        });
        objectsGroup.getChildren().add(rectangle);

    }

    private void drawLine() {
        gcB.setStroke(Color.WHITE);
        gcB.strokeLine(startX, startY, lastX, lastY);

        MyLine line = new MyLine();
        line.setStartX(startX-15);line.setStartY(startY-7);
        line.setEndX(lastX-15);line.setEndY(lastY-7);
        try {
            line.setDbId(connector.setObjectWithReturning(
                    new Object(0, "Линия", "Адрес линии")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        buildData();
        line.setOnMouseClicked(e -> {
            showModal(line);
        });
        objectsGroup.getChildren().add(line);
    }

    private void drawInfection() {
        Ellipse factEllipse = new Ellipse();
        Ellipse possibleEllipse = new Ellipse();
        Substance substance;

        if (infectionChlorine) {
            substance = new Chlorine(this.currentWeight, this.windSpeed);
        } else {
            substance = new Ftor(this.currentWeight, this.windSpeed);
        }

//        this.substance.setWindSpeed(this.windSpeed);
//        this.substance.setCurrentTon(this.currentWeight);

        factEllipse.setCenterX(this.startX - 15);factEllipse.setCenterY(this.startY - 7);
        possibleEllipse.setCenterX(this.startX - 15);possibleEllipse.setCenterY(this.startY - 7);

        double factRadius = substance.getRadius(substance.FactSquare());
        double possibleRadius = substance.getRadius(substance.PossibleSquare());

        double onePixelToDegree = 0.01666666666;
        double YdolgotaKmInOneMinute = 1.85224768519;

        //перевести в пиксели
//        double xRad = (Math.abs(onePixelToDegree - radius)) * ((111.3 * (Math.cos(finalCordX))) / 60);
//        double yRad = (Math.abs(onePixelToDegree - radius) * onePixelToDegree) * YdolgotaKmInOneMinute;

        double factXRadius = ((factRadius/10) / onePixelToDegree) / (71.1 / 60);
        double factYRadius = ((factRadius/10) / onePixelToDegree) / YdolgotaKmInOneMinute;

        double possibleXRadius = ((possibleRadius/10) / onePixelToDegree) / (71.1 / 60);
        double possibleYRadius = ((possibleRadius/10) / onePixelToDegree) / YdolgotaKmInOneMinute;

        factEllipse.setRadiusX(factXRadius);factEllipse.setRadiusY(factYRadius);
        factEllipse.setFill(Color.LIGHTGREEN);
        possibleEllipse.setRadiusX(possibleXRadius);possibleEllipse.setRadiusY(possibleYRadius);
        possibleEllipse.setFill(Color.ORANGE);

        objectsGroup.getChildren().add(possibleEllipse);
        objectsGroup.getChildren().add(factEllipse);
    }

    //////////////////////////////////////////////////////////////////////
    //>>>>>>>>>>>>>>>>>>>>>>>>>>Draw effects methods<<<<<<<<<<<<<<<<<<<<<<<

    private void drawOvalEffect() {
        double wh = lastX - startX;
        double hg = lastY - startY;

        gcF.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
        gcF.setFill(Color.WHITE);
        gcF.fillOval(startX, startY, wh, hg);

    }

    private void drawRectEffect() {
        double wh = lastX - startX;
        double hg = lastY - startY;

        gcF.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
        gcF.setFill(Color.WHITE);
        gcF.fillRect(startX, startY, wh, hg);
    }

    private void drawLineEffect() {
        gcF.setLineWidth(5);
        gcF.clearRect(0, 0, canvasGo.getWidth() , canvasGo.getHeight());
        gcF.strokeLine(startX, startY, lastX, lastY);
    }

    ///////////////////////////////////////////////////////////////////////
    //>>>>>>>>>>>>>>>>>>>>>Buttons control<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @FXML
    private void setOvalAsCurrentShape(ActionEvent e) {
        drawline = false;
        drawoval = true;
        drawrectangle = false;
        drawpolygon = false;
        infectionFtor = false;
        infectionChlorine = false;
    }

    @FXML
    private void setLineAsCurrentShape() {
        drawline = true;
        drawoval = false;
        drawrectangle = false;
        drawpolygon = false;
        infectionFtor = false;
        infectionChlorine = false;
    }

    @FXML
    private void setRectangleAsCurrentShape(ActionEvent e) {
        drawline = false;
        drawoval = false;
        drawpolygon = false;
        drawrectangle = true;
        infectionFtor = false;
        infectionChlorine = false;
    }

    @FXML
    private void setPolygon(ActionEvent e) {
        drawline = false;
        drawoval = false;
        drawrectangle = false;
        drawpolygon = true;
        infectionFtor = false;
        infectionChlorine = false;

        polygon = new MyPolygon();
    }

    private void showModal(Shape shape) {
        FXMLLoader root = new FXMLLoader(getClass().getResource("ShapeInfoModal.fxml"));
        Stage dialog = new Stage();
        dialog.setTitle("Данные объекта");
        try {
            dialog.setScene(new Scene(root.load(), 433, 160));
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initOwner(openFileButton.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setOnHidden(e -> buildData());
        ShapeInfoModalController shapeInfoModalController = root.getController();
        shapeInfoModalController.initData(objectsGroup, shape, xDegree, yDegree);

        dialog.showAndWait();
    }
    @FXML private void showDataModal() {
        FXMLLoader root = new FXMLLoader(getClass().getResource("dataInfoModal.fxml"));
        Stage dialog = new Stage();

        dialog.setTitle("Данные записи");
        try {
            dialog.setScene(new Scene(root.load(), 395, 155));
        } catch (IOException e) {
            e.printStackTrace();;
        }
        dialog.initOwner(openFileButton.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        DataInfoModalController dataInfoModalController = root.getController();

        try {
            object.getId();
            dataInfoModalController.initData(object);
            dialog.setOnHidden(e -> buildData());
            dialog.showAndWait();
        } catch (Exception ignored) {

        }


    }
    @FXML private void showNewDataModal() {
        FXMLLoader root = new FXMLLoader(getClass().getResource("dataInfoModal.fxml"));
        Stage dialog = new Stage();
        dialog.setTitle("Данные записи");
        try {
            dialog.setScene(new Scene(root.load(), 395, 155));
        } catch (IOException e) {
            e.printStackTrace();;
        }
        dialog.initOwner(openFileButton.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        DataInfoModalController dataInfoModalController = root.getController();

        Object object = new Object(0, "", "");
        dataInfoModalController.initData(object);
        dialog.setOnHidden(e -> buildData());
        dialog.showAndWait();
    }

    public void buildData() {
        try {
            connection = connector.getConnection();
            dataTable.setItems(connector.getUsers("SELECT * FROM object"));

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataTable.setOnMouseClicked(e -> {
//            Object selected = dataTable.getSelectionModel().getSelectedItem();
//            System.out.println(selected.getId());
            object = dataTable.getSelectionModel().getSelectedItem();
        });
    }

    @FXML private void deleteData() throws SQLException {
        int dbId = object.getId();

        for(int i=0; i < objectsGroup.getChildren().size(); i++) {
            java.lang.Object object = objectsGroup.getChildren().get(i);
            if (object instanceof MyPolygon && ((MyPolygon) object).getDbId() == dbId)
                objectsGroup.getChildren().remove(object);
            else if (object instanceof MyLine && ((MyLine) object).getDbId() == dbId)
                objectsGroup.getChildren().remove(object);
            else if (object instanceof MyEllipse && ((MyEllipse) object).getDbId() == dbId)
                objectsGroup.getChildren().remove(object);
            else if (object instanceof MyRectangle && ((MyRectangle) object).getDbId() == dbId)
                objectsGroup.getChildren().remove(object);
        }

        connector.deleteObject(object);
        buildData();
    }

    private void reset() {
        drawline=false;
        drawoval=false;
        drawrectangle=false;
        drawpolygon=false;
    }

    @FXML private void setInfectionChlorine() {
        drawline = false;
        drawoval = false;
        drawrectangle = false;
        drawpolygon = false;
//        this.infectionChlorine = true;
//        this.infectionFtor = false;
        substance = new Chlorine();
    }
    @FXML private void setInfectionFtor() {
        drawline = false;
        drawoval = false;
        drawrectangle = false;
        drawpolygon = false;
//        this.infectionChlorine = false;
//        this.infectionFtor = true;
        substance = new Ftor();
    }

    @FXML private void setWindSpeedOne() {
        this.windSpeed = 1;
    }
    @FXML private void setWindSpeedTwo() {
        this.windSpeed = 2;
    }
    @FXML private void setWindSpeedThree() {
        this.windSpeed = 3;
    }
    @FXML private void setWindSpeedFour() {
        this.windSpeed = 4;
    }
    @FXML private void setWindSpeedFive() {
        this.windSpeed = 5;
    }

    @FXML private void saveInfection() {
        this.currentWeight = Double.parseDouble(weightField.getText());
        drawline = false;
        drawoval = false;
        drawrectangle = false;
        drawpolygon = false;
        if (substance instanceof Chlorine) {
            System.out.println("Chlorine");
            this.infectionChlorine = true;
            this.infectionFtor = false;
        } else if (substance instanceof Ftor) {
            System.out.println("Ftor");
            this.infectionChlorine = false;
            this.infectionFtor = true;
        }
    }
}
