package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;

/**
 * Dialog displayed when users click the "Add Gradle Tasks" button in the settings panel
 */
public class GradleDialog extends PopTable  {
    public GradleDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(20).padTop(30).padBottom(30);

        //title
        Label label = new Label(prop.getProperty("gradleTasksPrompt"), skin, "field");
        label.setWrap(true);
        add(label).growX();

        //explanation
        row();
        label = new Label(prop.getProperty("gradleTasksTip"), skin, "description");
        label.setWrap(true);
        add(label).growX().spaceTop(20);

        //gradle commands textfield
        row();
        TextField textField = new TextField("", skin);
        add(textField).width(350);
        addIbeamListener(textField);
        stage.setKeyboardFocus(textField);

        //ok button
        row();
        TextButton textButton = new TextButton("OK", skin);
        add(textButton).prefWidth(140).spaceTop(20);
        addHandListener(textButton);
        onChange(textButton, () -> hide());
    }

    public static PopTable show() {
        GradleDialog dialog = new GradleDialog();
        dialog.show(stage);
        return dialog;
    }
}
