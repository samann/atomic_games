javac -cp "gson-2.4.jar" src/com/goals/squad/othello/Othello.java src/com/goals/squad/othello/JsonObject.java

java -jar othello.jar -w ./ai_exe.sh -b random -u console -m 50
#./ai_exe.sh -b '{ "width": 8, "height": 8, "max-index": 63, "squares": ["-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","w","b","-","-","-","-","-","-","b","w","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-"] }' -p "black" -t 100
