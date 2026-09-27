package com.agriflow.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Built-in Embedded HTTP Server for AgriFlow
 * Uses standard JDK com.sun.net.httpserver.HttpServer - zero external dependencies.
 */
public class AgriFlowServer {

    private final int port;
    private int boundPort = -1;
    private HttpServer server;

    public AgriFlowServer(int port) {
        this.port = port;
    }

    public int start() throws IOException {
        int currentPort = this.port;
        int maxAttempts = 15;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                server = HttpServer.create(new InetSocketAddress(currentPort), 0);
                server.createContext("/api", new ApiHttpHandler());
                server.createContext("/", new StaticHttpHandler());
                server.setExecutor(Executors.newCachedThreadPool());
                server.start();
                this.boundPort = currentPort;
                System.out.println("==================================================================");
                System.out.println("  🌾 AgriFlow Embedded HTTP Web Server is RUNNING!");
                System.out.println("  🔗 Web UI Dashboard: http://localhost:" + currentPort);
                System.out.println("==================================================================");
                return currentPort;
            } catch (java.net.BindException be) {
                currentPort++;
            }
        }
        throw new IOException("Could not bind to any port between " + port + " and " + (port + maxAttempts));
    }

    public int getBoundPort() {
        return boundPort;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public static void openBrowser(int port) {
        String url = "http://localhost:" + port;
        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                return;
            }
        } catch (Exception ignored) {}

        // Fallback for Windows
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
        } catch (Exception e) {
            System.out.println("Notice: Please open " + url + " manually in your browser.");
        }
    }

    /**
     * API Handler
     */
    private static class ApiHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);

            // If POST or body present, also parse body params
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                params.putAll(parseQueryParams(body));
            }

            String jsonResponse = ApiDispatcher.handleRequest(path, params);

            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }

        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null || query.isEmpty()) return map;

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    try {
                        String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                        map.put(key, value);
                    } catch (Exception ignored) {}
                }
            }
            return map;
        }
    }

    /**
     * Static File Handler (serves HTML, CSS, JS)
     */
    private static class StaticHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if ("/".equals(pathStr) || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }

            // Look for file in resources or on filesystem
            Path localFile = Paths.get("src/main/resources/web", pathStr.substring(1));
            byte[] fileBytes = null;

            if (Files.exists(localFile) && !Files.isDirectory(localFile)) {
                fileBytes = Files.readAllBytes(localFile);
            } else {
                // Try from classpath
                InputStream is = getClass().getResourceAsStream("/web" + pathStr);
                if (is != null) {
                    fileBytes = is.readAllBytes();
                }
            }

            if (fileBytes != null) {
                String contentType = "text/html; charset=UTF-8";
                if (pathStr.endsWith(".css")) contentType = "text/css; charset=UTF-8";
                else if (pathStr.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
                else if (pathStr.endsWith(".json")) contentType = "application/json; charset=UTF-8";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                String notFound = "404 Not Found: " + pathStr;
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }
}
