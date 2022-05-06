package sample.myShapes;

import javafx.scene.shape.Line;

public class MyLine extends Line {
    int dbId;

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }
    public String getDbIdAsString() {
        return String.valueOf(dbId);
    }
}
