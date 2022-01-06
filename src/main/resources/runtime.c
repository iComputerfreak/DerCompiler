/*
 * Default Runtime for der Compiler.
 *
 * you can swap out the runtime.c with your own
 * or modify this file, but there have to be the 
 * same function-definitions in the modified runtime.
 */

#include <inttypes.h>
#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void main_func();

int main(int argc, char** argv) { 
    main_func(); 
    return 0; 
}

void print_int(int32_t i) { printf("%d\n", i); }

void print_byte(int32_t b) { putchar(b); }

int32_t read_int() { return getchar(); }

void flush_out() { fflush(stdout); }

void* allocate(int64_t size) {
    //we create always some space on the stack
    //because new int[0] != new int[0] and could
    //otherwise return the same address or NULL
    size_t realsize = size <= 0 ? 1 : size;
    return malloc(realsize);
}

#ifdef __cplusplus
}
#endif
