import java.io.*; // для потоков ввода/вывода
import java.net.*; // для сокетов
import java.util.Scanner; // чтение с консоли
import java.util.concurrent.CopyOnWriteArrayList; // потокобезопасный список

public class Server {
    // Список всех подключенных клиентов (потокобезопасный)
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    public static void main(String[] args) {
        System.out.println("Сервер запущен..."); 
        
        try (ServerSocket serverSocket = new ServerSocket(8080)) {

            Thread consoleReader = new Thread(new ConsoleReader()); // Запускает отдельный поток для чтения с консоли сервера
            consoleReader.setDaemon(true); 
            consoleReader.start(); 
            
            // Бесконечно принимаем новых клиентов
            while (true) {
                Socket clientSocket = serverSocket.accept(); // accept() — блокирующий вызов: ждёт, пока какой-нибудь клиент не подключится
                // Как только клиент подключился метод возвращает объект Socket для общения с ним
                System.out.println("Новый клиент подключился: " + clientSocket.getInetAddress()); 
                
                ClientHandler clientHandler = new ClientHandler(clientSocket); // Создаёт объект, который будет обслуживать этого конкретного клиента
                clients.add(clientHandler); // Обработчик добавляется в общий список
                 
                // Запускается в новом потоке — чтобы сервер мог одновременно обслуживать много клиентов
                // Если обслуживать клиента в том же потоке, сервер не сможет принимать других клиентов
                new Thread(clientHandler).start();
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }
    
    // Отправка сообщения всем клиентам
    public static void broadcastMessage(String message, ClientHandler sender) {
        System.out.println("Широковещательное сообщение: " + message); // Печатает в консоль сервера текст сообщения
        
        for (ClientHandler client : clients) { 
            if (client != sender) { // если клиент не отправитель, то ему отправится сообщение
                client.sendMessage(message);
           }
        }
    }
    
    // Удаление клиента из списка при отключении
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Клиент отключен. Осталось клиентов: " + clients.size()); 
    }
    
    // Поток для чтения ввода с консоли сервера
    static class ConsoleReader implements Runnable {
        @Override
        public void run() { 
            Scanner scanner = new Scanner(System.in);  
            while (true) { // Бесконечно читает строки из консоли сервера  Работает вечно, пока сервер не остановят
                String message = scanner.nextLine(); // чтение строки,  
                //БЛОКИРУЮЩИЙ вызов — поток останавливается и ждёт когда администратор нажимает Enter, метод возвращает введённую строку
                if (message != null && !message.trim().isEmpty()) { 
                    // Отправляем сообщение от сервера всем клиентам
                    String serverMessage = "Сервер: " + message; 
                    for (ClientHandler client : clients) { 
                        client.sendMessage(serverMessage);
                    }
                }
            }
        }
    }
    
    // Он отвечает за общение с одним конкретным клиентом.
    static class ClientHandler implements Runnable {
        private Socket socket; // сокет для связи с клиентом
        private PrintWriter out; // отправка сообщений клиенту
        private BufferedReader in; // чтение сообщений от клиента
        private String clientName; // имя клиента (например, "Алиса")
        
        public ClientHandler(Socket socket) { 
            this.socket = socket; 
            try {
                // Создаем потоки для чтения и записи Чтобы сервер и клиент могли общаться!
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } 
            catch (IOException e) {
                System.err.println("Ошибка создания потоков клиента: " + e.getMessage());
            }
        }
        

        // run() — это метод, который выполняется в отдельном потоке для каждого клиента. 
        // Здесь происходит вся работа по обслуживанию одного клиента.
        @Override
        public void run() {
            try {
                clientName = in.readLine(); 
                sendMessage("Добро пожаловать, " + clientName + "!");
                
                broadcastMessage(clientName + " присоединился к чату", this); 
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) { // это бесконечный цикл который ждёт сообщения от клиента
                // Когда сообщение пришло — обрабатывает его и снова ждет следующее
                    if (inputLine.equalsIgnoreCase("/quit")) { 
                        break; // если пользователь ввел команду выхода то далее выполняется блок finally
                    }
                    
                    String formattedMessage = clientName + ": " + inputLine;
                    broadcastMessage(formattedMessage, this);
                }
                
            } catch (IOException e) {
                System.err.println("Ошибка связи с клиентом: " + e.getMessage());
            } finally {
                try {
                    if (socket != null && !socket.isClosed()) { // проверяем сокет существует ли он ещё не закрыт ли он ещё
                        broadcastMessage(clientName + " покинул чат", this); 
                        socket.close(); 
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this); 
            }
        }
        
        // Отправка сообщения конкретному клиенту
        public void sendMessage(String message) {
            if (out != null && !socket.isClosed()) { // проверяет, что поток отправки существует
                out.println(message); // отправка сообщения
            }
        }
    }
}