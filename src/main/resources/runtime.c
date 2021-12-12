/*
 * Default Runtime for der Compiler.
 *
 * you can swap out the runtime.c with your own
 * or modify this file, but there have to be the 
 * same function-definitions in the modified runtime.
 */
#ifdef __cplusplus
extern "C" {
#endif

#include <inttypes.h>
#include <stdio.h>
#include <stdlib.h>

extern void main_func();

int main(int argc, char** argv) { 
    main_func(); 
    return 0; 
}

void print_int(int32_t i) { printf("%d", i); }

void print_byte(int32_t b) { putchar(b); }

int32_t read_int() {
    int32_t i;
    scanf("%d", &i);
    return i;
}

void flush_out() { fflush(stdout); }

void* allocate(int32_t size) {
    size_t realsize = size <= 0 ? 1 : size;
    return malloc(realsize);
}

#ifdef __cplusplus
}
#endif
