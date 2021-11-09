package cf.wayzer.imeFix;

import arc.util.*;
import com.sun.jna.*;
import com.sun.jna.platform.win32.BaseTSD.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinUser.*;

import java.util.concurrent.atomic.*;

@SuppressWarnings("UnusedReturnValue")
public interface IMEHelper extends Library{
    class HIMC extends WinNT.HANDLE{
    }

    @Structure.FieldOrder({"dwStyle", "pos", "area"})
    class CompositionForm extends Structure{
        public int dwStyle;
        public WinDef.POINT pos;
        public WinDef.RECT area;

        public static class ByReference extends CompositionForm implements Structure.ByReference{
        }
    }

    int CFS_POINT = 2;

    HIMC ImmGetContext(WinDef.HWND window);

    boolean ImmReleaseContext(WinDef.HWND window, HIMC himc);

    HIMC ImmAssociateContext(WinDef.HWND window, HIMC himc);


    HIMC ImmCreateContext();

    boolean ImmDestroyContext(HIMC himc);

    boolean ImmSetCompositionWindow(HIMC himc, CompositionForm.ByReference option);

    boolean ImmSetOpenStatus(HIMC himc, boolean open);

    boolean ImmIsUIMessageW(HWND hwnd, int msg, WPARAM wparam, LPARAM lparam);

    boolean ImmNotifyIME(HIMC hImc, DWORD dwAction, DWORD dwIndex, DWORD dwValue);

    IMEHelper INSTANCE = Native.load("imm32", IMEHelper.class);

    CompositionForm.ByReference option = new CompositionForm.ByReference(){{
        area = new WinDef.RECT();
        dwStyle = CFS_POINT;
        pos = new WinDef.POINT();
    }};

    AtomicReference<WinDef.HWND> lastWindow = new AtomicReference<>();

    int WM_IME_SetCONTEXT = 641;

    class MyWinProc implements WindowProc{
        static MyWinProc Instance;
        private final LONG_PTR oldP;

        MyWinProc(LONG_PTR oldP){
            this.oldP = oldP;
            Instance = this;
        }

        public WinDef.LRESULT callback(HWND hwnd, int msg, WPARAM wparam, LPARAM lparam){
            if(oldP.longValue() == 0)
                return User32.INSTANCE.DefWindowProc(hwnd, msg, wparam, lparam);
            if(msg == WM_IME_SetCONTEXT){
                Log.infoTag("IMEHelper", "Imm#" + msg + "#" + wparam + "#" + lparam);
                WinDef.LRESULT result = User32.INSTANCE.DefWindowProc(hwnd, msg, wparam, lparam);
                Log.infoTag("IMEHelper", result.toString());
                return result;
            }
            return User32.INSTANCE.CallWindowProc(oldP.toPointer(), hwnd, msg, wparam, lparam);
        }
    }

    /** 替换原版处理函数 */
    static void setup(){
        WinDef.HWND window = User32.INSTANCE.GetForegroundWindow();
        lastWindow.set(window);
        LONG_PTR oldP = User32.INSTANCE.GetWindowLongPtr(window, User32.GWL_WNDPROC);
        Log.infoTag("IMEHelper", "oldP=" + oldP);
        Pointer newP = CallbackReference.getFunctionPointer(new MyWinProc(oldP));
        User32.INSTANCE.SetWindowLongPtr(window, User32.GWL_WNDPROC, newP);

        HIMC himc = INSTANCE.ImmGetContext(window);
        INSTANCE.ImmDestroyContext(himc);
        INSTANCE.ImmReleaseContext(window, himc);
        himc = INSTANCE.ImmCreateContext();
        INSTANCE.ImmAssociateContext(window, himc);
        INSTANCE.ImmReleaseContext(window, himc);
    }

//    static boolean turn(boolean bool){
//        WinDef.HWND window = lastWindow.get();
//        HIMC himc = INSTANCE.ImmGetContext(window);
//        if(bool && himc == null){
//            himc = INSTANCE.ImmCreateContext();
//            INSTANCE.ImmAssociateContext(window, himc);
//        }else if(!bool && himc != null){
//            INSTANCE.ImmDestroyContext(himc);
//            INSTANCE.ImmAssociateContext(window, null);
//        }
//        INSTANCE.ImmReleaseContext(window, himc);
//        return himc != null;
//    }

    // dwAction for ImmNotifyIME
    DWORD NI_CONTEXTUPDATED = new DWORD(0x0003);
    DWORD NI_OPENCANDIDATE = new DWORD(0x0010);
    DWORD NI_CLOSECANDIDATE = new DWORD(0x0011);
    DWORD NI_SELECTCANDIDATESTR = new DWORD(0x0012);
    DWORD NI_CHANGECANDIDATELIST = new DWORD(0x0013);
    DWORD NI_FINALIZECONVERSIONRESULT = new DWORD(0x0014);
    DWORD NI_COMPOSITIONSTR = new DWORD(0x0015);
    DWORD NI_SETCANDIDATE_PAGESTART = new DWORD(0x0016);
    DWORD NI_SETCANDIDATE_PAGESIZE = new DWORD(0x0017);
    DWORD NI_IMEMENUSELECTED = new DWORD(0x0018);

    static boolean setOpen(boolean b){
        WinDef.HWND window = lastWindow.get();
        HIMC himc = INSTANCE.ImmGetContext(window);
        boolean bb = false;
        if(himc != null){
            bb = INSTANCE.ImmSetOpenStatus(himc, b);
            INSTANCE.ImmNotifyIME(himc, b ? NI_OPENCANDIDATE : NI_CLOSECANDIDATE, new DWORD(0), new DWORD(0));
            INSTANCE.ImmReleaseContext(window, himc);
        }
        return bb;
    }

    static void setPos(int x, int y){
        WinDef.HWND window = lastWindow.get();
        HIMC himc = INSTANCE.ImmGetContext(window);
        if(himc != null){
            option.pos.x = x;
            option.pos.y = y;
            INSTANCE.ImmSetCompositionWindow(himc, option);
            INSTANCE.ImmReleaseContext(window, himc);
        }
    }
}
