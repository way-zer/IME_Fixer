package cf.wayzer.imeFix;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Font;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.style.BaseDrawable;
import arc.scene.style.Drawable;
import arc.scene.ui.TextField;
import arc.util.Log;
import arc.util.OS;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class MODMain extends Mod {
    @Override
    public void init() {
        super.init();
        lateInit();
//        Timer.schedule(this::lateInit, 3f);
    }

    void lateInit() {
        if (!OS.isWindows && !OS.is64Bit) {
            Core.app.post(() -> Vars.ui.showText("IMEFix", "目前仅支持WIN64"));
            return;
        }
        try {
            Log.infoTag("IMEFix", "setup:" + JNIImpl.setup());
            Log.infoTag("IMEFix", "setOpen(false):" + JNIImpl.setOpen(false));
            final Method drawCursor = TextField.class.getDeclaredMethod("drawCursor", Drawable.class, Font.class, Float.TYPE, Float.TYPE);
            drawCursor.setAccessible(true);
            final Method getTextY = TextField.class.getDeclaredMethod("getTextY", Font.class, Drawable.class);
            getTextY.setAccessible(true);

            Log.infoTag("IMEFix", "加载完成");
            final AtomicReference<Element> lastFocus = new AtomicReference<>(null);
            final Vec2 vec2 = new Vec2();
            final Drawable fakeDrawable = new BaseDrawable() {
                @Override
                public void draw(float x, float y, float width, float height) {
                    vec2.set(x + 2, y + height - 2);
                }
            };
            Events.run(EventType.Trigger.update, () -> {
                Element focus = Core.scene.getKeyboardFocus();
                try {
                    if (focus instanceof TextField) {
                        TextField.TextFieldStyle style = ((TextField) focus).getStyle();
                        Drawable background = style.focusedBackground;
                        if (background == null) background = style.background;
                        float textY = (float) getTextY.invoke(focus, style.font, background);
                        float bgLeftWidth = background == null ? 0 : background.getLeftWidth();
                        drawCursor.invoke(focus, fakeDrawable, style.font, bgLeftWidth, textY);
                        Core.scene.stageToScreenCoordinates(focus.localToStageCoordinates(vec2));
                        JNIImpl.setPos((int) vec2.x, (int) vec2.y);
                    } else vec2.setZero();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (lastFocus.getAndSet(focus) != focus) {
                    boolean show = focus instanceof TextField;
                    Log.infoTag("IMEFix", "setOpen(" + show + "):" + JNIImpl.setOpen(show));
                    Log.infoTag("IMEFix", "" + vec2.x + ";" + vec2.y);
                    JNIImpl.setPos((int) vec2.x, (int) vec2.y);
                }
            });
        } catch (Throwable e) {
            Log.err(e);
        }
    }
}
