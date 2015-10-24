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

java -cp .:out/production/atomic_games:gson-2.4.jar com.goals.squad.othello.Othello "${fboard}" "${fcolor}" ${ftime}
