package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) throws IOException {
    if (args.length > 0 && args[0].equals("server")) {
      CompilerService.startServer();
    } else {
      terminalApp(args);
    }
  }


  private static void terminalApp(String[] args) {
    try {
      if (args.length != 1) {
        System.err.println("Un argumento es requerido");
        System.exit(1);
      }

      String fileName = args[0];
      Class<?> cls = getClassFromSourceCode(fileName, "", true);
      printClassInformation(cls);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void deleteFile(String filePath) {
    try {
      Path path = Paths.get(filePath);
      Files.delete(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getSourceCodeTerminal(String fileName) {
    String sourceCode = "";

    try {
      InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
      if (inputStream == null) {
        throw new IllegalArgumentException("File not found: " + fileName);
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        sourceCode = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
      return sourceCode;
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Error reading file.");
    }
  }

  public static String extractClassName(String sourceCode) {
    // La expresión regular busca por la palabra clave "class" seguida de un espacio,
    // y luego captura la siguiente secuencia de caracteres que constituyen el nombre de la clase.
    Pattern pattern = Pattern.compile("\\bclass\\s+([\\w\\$]+)");
    Matcher matcher = pattern.matcher(sourceCode);

    if (matcher.find()) {
      // Retorna el nombre de la clase capturado por el primer grupo de la expresión regular.
      return matcher.group(1);
    } else {
      // Si no encuentra una coincidencia, retorna una cadena vacía.
      return "";
    }
  }
  public static Class<?> getClassFromSourceCode(String fileName, String sourceCodeFromBody, boolean isTerminal) {
    String sourceCode = "";
    // TODO: Person.java no puede ir hardcodeado
    if (isTerminal) {
      sourceCode = getSourceCodeTerminal(fileName);
    } else {
      sourceCode = sourceCodeFromBody;
    }

    String className = extractClassName(sourceCode);
    String classNameWithExtJava = className + ".java";
    String classNameWithExtClass = className + ".class";

    Path sourceFilePath = Paths.get("src", "main", "java", "org", "example", classNameWithExtJava);

    try {
      Files.write(sourceFilePath, sourceCode.getBytes());
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFilePath.toFile());

      boolean success = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
      fileManager.close();
      if (!success) {
        for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
          System.err.println(diagnostic);
        }
        throw new IllegalStateException("Compilation failed.");
      }

      Path classFilePath = Paths.get("src", "main", "java", "org", "example", classNameWithExtClass);
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classFilePath.getParent().toUri().toURL()});
      Class<?> cls = Class.forName(className, true, classLoader);
      deleteFile("src/main/java/org/example/"+classNameWithExtClass);
      deleteFile("src/main/java/org/example/"+classNameWithExtJava);
      return cls;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new IllegalStateException("Compilation failed.");
    }
  }

  public static ResponseDto buildResponseDto(Class<?> cls) {
    List<FieldOrMethodDto> data = new ArrayList<>();
    for (Field field : cls.getDeclaredFields()) {
      data.add(new FieldOrMethodDto(field.getName(), "A", Modifier.toString(field.getModifiers()), "TYPE:" + field.getType().getSimpleName()));
    }
    for (Method method : cls.getDeclaredMethods()) {
      data.add(new FieldOrMethodDto(method.getName(), "M", Modifier.toString(method.getModifiers()), getSignature(method)));
    }

    return new ResponseDto(cls.getSimpleName(), Modifier.toString(cls.getModifiers()),
            Arrays.stream(cls.getDeclaredConstructors())
                    .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                    .findFirst()
                    .map(Constructor::getName)
                    .orElse(""),
            data);
  }

  private static void printClassInformation(Class<?> cls) {
    System.out.println("class name: " + cls.getSimpleName());
    System.out.println("scope: " + Modifier.toString(cls.getModifiers()));
    Arrays.stream(cls.getDeclaredConstructors())
            .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
            .findFirst()
            .ifPresent(constructor -> System.out.println("constructor: " + constructor.getName()));

    int maxNameLength = Math.max("NAME".length(),
            Stream.concat(
                            Arrays.stream(cls.getDeclaredFields()).map(Field::getName),
                            Arrays.stream(cls.getDeclaredMethods()).map(Method::getName)
                    )
                    .mapToInt(String::length)
                    .max().orElse(0));

    int maxVariLength = "VARI".length();
    int maxScopeLength = Math.max("SCOPE".length(),
            Stream.concat(
                            Arrays.stream(cls.getDeclaredFields()).map(field -> Modifier.toString(field.getModifiers())),
                            Arrays.stream(cls.getDeclaredMethods()).map(method -> Modifier.toString(method.getModifiers()))
                    )
                    .mapToInt(String::length)
                    .max().orElse(0));

    int maxSignatureLength = Math.max("SIGNATURE".length(),
            Arrays.stream(cls.getDeclaredMethods())
                    .mapToInt(method -> getSignature(method).length())
                    .max().orElse(0));

    String headerFormat = "┃ %-" + maxNameLength + "s ┃ %-" + maxVariLength + "s ┃ %-" + maxScopeLength + "s ┃ %-" + maxSignatureLength + "s ┃%n";

    printLine(maxNameLength, maxVariLength, maxScopeLength, maxSignatureLength, '┏', '┳', '┓');
    System.out.printf(headerFormat, "NAME", "VARI", "SCOPE", "SIGNATURE");
    printLine(maxNameLength, maxVariLength, maxScopeLength, maxSignatureLength, '┣', '╋', '┫');

    String rowFormat = "┃ %-" + maxNameLength + "s ┃ %-" + maxVariLength + "s ┃ %-" + maxScopeLength + "s ┃ %-" + maxSignatureLength + "s ┃%n";

    printRows(cls, rowFormat);

    printLine(maxNameLength, maxVariLength, maxScopeLength, maxSignatureLength, '┗', '┻', '┛');
    System.out.println("VARI: variación, RTYPE: return type, PARAMS: parámetros");
    System.out.println("A: atributo, M: método");
  }

  private static void printLine(int nameLen, int variLen, int scopeLen, int signLen, char start, char middle, char end) {
    System.out.print(start);
    System.out.print(String.join("", Collections.nCopies(nameLen + 2, "━")));
    System.out.print(middle);
    System.out.print(String.join("", Collections.nCopies(variLen + 2, "━")));
    System.out.print(middle);
    System.out.print(String.join("", Collections.nCopies(scopeLen + 2, "━")));
    System.out.print(middle);
    System.out.print(String.join("", Collections.nCopies(signLen + 2, "━")));
    System.out.println(end);
  }

  private static String getSignature(Method method) {
    return String.format("RTYPE:%s, PARAMS:%s",
            method.getReturnType().getSimpleName(),
            Arrays.toString(method.getParameterTypes()).replaceAll("class ", "").replaceAll("\\[|\\]", ""));
  }

  private static void printRows(Class<?> cls, String rowFormat) {
    for (Field field : cls.getDeclaredFields()) {
      System.out.printf(rowFormat, field.getName(), "A", Modifier.toString(field.getModifiers()), "TYPE:" + field.getType().getSimpleName());
    }
    for (Method method : cls.getDeclaredMethods()) {
      System.out.printf(rowFormat, method.getName(), "M", Modifier.toString(method.getModifiers()), getSignature(method));
    }
  }
}
