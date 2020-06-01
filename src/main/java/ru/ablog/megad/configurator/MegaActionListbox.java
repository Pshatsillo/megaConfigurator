package ru.ablog.megad.configurator;

import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;

import java.util.ArrayList;

public class MegaActionListbox extends ActionListBox {
    private ArrayList<Runnable> listeners;

    public MegaActionListbox() {
        listeners = new ArrayList<>();
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        Object selectedItem = this.getSelectedItem();
        if (selectedItem != null && (keyStroke.getKeyType() == KeyType.Enter || keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ' || keyStroke.getKeyType() == KeyType.MouseEvent) && this.isFocused()) {
            if (keyStroke.getKeyType() == KeyType.MouseEvent) {
                int newIndex = this.getIndexByMouseAction((MouseAction)keyStroke);
                if (newIndex != this.getSelectedIndex()) {
                    return super.handleKeyStroke(keyStroke);
                }
            }

            ((Runnable)selectedItem).run();
            return Result.HANDLED;
        } else {
            if(keyStroke.getKeyType() == KeyType.ArrowDown){
                int index = this.getSelectedIndex() +1;
                if(index < this.getItemCount()) {
                    Object item = this.getItemAt(index);
                    ((Runnable) item).run();
                    return super.handleKeyStroke(keyStroke);
                } else {
                    return Result.HANDLED;
                }
            } else if(keyStroke.getKeyType() == KeyType.ArrowUp){
                int index = this.getSelectedIndex() - 1;
                if(index >= 0) {
                    Object item = this.getItemAt(index);
                    ((Runnable) item).run();
                    return super.handleKeyStroke(keyStroke);
                } else {
                    return Result.HANDLED;
                }
            }
            return super.handleKeyStroke(keyStroke);
        }
    }

    public void addListener() {

    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }
}
