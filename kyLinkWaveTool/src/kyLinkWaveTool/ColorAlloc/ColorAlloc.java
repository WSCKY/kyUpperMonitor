package kyLinkWaveTool.ColorAlloc;

import java.awt.Color;
import java.util.ArrayList;

public final class ColorAlloc {
	private ArrayList<ColorUnit> Colors = null;

	public ColorAlloc() {
		Colors = new ArrayList<ColorUnit>();
		Colors.add(new ColorUnit(Color.RED));
		Colors.add(new ColorUnit(Color.GREEN));
		Colors.add(new ColorUnit(Color.BLUE));
		Colors.add(new ColorUnit(Color.YELLOW));
		Colors.add(new ColorUnit(Color.PINK));
		Colors.add(new ColorUnit(Color.CYAN));
		Colors.add(new ColorUnit(Color.LIGHT_GRAY));
		Colors.add(new ColorUnit(Color.MAGENTA));
	}

	public Color getColor() {
		for(ColorUnit c : Colors) {
			if(!c.used) {
				c.used = true;
				return c.color;
			}
		}
		return Color.RED;
	}
	public void releaseColor(Color c) {
		for(ColorUnit dc : Colors) {
			if(dc.color.equals(c)) {
				dc.used = false;
				return;
			}
		}
	}
}

class ColorUnit {
	public Color color = Color.WHITE;
	public boolean used = false;

	public ColorUnit() {}
	public ColorUnit(Color c) { color = c; }
}
