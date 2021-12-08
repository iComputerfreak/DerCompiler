# https://de.wikipedia.org/wiki/Liste_von_Hallo-Welt-Programmen/Assembler
# Kompilieren mit
# "as -o hallo.o hallo.s; ld -o hallo hallo.o"

    .section .data
s: .string "Hallo Welt!\n"

    .section .text
    .globl _start
_start:
    movl $4,%eax      # Syscall-ID 4 (= sys_write)
    movl $1,%ebx      # Ausgabe-Dateideskriptor stdout (= 1)
    movl $s,%ecx      # Adresse des ersten Zeichens der Zeichenkette
    movl $12,%edx     # Länge der Zeichenkette (12 Zeichen)
    int $0x80         # Softwareinterrupt 0x80, um Syscall (write(1,s,12)) auszuführen

    movl $1,%eax      # Syscall-ID 1 (= sys_exit)
    movl $0,%ebx      # Rückgabewert 0 (= alles in Ordnung)
    int $0x80         # Softwareinterrupt 0x80 um Syscall (exit(0)) auszuführen