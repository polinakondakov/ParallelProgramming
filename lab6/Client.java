import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;           // Сокет для связи с сервером
    private BufferedReader in;       // Чтение сообщений от сервера
    private PrintWriter out;         // Отправка сообщений серверу
    private String clientName;       // Имя клиента
    
    public Client(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort); // Создаёт сокет и подключается к серверу

            // Создаём потоки для чтения и отправки
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); 
            
            System.out.println("Подключено к серверу " + serverAddress + ":" + serverPort);
            
        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу: " + e.getMessage());
            System.exit(1); 
        }
    }
    
    public void start() {
        // ПОТОК 1: Читает сообщения от сервера
        Thread messageReader = new Thread(() -> { 
            try {
                String message;
                while ((message = in.readLine()) != null) { 
                    System.out.println(message);  
                }
            } catch (IOException e) { 
                System.err.println("Соединение с сервером разорвано");
                System.exit(0);
            }
        });
        messageReader.start();  // Запускаем поток чтения
     
        // ПОТОК 2 (главный): Читает ввод пользователя
        Scanner scanner = new Scanner(System.in); // Создаёт сканер для чтения с клавиатуры
        
        // Сначала вводим имя
        System.out.print("Введите ваше имя: ");
        clientName = scanner.nextLine(); 
        out.println(clientName);  
        
        System.out.println("Чат начался! Введите /quit для выхода");
        System.out.println("----------------------------------------");
        
        // Читаем сообщения пользователя и отправляем на сервер
        while (true) { // Бесконечно читает, что пишет пользователь
            String userInput = scanner.nextLine();
            
            if (userInput.equalsIgnoreCase("/quit")) {
                out.println("/quit"); 
                break;  
            }

            out.println(userInput);  // Отправляем сообщение серверу
        }
        
        // Закрываем ресурсы
        try {
            scanner.close(); 
            socket.close(); 
            System.out.println("Отключено от сервера");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0); 
    }
    
    public static void main(String[] args) {
        // Можно передать адрес и порт аргументами командной строки
        String serverAddress = "localhost";  // По умолчанию localhost
        int serverPort = 8080;               // По умолчанию порт 8080
        
        if (args.length >= 2) {
            serverAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
        }
        
        Client client = new Client(serverAddress, serverPort); // Создаёт объект клиента
        client.start(); // Запускает клиента
    }
}