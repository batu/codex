package player;

public class JMToBeads {

	//transform JM durations (1=4beats) to Beads ticks (16 ticks per beat)
	public static int toTicks(double duration){
		double numberOfBeats = duration * 4;
		double numberOfTicks = numberOfBeats * 16;
		return (int)numberOfTicks;
	}
	
	//transform JM duration (1=4beats) to milliseconds
	public static float toMillis(double duration){
		double ticks = toTicks(duration);
		return (float) (ticks * Player.intervalEnvelope.getCurrentValue() / 16);
	}
}
