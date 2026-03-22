import java.util.*; //импортируем коллекции
import java.util.concurrent.*; 
import java.util.concurrent.atomic.AtomicInteger; //импортируем атомарный счетчик для потокобезопасного подсчета

public class Main {

    public static final int THREADS = 50; // кол-во потоков запущенных одновременно
    public static final int ITERATIONS = 1000; // кол-во итераций в каждом потоке
    public static final double NSEC = 1000_000_000.0; // для перевода наносекунд в секунды
    public static final int MAP_SIZE = 3; // кол-во разных ключей для теста
    public static final int SAMPLES = 5; // каждый тест запускается 5 раз

    // Объявление 4-х типов коллекций для тестирования и анализа
    public static Map<String, Integer> hashMap = new HashMap<>();
    public static Map<String, Integer> hashTable = new Hashtable<>();
    public static Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Integer> cHashMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        System.out.println("Тест 1. Потеря данных при конкурентной записи\n");
        // Запускаем тест для каждого типа
        double hashMapTime1 = testLostData(hashMap) / NSEC;
        double hashTableTime1 = testLostData(hashTable) / NSEC;
        double syncMapTime1 = testLostData(syncMap) / NSEC;
        double cHashMapTime1 = testLostData(cHashMap) / NSEC;
        
        System.out.println("\nВремя для 1 теста");
        System.out.println(String.format("\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.",
                hashMapTime1, hashTableTime1, syncMapTime1, cHashMapTime1));

        System.out.println("\nТест 2. Колизии, все потоки пишут в 3 ключа\n");
        double hashMapTime2 = testCollisions(hashMap) / NSEC;
        double hashTableTime2 = testCollisions(hashTable) / NSEC;
        double syncMapTime2 = testCollisions(syncMap) / NSEC;
        double cHashMapTime2 = testCollisions(cHashMap) / NSEC;

        System.out.println("\nВремя для 2 теста");
        System.out.println(String.format("\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.",
                hashMapTime2, hashTableTime2, syncMapTime2, cHashMapTime2));

        System.out.println("\nТест 3. Производительность\n");
        double hashMapTime3 = testPerformance(hashMap) / NSEC;
        double hashTableTime3 = testPerformance(hashTable) / NSEC;
        double syncMapTime3 = testPerformance(syncMap) / NSEC;
        double cHashMapTime3 = testPerformance(cHashMap) / NSEC;
        
