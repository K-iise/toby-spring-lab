package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DconnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        // D사의 독자적인 방법으로 Connection 생성하는 코드.
        return null;
    }
}
