mkdir out
i686-w64-mingw32-g++ -shared -Wall -Wl,--kill-at \
  -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 \
  -o out/ime_Fixer.dll *.cpp \
  -limm32 -lmsctf
x86_64-w64-mingw32-g++ -shared -Wall -Wl,--kill-at \
  -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 \
  -o out/ime_Fixer64.dll *.cpp \
  -limm32 -L ./lib -lmsctf