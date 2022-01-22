package cf.wayzer.imeFix;

import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.SharedLibraryLoader;

import java.io.File;
import java.io.InputStream;

public class JNIImpl {
    public static native boolean setup();

    public static native boolean setPos(int x, int y);

    public static native boolean setOpen(boolean b);

    //used in native
    @SuppressWarnings("unused")
    public static void log(String str) {
        Log.infoTag("IMEFix_Native", str);
    }

    static {
        new SharedLibraryLoader() {
            @Override
            protected InputStream readFile(String path) {
                InputStream input = JNIImpl.class.getResourceAsStream("/" + path);
                if (input == null) {
                    throw new ArcRuntimeException("Unable to read file for extraction: " + path);
                } else {
                    return input;
                }
            }
            @Override
            protected Throwable loadFile(String sourcePath, String sourceCrc, File extractedFile) {
                try {
                    System.load(this.extractFile(sourcePath, sourceCrc, extractedFile).getAbsolutePath());
                    return null;
                } catch (Throwable var5) {
                    return var5;
                }
            }
        }.load("ime_Fixer");
    }
}
