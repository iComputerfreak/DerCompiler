#!/bin/bash
cd $(dirname "$0")

read -r -p "This will remove all .c, .S, .vcg and .dot files in the project directory. Continue? [Y/n] " response
case "$response" in
    [yY][eE][sS]|[yY]|'')
        rm -f *.c *.S *.vcg graphs/*.dot
        ;;
    *)
        exit 0
        ;;
esac