        System.out.println("\nВремя для 3 теста");
        System.out.println(String.format("\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.",
                hashMapTime3, hashTableTime3, syncMapTime3, cHashMapTime3));
    }

    // Тест 1 демонстрирует, что HashMap теряет данные, а потокобезопасные коллекции - нет
    private static long testLostData(Map<String, Integer> map) {
         // Выводим имя класса тестируемой коллекции
        System.out.print(String.format("\t%s", map.getClass().getSimpleName()));
        long totalTime = 0;

         // SAMPLES делаем 5 замеров для усреднения
        for (int k = 0; k < SAMPLES; k++) {
            map.clear(); // очищаем мап перед каждым замером
            
            AtomicInteger totalLost = new AtomicInteger(0);  // Сколько записей потеряно
            AtomicInteger totalExpected = new AtomicInteger(0);  // Сколько записей потеряно

            long start = System.nanoTime(); // Засекаем время начала

             // Создаем 50 потоков
            ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
            List<Callable<String>> tasks = new ArrayList<>();

            // Создаем THREADS задач (по одной на поток)
            for (int i = 0; i < THREADS; i++) {
                tasks.add(() -> {
                    String threadName = Thread.currentThread().getName(); // Имя текущего потока
                    int lost = 0;  // счетчик потерь 
                    
                    // Каждый поток делает ITERATIONS операций (1000 раз)
                    for (int j = 0; j < ITERATIONS; j++) {
                        String key = threadName + "_" + j; // уникальный ключ
                        int value = j;  // значение = номер итерации
                        
                        map.put(key, value); // записываем значение в мап
                        
                        Integer readValue = map.get(key);
                        if (readValue == null || !readValue.equals(value)) {
                            lost++; // если значение не найдено или не совпадает это потеря данных
                        }
                    }
                    
                    totalLost.addAndGet(lost);  // Добавляем потери этого потока в общий счетчик
                    totalExpected.addAndGet(ITERATIONS); // Добавляем количество ожидаемых операций 
                    
                    return "done";
                });
            }
            List<Future<String>> results = null;

            try {
                results = executorService.invokeAll(tasks);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // get results from futures
            try {
                for (Future<String> result : results) {
                    String s = result.get();
                    // System.out.println(s);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // shutdown executor service
            executorService.shutdown();

            long stop = System.nanoTime();
            totalTime += (stop - start);
            
            int expected = totalExpected.get();
            int lost = totalLost.get();
            
            if (lost > 0) {
                System.out.print(" [Потеряно: " + lost + "/" + expected + "]"); // выводим потери если есть
            } else {
                System.out.print(" [OK: " + expected + "]");
            }
        }

        System.out.println("...done.");
        return totalTime;
    }

    // Тест 2 Колизии
     private static long testCollisions(Map<String, Integer> map) {
    System.out.print(String.format("\t%s", map.getClass().getSimpleName()));
    long totalTime = 0;

    for (int k = 0; k < SAMPLES; k++) {
        map.clear();
        
        long start = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    String key = "key_" + (j % MAP_SIZE);
                    
                    // merge() гарантирует, что чтение и запись выполнятся как единое целое
                    map.merge(key, 1, Integer::sum);
                    
                    if (j % 100 == 0) {
                        Thread.yield();
                    }
                }
                return "done";
            });
        }

        List<Future<String>> results = null;

        try {
                results = executorService.invokeAll(tasks);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // get results from futures
            try {
                for (Future<String> result : results) {
                    String s = result.get();
                    // System.out.println(s);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // shutdown executor service
            executorService.shutdown();

        long stop = System.nanoTime();
        totalTime += (stop - start);
        
        // Подсчет итоговой суммы
        int sum = 0;
        for (Integer v : map.values()) {
            sum += v;
        }
        
        int expected = THREADS * ITERATIONS;
        
        if (sum < expected) {
            System.out.print(" [ПОТЕРЯНО: " + (expected - sum) + "/" + expected + "]");
        } else {
            System.out.print(" [OK: " + sum + "]");
        }
    }

    System.out.println("...done.");
    return totalTime;
}


    // Тест 3 на производительность
    // ConcurrentHashMap должна быть значительно быстрее Hashtable и synchronizedMap
    private static long testPerformance(Map<String, Integer> map) {
        System.out.print(String.format("\t%s", map.getClass().getSimpleName()));
        long totalTime = 0;

        for (int k = 0; k < SAMPLES; k++) {
            map.clear();
            
            // Заполняем мапу начальными данными для чтения
            for (int i = 0; i < 1000; i++) {
                map.put("base_" + i, i);
            }
            
            long start = System.nanoTime();

            ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
            List<Callable<String>> tasks = new ArrayList<>();

            for (int i = 0; i < THREADS; i++) {
                final int threadId = i;
                tasks.add(() -> {
                    Random random = ThreadLocalRandom.current();
                    
                    for (int j = 0; j < ITERATIONS * 10; j++) {
                        // 90% операций - чтение
                        if (random.nextInt(100) < 90) {
                            // Чтение существующего ключа
                            String key = "base_" + random.nextInt(1000);
                            map.get(key);
                        } else {
                            // 10% операций - запись
                            String key = "temp_" + threadId + "_" + j;
                            map.put(key, j);
                            // Сразу удаляем, чтобы мапа не росла
                            if (j % 10 == 0) {
                                map.remove(key);
                            }
                        }
                    }
                    return "done";
                });
            }

            List<Future<String>> results = null;

            try {
                results = executorService.invokeAll(tasks);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // get results from futures
            try {
                for (Future<String> result : results) {
                    String s = result.get();
                    // System.out.println(s);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // shutdown executor service
            executorService.shutdown();

            long stop = System.nanoTime();
            totalTime += (stop - start);
        }

        System.out.println("...done.");
        return totalTime;
    }
}