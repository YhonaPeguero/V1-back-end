package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompilerService {
  public static void startServer() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(4000), 0);
    server.createContext("/class", new CompileHandler());
    server.start();
    System.out.println("Server started on port 4000");
  }

  static class CompileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      // Establecer encabezados CORS para todas las respuestas
      exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

      if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(204, -1); // No Content para la preflight request
        return;
      }

      if (!"POST".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        return;
      }

      InputStream requestBody = exchange.getRequestBody();
      String sourceCode = new BufferedReader(new InputStreamReader(requestBody))
              .lines()
              .collect(Collectors.joining("\n"));

      Class<?> cls = Main.getClassFromSourceCode("", sourceCode, false);
      ResponseDto res = Main.buildResponseDto(cls);
      String jsonResponse = buildJsonResponse(res);

      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
      OutputStream responseBody = exchange.getResponseBody();
      responseBody.write(jsonResponse.getBytes());
      responseBody.close();
    }
    private static String buildJsonResponse(ResponseDto res) {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      sb.append("\"className\":").append("\"").append(res.getClassName()).append("\",");
      sb.append("\"scope\":").append("\"").append(res.getScope()).append("\",");
      sb.append("\"constructor\":").append("\"").append(res.getConstructor()).append("\",");
      sb.append("\"data\":[");
      List<String> dataEntries = new ArrayList<>();
      for (FieldOrMethodDto fieldOrMethod : res.getData()) {
        StringBuilder dataEntry = new StringBuilder();
        dataEntry.append("{");
        dataEntry.append("\"name\":").append("\"").append(fieldOrMethod.getName()).append("\",");
        dataEntry.append("\"vari\":").append("\"").append(fieldOrMethod.getVari()).append("\",");
        dataEntry.append("\"scope\":").append("\"").append(fieldOrMethod.getScope()).append("\",");
        dataEntry.append("\"signature\":").append("\"").append(fieldOrMethod.getSignature()).append("\"");
        dataEntry.append("}");
        dataEntries.add(dataEntry.toString());
      }
      sb.append(String.join(",", dataEntries));
      sb.append("]");
      sb.append("}");
      return sb.toString();
    }
  }
}
