; https://de.wikipedia.org/wiki/Liste_von_Hallo-Welt-Programmen/Assembler
; Befehl zum Assemblen: nasm -felf hello.s
;                  && ld -o hello hello.o
    section data
hello db  "Hallo Welt!",0xa
len   equ $-hello

    section text
    global  _start
_start:
    mov eax, 4 ; write(stdout, hello, len)
    mov ebx, 1
    mov ecx, hello
    mov edx, len
    int 80h

    mov eax, 1 ; exit(0)
    mov ebx, 0
    int 80h