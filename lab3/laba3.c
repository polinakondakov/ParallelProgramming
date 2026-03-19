#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <omp.h>  

void *heavy_task() {  // Функция с длительной вычислительной нагрузкой
  for (int i = 0; i < 1e8; i++) {
    sqrt(i);
  }
}

void openmp(int thread_num) { // Функция запуска параллельной области
  //omp_set_dynamic(0);
  //omp_set_num_threads(thread_num);
  //printf("OpenMP threads: %d\n", omp_get_num_threads());
  #pragma omp parallel for num_threads(thread_num)  // Директива: распараллелить цикл на указанное число потоков
  for (int i = 0; i < thread_num; i++) {
      heavy_task();  // Выполняется параллельно
  }
}

int main(int argc, char** argv) {
    int thread_num = atoi(argv[1]);  // Чтение количества потоков из аргументов
    
     // Замер времени (START)
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    
    openmp(thread_num);  // Запуск OpenMP версии
    
    // Замер времени (END)
    clock_gettime(CLOCK_MONOTONIC, &end);
    
    double time = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
    
    printf("\nOpenMP time: %.3f seconds\n", time);
    printf("Threads used: %d\n", thread_num);
    
    return 0;
}