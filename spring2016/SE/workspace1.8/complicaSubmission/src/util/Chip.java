package util;

import java.lang.Comparable;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleObjectProperty;

public class Chip extends SimpleObjectProperty<Color>
    implements Comparable<Chip> {
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
        return color.equals(getValue());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Chip)) {
            return false;
        }
        Chip chip = (Chip)obj;
        
        return is(chip.getValue());
    }

    public int compareTo(Chip o) {
	return toString().compareTo(o.toString());
    }
}
