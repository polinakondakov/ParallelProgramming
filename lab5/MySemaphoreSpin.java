import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger; // AtomicInteger - специальный класс, 
//который позволяет менять значение атомарно без использования Lock.

//Альтернативная реализация семафора, которая использует активное ожидание (spin) вместо блокировки.

public class MySemaphoreSpin extends Semaphore {

    private final AtomicInteger permits;  // атомарный счетчик разрешений
    private final int max;  // максимальное количество разрешений

    public MySemaphoreSpin(int permits) { 
        super(permits);
        this.permits = new AtomicInteger(permits);
        this.max = permits;
    }

    @Override
    public void acquire() { // захват разрешения
        System.out.println(Thread.currentThread().getName() + " [SPIN] пытается захватить");

        while (true) { // Крутимся, пока не получим разрешение
            int current = permits.get(); 

            if (current > 0) { // если есть свободные места
                if (permits.compareAndSet(current, current - 1)) {
                    System.out.println(Thread.currentThread().getName() + " [SPIN] получил. Осталось: " + (current - 1));
                    return;
                }
            } 
        }
    }

    @Override
    public void release() {
        while (true) { 
            int current = permits.get(); // читаем текущее значение свободных разрешений

            if (current < max) { 
                if (permits.compareAndSet(current, current + 1)) {
                    System.out.println(Thread.currentThread().getName() + " [SPIN] освободил. Доступно: " + (current + 1));
                    return;
                }
            } 
            else {
                return;
            }
        }
    }
}