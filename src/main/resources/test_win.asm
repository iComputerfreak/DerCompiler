; https://stackoverflow.com/questions/1023593/how-to-write-hello-world-in-assembler-under-windows
; ----------------------------------------------------------------------------
; helloworld.asm
;
; This is a Win32 console program that writes "success" on one line and
; then exits.  It needs to be linked with a C library.
; ----------------------------------------------------------------------------

    global  _main
    extern  _printf

    section .text
_main:
    push    message
    call    _printf
    add     esp, 4
    ret
message:
    db  'success', 0