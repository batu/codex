package control;

public enum Command {
	START, STOP, CHANGE, NEWTEMPO, NEWMOOD;
	
	public static Command fromInt(int i){
		switch (i) {
		case 0:
			return Command.START;
		case -1:
			return Command.STOP;
		case 1:
			return Command.CHANGE;
		case 2:
			return Command.NEWTEMPO;
		case 3:
			return Command.NEWMOOD;
		default:
			return null;
		}
		
	}
}
