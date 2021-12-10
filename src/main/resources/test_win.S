#
#
#

.section .data
s: .string "success"

.section .text
.global _main

_main:
    push $s
    call _printf
    add %esp, 4
    ret