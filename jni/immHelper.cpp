#include "myHeader.h"
#include <imm.h>

LONG_PTR oldP;
LRESULT winProc(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam)
{
    switch (msg)
    {
    // case WM_INPUTLANGCHANGE:
    case WM_IME_SETCONTEXT:
    case WM_IME_COMPOSITION:
    case WM_IME_ENDCOMPOSITION:
    case WM_IME_STARTCOMPOSITION:
        return DefWindowProc(hWnd, msg, wParam, lParam);

    case WM_KEYDOWN:
    case WM_SYSKEYDOWN:
        UINT key = (lParam >> 16) & 0xff;
        bool extKey = (lParam >> 17) & 1;
        UINT vKey = MapVirtualKey(key + extKey * 0xe000, MAPVK_VSC_TO_VK);
        // log("KEYDOWN " + std::to_string(wParam) + " " + std::to_string(key) + " " + std::to_string(vKey));

        if (wParam == VK_PROCESSKEY) //in COMPOSITION
        {
            //block some keys, according to SDLInput where see these keys as keyTyped.
            switch (vKey)
            {
            case VK_BACK:
            case VK_TAB:
            case VK_RETURN:
            case VK_DELETE:
            case VK_LEFT:
            case VK_RIGHT:
                log("Block Key " + std::to_string(vKey));
                return 0;
            }
        }
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
    static HIMC bakHIMC;
    HIMC himc = ImmGetContext(window);
    if (!open && himc != NULL)
    {
        if (bakHIMC != NULL)
            log("WARN: bakHIMC is not NULL when close");
        bakHIMC = himc;
        ImmAssociateContext(window, NULL);
        return true;
    }
    else if (open && himc == NULL)
    {
        if (bakHIMC == NULL)
            log("WARN: bakHIMC is NULL when open");
        ImmAssociateContext(window, bakHIMC);
        ImmReleaseContext(window, bakHIMC);
        bakHIMC = NULL;
        return true;
    }
    else
        return false;
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