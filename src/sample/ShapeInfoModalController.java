package sample;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import sample.myShapes.MyEllipse;
import sample.myShapes.MyLine;
import sample.myShapes.MyPolygon;
import sample.myShapes.MyRectangle;

import java.sql.SQLException;
import java.util.List;

public class ShapeInfoModalController {
    Group objectsGroup;
    Shape shape;
    String currentShape;
    Integer id;
    DbConnector dbConnector;
    @FXML Text squareText, perimeterText, databaseInfo;
    double xDegree, yDegree;
    final double onePixelToDegree= 0.01666666666;
    final double YdolgotaKmInOneMinute = 1.85224768519;
    private double XshirotaKmInOneMinute;
    //длина по width меняется
    // по height нет
    @FXML private Button deleteShapeBtn;

    private double belonging(double Y) {
        if (Y >= 0 && Y < 10) return 111.3 / 60;
        else if (Y >= 10 && Y < 20) return  109.6 / 60;
        else if (Y >= 20 && Y < 30) return  104.6 / 60;
        else if (Y >= 30 && Y < 40) return  96.5 / 60;
        else if (Y >= 40 && Y < 50) return  85.3 / 60;
        else if (Y >= 50 && Y < 60) return  71.1 / 60;
        else if (Y >= 60 && Y < 70) return 55.8 / 60;
        else if (Y >= 70 && Y < 80) return  38.2 / 60;
        else if (Y >= 80 && Y < 90) return 19.8 / 60;
        else return  0;
    }
    private double widthKmInOneMinute(double degree) {
        return (111.3 * (Math.cos(degree))) / 60;
    }

    public void initialize() {

    }
    public void initData(Group group, Shape shape, String xDegree, String yDegree) {
        this.objectsGroup = group;
        this.shape = shape;

        currentShape = shape.getClass().toString();
        squareText.setText(currentShape);
        this.xDegree = Double.parseDouble(xDegree);
        this.yDegree = Double.parseDouble(yDegree);

        switch (shape.getClass().toString()) {
            case "class sample.myShapes.MyRectangle":
                rectangleInfoSet(shape);
                databaseInfo.setText(((MyRectangle) shape).getDbIdAsString());
                break;
            case "class sample.myShapes.MyEllipse":
                ellipseInfoSet(shape);
                databaseInfo.setText(((MyEllipse) shape).getDbIdAsString());
                break;
            case "class sample.myShapes.MyLine":
                perimeterText.setText(lineInfoSet(shape) + " km");
                databaseInfo.setText(((MyLine) shape).getDbIdAsString());
                break;
            case "class sample.myShapes.MyPolygon":
                polygonInfoSet(shape);
                databaseInfo.setText(((MyPolygon) shape).getDbIdAsString());
                break;
            default:
                break;
        }
    }

    private void rectangleInfoSet(Shape shape) {
        MyRectangle rectangle = (MyRectangle) shape;
        id = rectangle.getDbId();
        double upperCordY = Math.abs((rectangle.getY() * (0.33 / 570))
                - (yDegree)) + 0.33;

        double wdth = widthKmInOneMinute(upperCordY);

        double heightKm = (rectangle.getHeight() * onePixelToDegree) * YdolgotaKmInOneMinute;
        double widthKm = (rectangle.getWidth() * onePixelToDegree) * wdth;

        double perimeter = (heightKm * 2) + (widthKm * 2);
        double square = (heightKm * widthKm);

        this.perimeterText.setText(perimeter + " км");
        this.squareText.setText(square + " км^2");
    }

    private double lineInfoSet(Shape shape) {
        MyLine line = (MyLine) shape;
        id = line.getDbId();

        double Ax = line.getStartX(); double Ay = line.getEndY();
        double Bx = line.getStartX(); double By = line.getStartY();
        double Cx = line.getEndX(); double Cy = line.getStartY();
        double Dx = line.getEndX(); double Dy = line.getEndY();

        double ABkm = (Math.abs(By - Ay) * YdolgotaKmInOneMinute) / 60;
        double BCkm = (Math.abs(Cx - Bx) * widthKmInOneMinute(By)) / 60;
        double ADkm = (Math.abs(Dx - Ax) * widthKmInOneMinute(Ay)) / 60;

        double AHkm = 0.5 * (Math.abs(BCkm - ADkm));
        double BHkm = Math.sqrt(Math.abs(ABkm*ABkm - AHkm*ABkm));
        double HDkm = ADkm - AHkm;

        double length = Math.sqrt( BHkm*BHkm + HDkm*HDkm );

        return length;
    }

    private void ellipseInfoSet(Shape shape) {
        MyEllipse ellipse = (MyEllipse) shape;
        id = ellipse.getDbId();

        double upperCordY = Math.abs((ellipse.getCenterY() * (0.33 / 570))
                - (yDegree)) + 0.33;
        //ellipse.getCenterX() перевести в градусы
        double xRadius = (ellipse.getRadiusX() * onePixelToDegree) * widthKmInOneMinute(upperCordY);
        double yRadius = (ellipse.getRadiusY() * onePixelToDegree) * YdolgotaKmInOneMinute;

        double square = Math.PI * xRadius * yRadius;

        squareText.setText(String.valueOf(square));
    }

    private void polygonInfoSet(Shape shape) {
        MyPolygon polygon = (MyPolygon) shape;
        id = polygon.getDbId();
        double length = 0;

        List<Double> list = polygon.getPoints();

        for(int i = 0; i < polygon.getPoints().size() - 2; i = i + 2) {
            Line line = new Line();
            line.setStartX(list.get(i));line.setStartY(list.get(i+1));
            line.setEndX(list.get(i+2));line.setEndY(list.get(i+3));

            length += lineInfoSet(line);
        }

        perimeterText.setText(String.valueOf(length));
    }

    @FXML public void deleteShape() throws SQLException, ClassNotFoundException {
        DbConnector dbConnector = new DbConnector();
        dbConnector.deleteObjectById(id);

        objectsGroup.getChildren().remove(shape);
        deleteShapeBtn.getScene().getWindow().hide();
    }
}
