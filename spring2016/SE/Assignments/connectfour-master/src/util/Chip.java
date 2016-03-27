package util;

import javafx.scene.paint.Color;
import javafx.beans.property.SimpleObjectProperty;

public class Chip extends SimpleObjectProperty<Color> {
    public static final Color INITIAL_COLOR = Color.TRANSPARENT;

    public Chip(Color color) {
        super(color);
    }
    
    public Chip(Chip chip) {
        this(chip.getValue());
    }
    
    public Chip() {
        this(Chip.INITIAL_COLOR);
    }

    public void setValue(Chip chip) {
        setValue(chip.getValue());
    }

    public String toString() {
        return getValue().toString();
    }

    public boolean is(Color color) {
        return getValue().equals(color);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Chip)) {
            return false;
        }
        Chip chip = (Chip)obj;
        
        return is(chip.getValue());
    }
}
