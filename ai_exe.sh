#!/usr/bin/env bash
fboard=''
fcolor=''
ftime=''

while getopts ':b:p:t:' flag; do
  case "${flag}" in
    b) fboard=${OPTARG} ;;
    p) fcolor=${OPTARG} ;;
    t) ftime=${OPTARG} ;;

    *) error "Unexpected option ${flag}" ;;
  esac
done

#echo "fboard = ${fboard} "
#echo "fcolor = ${fcolor} "
#echo "ftime = ${ftime} "

java Othello ${fboard} ${fcolor} ${ftime}
