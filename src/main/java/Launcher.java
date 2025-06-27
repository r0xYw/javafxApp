/**
 * Launcher class для обхода ограничений JavaFX при создании JAR файла.
 * Этот класс не должен наследоваться от Application и должен находиться
 * в корне src/main/java (не в пакете).
 */
public class Launcher {
    public static void main(String[] args) {
        // Запускаем основное JavaFX приложение
        com.transport.MainApp.main(args);
    }
}