package hello.springbook.user.dao;


import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
    int counter = 0;
    private final ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker){
        this.realConnectionMaker = connectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeConnection();
    }

    public int getCounter(){
        return this.counter;
    }
}
