import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static final int THREADS = 5; // сколько потоков создадим
    public static final int COUNT = 2; // сколько разрешений у семафора

    // Создаем три разных семафора
    public static MySemaphoreLock myLockSemaphore = new MySemaphoreLock(COUNT);
    public static MySemaphoreSpin mySpinSemaphore = new MySemaphoreSpin(COUNT);
    public static Semaphore regularSemaphore = new Semaphore(COUNT);

    // счётчики
    private static AtomicInteger activeThreads = new AtomicInteger(0); 
    private static AtomicInteger maxConcurrent = new AtomicInteger(0);

    public static void main(String[] args) { 

        System.out.println("-------------------\nRegular semaphore:\n-------------------");
        runTask(regularSemaphore);  

        System.out.println("--------------\nMy LOCK semaphore:\n--------------");
        runTask(myLockSemaphore);

        System.out.println("--------------\nMy SPIN semaphore:\n--------------");
        runTask(mySpinSemaphore);
    }

    private static void runTask(Semaphore semaphore) { // тест для семафоров
    ExecutorService es = Executors.newFixedThreadPool(THREADS); // Создаем пул из 50 потоков (они будут переиспользоваться)

    List<Callable<String>> tasks = new ArrayList<>(); 

    // сброс перед тестом
    activeThreads.set(0);    // обнуляем счетчик активных
    maxConcurrent.set(0);    // обнуляем максимум

    long startTime = System.nanoTime(); // старт времени

    for (int i = 0; i < THREADS; i++) { // создаем 50 задач
        int taskId = i; 

        tasks.add(() -> { // добавляем задачу
            String threadName = Thread.currentThread().getName();  

            // Вход в критическую секцию
            System.out.println(threadName + " (task " + taskId + ") хочет войти"); 

            semaphore.acquire();  // Если нет свободных разрешений - поток ждет (или крутится, зависит от семафора)

            int current = activeThreads.incrementAndGet(); // учет активных потоков 

            maxConcurrent.updateAndGet(prev -> Math.max(prev, current));  // Отслеживаем пиковое количество одновременно работающих потоков.

            System.out.println(threadName + " (task " + taskId + ") начал работу. Активных: " + current);

            Thread.sleep(50); // имитация работы

            System.out.println(threadName + " (task " + taskId + ") завершает работу");

            activeThreads.decrementAndGet(); // уменьшаем счётчик

            semaphore.release(); // освобождаем разрешение

            System.out.println(threadName + " (task " + taskId + ") вышел");

            return "Thread " + threadName + " done";
        });
    }

    // Запуск задачи
    try {
        es.invokeAll(tasks);
    } 
    catch (InterruptedException ie) {
        ie.printStackTrace();  
    }

    es.shutdown(); // новые задачи не принимаются, старые выполняются до коцна

    long endTime = System.nanoTime(); // конец времени

    long durationMs = (endTime - startTime) / 1_000_000;

    // проверка
    System.out.println("Максимум одновременно: " + maxConcurrent.get());
    System.out.println("Ожидается: " + COUNT);

    if (maxConcurrent.get() <= COUNT) {
      // Если maxConcurrent превышает COUNT (2), значит семафор пропустил больше потоков, чем разрешено
        System.out.println("Работает корректно");
    } 
    else {
        System.out.println("Ошибка в семафоре");
    }

    System.out.println("Время выполнения: " + durationMs + " ms\n");
}
}