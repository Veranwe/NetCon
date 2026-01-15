package src.pre.NetCon.connection;

import src.pre.NetCon.exceptions.AlreadyRunning;

import java.io.IOException;

public interface NetConnection {
    void start() throws IOException, AlreadyRunning;
    void stop() throws IOException;

    boolean isRunning();
}
