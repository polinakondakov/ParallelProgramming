#include <stdio.h>
#include <time.h>

// Функция с использованием SSE инструкций через ассемблерную вставку
void sse(float a[], float b[], float c[]) {
  asm volatile (
                "movups %[a], %%xmm0\n"  
                "movups %[b], %%xmm1\n"   
                "mulps %%xmm1, %%xmm0\n" 
                "movups %%xmm0, %[c]\n"    
                :                          
                : [a]"m"(*a), [b]"m"(*b), [c]"m"(*c)  // входные: массивы a,b,c
                : "%xmm0", "%xmm1");      
  for (int i = 0; i < 4; i++) {
    //printf("%f ", c[i]);
  }
    //printf("\n");
}

// Обычная функция без SSE - последовательное умножение
void sequential(float a[], float b[], float c[]) {
  for(int i = 0; i < 4; i++) {
    c[i] = a[i] * b[i];  
    //printf("%f ", c[i]);
  }
  //printf("\n");
}

int main() {
  // Инициализация массивов
  float a[4] = {2.5, 3.5, 4.5, 5.5};
  float b[4] = {1.5, 2.5, 3.5, 4.5};
  float c[4];  // массив для результата
  
  clock_t start, end;  // для замера времени
  int iterations_num = 1000000;  // количество повторений, чтоб время замерить
  
  // SSE
  start = clock(); 
  for(int i = 0; i < iterations_num; i++) {
    sse(a, b, c); 
  }
  end = clock();  
  
  printf("Результат SSE: ");
  for(int i = 0; i < 4; i++) printf("%f ", c[i]);
  printf("\nВремя SSE: %f сек\n", (double)(end-start)/CLOCKS_PER_SEC);
  printf("\n");

  // Последовательное перемножение
  start = clock(); 
  for(int i = 0; i < iterations_num; i++) {
    sequential(a, b, c);  
  }
  end = clock();  
  
  printf("Результат последовательного умножения: ");
  for(int i = 0; i < 4; i++) printf("%f ", c[i]);
  printf("\nВремя последовательного: %f сек\n", (double)(end-start)/CLOCKS_PER_SEC);
  
  return 0;
}