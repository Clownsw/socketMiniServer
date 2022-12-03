package cn.smilex;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author smilex
 */
@SuppressWarnings("all")
@Slf4j
public class Main {
    public static final String HOME_DIR = System.getProperty("home.dir");

    public static void main(String[] args) {
        if (StringUtils.isAllBlank(HOME_DIR)) {
            log.error("not set home.dir");
            System.exit(1);
        }

        startServer();
    }

    public static void startServer() {
        try {
            final int port = 8888;
            ServerSocket serverSocket = new ServerSocket(port, 1024);

            log.info("server start in port: " + port);

            while (true) {
                Thread.ofVirtual()
                        .start(new MessageHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
