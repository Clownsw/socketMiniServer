package cn.smilex;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author smilex
 */
@Slf4j
public class MessageHandler implements Runnable {
    private final Socket socket;
    private final Request request;
    private final OutputStream outputStream;

    public MessageHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.request = RequestUtil.parseRequest(socket.getInputStream());
        this.outputStream = this.socket.getOutputStream();
    }

    @Override
    public void run() {
        Thread.currentThread()
                .setName(String.valueOf(socket.getPort()));

        try {
            handlerMessage(request.getUri());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] findFileInHomeDir(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return Files.readAllBytes(Paths.get(path));
        }
        return null;
    }

    private void handlerMessage(String uri) throws IOException {
        byte[] message = RequestUtil.isDefaultPage(uri) ? findFileInHomeDir(buildPath("index.html")) : findFileInHomeDir(RequestUtil.uriToPath(uri));

        writeMessage(request.getVersion() + "\r\n");
        writeMessage(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf8\r\n");
        writeMessage("\n");

        if (message != null) {
            writeMessage(message);
        } else {
            writeMessage(findFileInHomeDir(buildPath("404.html")));
        }

        flush();
    }

    private String buildPath(String path) {
        return Main.HOME_DIR + File.separator + path;
    }

    private void writeMessage(String message) throws IOException {
        writeMessage(message.getBytes(StandardCharsets.UTF_8));
    }

    private void writeMessage(byte[] message) throws IOException {
        outputStream.write(message);
    }

    private void flush() throws IOException {
        outputStream.flush();
    }
}
