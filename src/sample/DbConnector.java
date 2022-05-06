package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DbConnector {
    private Connection connection;

    DbConnector() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres");
    }

    public ObservableList<Object> getUsers(String selectRequest) throws SQLException {
        ObservableList<Object> res = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(selectRequest);

        int id;
        String name,address;
        while (set.next()) {
            id = set.getInt("id");
            name = set.getString("name");
            address = set.getString("address");
            res.add(new Object(id, name, address));
        }
        return res;
    }

    public void setObject(Object object) throws SQLException {
        if (object.getId() == 0) {
            String SQL = "insert into \"object\"(name, address) values (?, ?)";
            PreparedStatement statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, object.getName());
            statement.setString(2, object.getAddress());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } else {
            String SQL = "update object set (name, address) = (?, ?) where id = ?";
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setString(1, object.getName());
            statement.setString(2, object.getAddress());
            statement.setInt(3, object.getId());
            statement.executeUpdate();
        }
    }

    public int setObjectWithReturning(Object object) throws SQLException {
        String SQL = "insert into \"object\"(name, address) values (?, ?)";
        PreparedStatement statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, object.getName());
        statement.setString(2, object.getAddress());
        statement.executeUpdate();
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return (generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }
    public void deleteObject(Object object) throws SQLException {
        String SQL = "delete from \"object\" where id = ?";
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.setInt(1, object.getId());
        statement.executeUpdate();
    }
    public void deleteObjectById(Integer id) throws SQLException {
        String SQL = "delete from \"object\" where id = ?";
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public Connection getConnection() {
        return connection;
    }
}
