#pragma once

#ifndef MYHEADER
#define MYHEADER
#include <string>
#include <windows.h>
void log(std::string str);

extern HWND window;
int setupImm();
int setupCtf();

int setOpen(int open);
int setPos(int x, int y);
#endif // !MYHEADER