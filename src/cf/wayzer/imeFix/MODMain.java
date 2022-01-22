package cf.wayzer.imeFix;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.OS;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.util.concurrent.atomic.AtomicBoolean;

public class MODMain extends Mod {
    @Override
    public void init() {
        super.init();
        lateInit();
//        Timer.schedule(this::lateInit, 3f);
    }

    void lateInit() {
        if (!OS.isWindows && !OS.is64Bit) {
            Core.app.post(() -> {
                Vars.ui.showText("IMEFix", "目前仅支持WIN64");
            });
            return;
        }
        try {
            Log.infoTag("IMEFix", "setup:" + JNIImpl.setup());
            Log.infoTag("IMEFix", "setOpen(false):" + JNIImpl.setOpen(false));
            Log.infoTag("IMEFix", "加载完成");
        } catch (Throwable e) {
            Log.err(e);
        }
        AtomicBoolean last = new AtomicBoolean(false);
        Events.run(EventType.Trigger.update, () -> {
            if (Vars.ui == null || Vars.ui.chatfrag == null) return;
            boolean now = Vars.ui.chatfrag.shown() || Vars.ui.scriptfrag.shown();
            if (last.compareAndSet(!now, now)) {
                Log.infoTag("IMEFix", "setOpen(" + now + "):" + JNIImpl.setOpen(now));
                Log.infoTag("IMEFix", "" + 0 + ";" + (Core.graphics.getHeight() - 100));
            }
            if (now) {
                JNIImpl.setPos(0, Core.graphics.getHeight() - 100);
            }
        });
    }
}
