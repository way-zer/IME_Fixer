package cf.wayzer.imeFix;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.mod.*;

import java.util.concurrent.atomic.*;

public class MODMain extends Mod{
    @Override
    public void init(){
        super.init();
        lateInit();
//        Timer.schedule(this::lateInit, 3f);
    }

    void lateInit(){
        try{
            IMEHelper.setup();
            Log.infoTag("IMEFix", "finish setup");
            IMEHelper.setOpen(false);
            Log.infoTag("IMEFix", "加载完成");
        }catch(Throwable e){
            Log.err(e);
        }
        AtomicBoolean last = new AtomicBoolean(false);
        Events.run(EventType.Trigger.update, () -> {
            if(Vars.ui == null || Vars.ui.chatfrag == null) return;
            boolean now = Vars.ui.chatfrag.shown() || Vars.ui.scriptfrag.shown();
            if(last.compareAndSet(!now, now)){
                try{
                    IMEHelper.setOpen(now);
                }catch(Throwable e){
                    Log.err(e);
                }
                Log.infoTag("IMEFix", now ? "true" : "false");
                Log.infoTag("IMEFix", "" + 0 + ";" + (Core.graphics.getHeight() - 100));
            }
            if(now){
                IMEHelper.setPos(0, Core.graphics.getHeight() - 100);
            }
        });
    }
}
