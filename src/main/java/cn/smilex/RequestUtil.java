package cn.smilex;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smilex
 */
public final class RequestUtil {

    /**
     * 解析请求数据
     *
     * @param reqStream 数据源
     * @return 请求对象
     * @throws IOException unknown
     */
    public static Request parseRequest(InputStream reqStream) throws IOException {
        BufferedReader httpReader = new BufferedReader(new InputStreamReader(reqStream, StandardCharsets.UTF_8));
        Request httpRequest = new Request();

        decodeRequestLine(httpReader, httpRequest);
        decodeRequestHeader(httpReader, httpRequest);
        decodeRequestMessage(httpReader, httpRequest);

        return httpRequest;
    }

    /**
     * 根据标准的http协议，解析请求行
     *
     * @param reader  数据源
     * @param request 请求对象
     * @throws IOException unknown
     */
    private static void decodeRequestLine(BufferedReader reader, Request request) throws IOException {
        String[] strArray = StringUtils.split(reader.readLine(), " ");
        assert strArray.length == 3;
        request.setMethod(strArray[0]);
        request.setUri(strArray[1]);
        request.setVersion(strArray[2]);
    }

    /**
     * 根据标准http协议，解析请求头
     *
     * @param reader  数据源
     * @param request 请求对象
     * @throws IOException unknown
     */
    private static void decodeRequestHeader(BufferedReader reader, Request request) throws IOException {
        Map<String, String> headers = new HashMap<>(16);
        String line = reader.readLine();

        while (StringUtils.isNotBlank(line)) {
            String[] kv = line.split(":");

            assert kv.length == 2;

            headers.put(StringUtils.trim(kv[0]), StringUtils.trim(kv[1]));
            line = reader.readLine();
        }

        request.setHeaders(headers);
    }

    /**
     * 解析消息中的数据
     *
     * @param reader  数据源
     * @param request 请求对象
     * @throws IOException unknown
     */
    @SuppressWarnings("all")
    public static void decodeRequestMessage(BufferedReader reader, Request request) throws IOException {
        int contentLength = Integer.parseInt(request.getHeaders().getOrDefault("Content-Length", "0"));
        if (contentLength == 0) {
            return;
        }

        char[] message = new char[contentLength];
        reader.read(message);
        request.setMessage(new String(message));
    }

    public static boolean isDefaultPage(String uri) {
        return "".equals(uri) || "/".equals(uri);
    }

    /**
     * 解析uri到路径
     *
     * @param uri uri
     * @return 路径
     */
    public static String uriToPath(String uri) {
        if (uri.contains("/..") || uri.contains("../")) {
            return null;
        }

        StringBuilder sb = new StringBuilder(Main.HOME_DIR);

        final int len = uri.length();
        int begin = 0, end = 0;

        while (end != len) {
            if (uri.charAt(end) == '/' && begin != end) {
                sb.append(File.separator)
                        .append(uri, begin + 1, end);
                begin = end;
            }

            end++;
        }

        if (begin != end) {
            sb.append(File.separator)
                    .append(uri, begin + 1, end);
        }

        return sb.toString();
    }
}
