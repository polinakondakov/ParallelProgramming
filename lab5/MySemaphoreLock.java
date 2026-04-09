import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition; 
import java.util.concurrent.locks.ReentrantLock; 

public class MySemaphoreLock extends Semaphore { 

    private int permits; // сколько ещё потоков могут зайти (кол-во доступных разрешений)
    private final int max; // максимальное количество разрешений

    private final ReentrantLock lock = new ReentrantLock();   
    private final Condition condition = lock.newCondition();  

    public MySemaphoreLock(int permits) {
        super(permits);           
        this.permits = permits;  
        this.max = permits;       
    }

    @Override 
    public void acquire() throws InterruptedException { // взять разрешение
        lock.lock(); // блокируем доступ к этому методу для других потоков

        try { // Начало безопасной секции
            System.out.println(Thread.currentThread().getName() + " [LOCK] пытается захватить. Доступно: " + permits);

            while (permits == 0) { // если нет свободных разрешений проверяем снова после пробуждения!
                System.out.println(Thread.currentThread().getName() + " [LOCK] ждет..."); 
                condition.await(); 
            }
            // есть свободные разрешения
            permits--; //  Уменьшает количество свободных разрешений на 1
            System.out.println(Thread.currentThread().getName() + " [LOCK] получил. Осталось: " + permits); 

        } 
        finally { 
            lock.unlock();
        }
    }

    @Override
    public void release() {   // освобождение разрешения
        lock.lock(); // захват замка Чтобы ТОЛЬКО ОДИН поток мог менять переменную permits
        try { // Начало безопасной секции 
            if (permits < max) { 
                permits++; 
                System.out.println(Thread.currentThread().getName() + " [LOCK] освободил. Доступно: " + permits);
                condition.signal(); //  Будит ОДИН поток, который уснул на condition.await()
            }
        } 
        finally {
            lock.unlock(); // выполнится всегда даже если будет какая то ошибка
        }
    }
}