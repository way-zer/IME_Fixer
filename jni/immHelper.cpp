#include "myHeader.h"
#include <imm.h>

LONG_PTR oldP;
LRESULT winProc(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam)
{
    switch (msg)
    {
    // case WM_INPUTLANGCHANGE:
    case WM_IME_SETCONTEXT:
    case WM_IME_STARTCOMPOSITION:
    case WM_IME_COMPOSITION:
    case WM_IME_ENDCOMPOSITION:
        return DefWindowProc(hWnd, msg, wParam, lParam);
        break;
    default:
        break;
    }
    return CallWindowProc((WNDPROC)oldP, hWnd, msg, wParam, lParam);
}

int setupImm()
{
    oldP = GetWindowLongPtr(window, GWLP_WNDPROC);
    if (oldP == (LONG_PTR)NULL)
    {
        log("Fail to get WNDPROC");
        return false;
    }
    log("oldP=" + std::to_string(oldP));
    SetWindowLongPtr(window, GWLP_WNDPROC, (LONG_PTR)winProc);

    HIMC himc = ImmGetContext(window);
    log("oldHIMC=" + std::to_string((long long)himc));
    if (himc == NULL)
    {
        himc = ImmCreateContext();
        ImmAssociateContext(window, himc);
    }
    log("HIMC=" + std::to_string((long long)himc));
    ImmReleaseContext(window, himc);
    return true;
}

int setOpen(int open)
{
    HIMC himc = ImmGetContext(window);
    if (himc == NULL)
    {
        log("setOpen: Fail get Context");
        return false;
    }
    int bb = ImmSetOpenStatus(himc, open);
    ImmReleaseContext(window, himc);
    return bb;
    // bb = bb && ImmNotifyIME(himc,open)
}

int setPos(int x, int y)
{
    static COMPOSITIONFORM option = {CFS_POINT};
    if (option.ptCurrentPos.x == x && option.ptCurrentPos.y == y)
        return true;
    option.ptCurrentPos.x = x;
    option.ptCurrentPos.y = y;
    HIMC himc = ImmGetContext(window);
    if (himc != NULL) //When not focus, context is Null
    {
        int bb = ImmSetCompositionWindow(himc, &option);
        ImmReleaseContext(window, himc);
        return bb;
    }
    return false;
}