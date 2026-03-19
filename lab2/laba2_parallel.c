#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <time.h>

int counter = 0;  // разделяемый ресурс: все потоки читают и пишут сюда
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER; 

void *heavy_task(void *i) { // функция потока
    int thread_num = *((int*) i);

    printf("\tThread #%d started\n", thread_num);
    pthread_mutex_lock(&mutex); 
    // Начало критической секции
    
    printf("\t\tThread #%d acquired mutex\n", thread_num); // "Я захватил мьютекс"
    counter++; 
    printf("\t\t\tThread #%d, counter: %d\n", thread_num, counter); 
    printf("\t\tThread #%d released mutex\n", thread_num); // "Я освобождаю мьютекс"
    
    pthread_mutex_unlock(&mutex);
    // Критическая секция завершена

    for (int i = 0; i < 1e8; i++) { // параллельно выполняются n таких циклов
        sqrt(i);
    }

    printf("\tThread #%d finished\n", thread_num); // Поток завершил свою работу
    free(i);  
}

void pthreads(int threads_num) { // создание и управление потоками
    pthread_t threads[threads_num];  
    int status; // если 0 - успех

    for (int i = 0; i < threads_num; i++) { // цикл создания потоков
        printf("MAIN: starting thread %d\n", i); // запуск потока

        int *thread_num = (int*) malloc(sizeof(int));
        *thread_num = i;

        // Создаём поток
        status = pthread_create(&threads[i], NULL, heavy_task, thread_num);

        if (status != 0) {
            fprintf(stderr, "pthread_create failed, error code %d\n", status);
            exit(EXIT_FAILURE);
        }
    }

    // Ждём завершения всех потоков
    for (int i = 0; i < threads_num; i++) {
        pthread_join(threads[i], NULL);
    }
}

int main(int argc, char** argv) {    
    int threads_num = atoi(argv[1]); // Преобразование аргумента из строки в число
    
    // Замер времени с использованием clock_gettime
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    
    pthreads(threads_num); // Запуск параллельной функции с заданным числом потоков
    
    clock_gettime(CLOCK_MONOTONIC, &end);
    
    double time = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
    
    // Очистка мьютекса 
    pthread_mutex_destroy(&mutex);
    
    printf("\nMultithreaded time: %.3f seconds\n", time);
    // Проверка: если counter == threads_num, значит мьютекс сработал корректно
    printf("Counter value: %d (expected: %d)\n", counter, threads_num);
    
    return 0;
}