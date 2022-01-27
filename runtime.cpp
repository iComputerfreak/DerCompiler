/*
 * Default Runtime for der Compiler.
 *
 * you can swap out the runtime.c with your own
 * or modify this file, but there have to be the
 * same function-definitions in the modified runtime.
 */

#include <exception>
#include <inttypes.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void main_func();

int main(int argc, char** argv) {
    try {
        main_func();
    } catch (std::exception& e) {
        std::cerr << "Exception catched: " << e.what() << "\n";
        return 1;
    }
    return 0;
}

void print_int(int32_t i) { printf("%d\n", i); }

void print_byte(int32_t b) { putchar(b); }

int32_t read_int() { return getchar(); }

void flush_out() { fflush(stdout); }

void* allocate(int64_t num, int64_t size) {
    //we create always some space on the stack
    //because new int[0] != new int[0] and could
    //otherwise return the same address or NULL
    size_t realsize = size <= 0 ? 1 : size;
    size_t realnum = num <= 0 ? 1 : num;
    void* res = calloc(realnum, realsize);
    //std::cout << "allocation - size: " << size << " num: " << num << " res: " << res <<"\n";
    return res;
}

#ifdef __cplusplus
}
#endif