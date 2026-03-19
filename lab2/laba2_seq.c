#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>  

int main() {
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start); // Старт времени

    for (int k = 0; k < 8; k++) {        
        // 100 000 000 вычислений квадратного корня
        for (int j = 0; j < 1e8; j++) {
            sqrt(j); 
        }
    }
    clock_gettime(CLOCK_MONOTONIC, &end); // Финиш  времени
    
    // секунды + наносекунды (переведенные в секунды)
    double time = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
    
    printf("\nSequential time: %.3f seconds\n", time);

    return 0;
}