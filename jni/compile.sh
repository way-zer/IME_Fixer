export JAVA_HOME=/mnt/c/Program\ Files/Java/jdk1.8.0_212/

mkdir out
echo Compile x86
i686-w64-mingw32-g++ -shared -Wall -Wl,--kill-at -time -s \
  -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 \
  -o out/ime_Fixer64.dll *.cpp \
  -Wl,-Bdynamic -limm32 -lmsctf \
  -Wl,-Bstatic -lstdc++ -lpthread -static-libgcc -static-libstdc++
echo Compile x64
x86_64-w64-mingw32-g++ -shared -Wall -Wl,--kill-at -time -s \
  -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 \
  -o out/ime_Fixer64.dll *.cpp -L ./lib \
  -Wl,-Bdynamic -limm32 -lmsctf \
  -Wl,-Bstatic -lstdc++ -lpthread -static-libgcc -static-libstdc++