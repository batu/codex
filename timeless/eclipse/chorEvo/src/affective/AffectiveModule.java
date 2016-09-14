package affective;

import player.Player;

//maps from bidimensional coordinates to music features
public class AffectiveModule {

	//parameters go from 0 to 100
	int valence = 50;
	int arousal = 50;
	
	Player player;
	
	public AffectiveModule(Player player) {
		this.player = player;
	}
	
	public void setAffectiveCoord(int valence, int arousal){
		this.valence = valence;
		this.arousal = arousal;
		
		updateFeatures();
	}

	private void updateFeatures() {

		//intensity (from 0.4 to 1)
		float volume = ( ((float)arousal /100f)* 0.6f ) + 0.4f;
		player.setGeneralVolume(volume);
		
		//timbre
		//cutoff from 440 to 9000
		float cutoffFreq = ( ((float)valence /100f)* 8560f ) + 440f;
		//resonance from 0.9 to 0
		float resonance = ( ((float)-valence /100f)* 0.9f ) + 0.9f ;
		player.setTimbre(cutoffFreq, resonance);
		//TODO either remove or think of a way of reintroducing it
		
		//tempo (from 50 to 120)
		int tempo = (int) (( ((float)arousal /100f)* 70f ) + 50);
		player.setBPM(tempo);
		
		//rhythm strength (from -1 to -1)
		float rhythm = ( ((float)arousal /100f)* 2f ) -1f;
		player.setRhythmVolume(rhythm);
		
		//irregularity (from 0 to 1)
		float irregularity = ((float)-valence/100f )+1f;
		player.setIrregularity(irregularity);
		//TODO fix irregularity
		
		//dissonances (from 0 to 1)
		float dissonance = ((float)-valence/100f )+1f;
		player.setDissonance(dissonance);
		
		//update music
		player.updatePhrases();
	}
	
	public void setIntensity(int arousal){
		//intensity (from 0.5 to 5)
				float volume = ( ((float)arousal /100f)* 4.5f ) + 0.5f;
				player.setGeneralVolume(volume);
				
				//update music
				player.updatePhrases();
	}
	
	public void setTimbre(int valence){
		//cutoff from 440 to 9000
				float cutoffFreq = ( ((float)valence /100f)* 8560f ) + 440f;
				//resonance from 0.9 to 0
				float resonance = ( ((float)-valence /100f)* 0.9f ) + 0.9f ;
				player.setTimbre(cutoffFreq, resonance);
				
				//update music
				player.updatePhrases();
	}
	
	public void setTempo(int arousal){
		//tempo (from 60 to 180)
				int tempo = (int) (( ((float)arousal /100f)* 120f ) + 60);
				player.setBPM(tempo);
				
				//update music
				player.updatePhrases();
	}
	
	public void setStrength(int arousal){
		//rhythm strength (from -1 to -1)
				float rhythm = ( ((float)arousal /100f)* 2f ) -1f;
				player.setRhythmVolume(rhythm);
				
				//update music
				player.updatePhrases();
	}
	
	public void setIrregularity(int valence){
		//irregularity (from 0 to 1)
				float irregularity = ((float)-valence/100f )+1f;
				player.setIrregularity(irregularity);
				
				//update music
				player.updatePhrases();
	}
	
	public void setDissonance(int valence){
		//dissonances (from 0 to 1)
				float dissonance = ((float)-valence/100f )+1f;
				player.setDissonance(dissonance);
				
				//update music
				player.updatePhrases();
	}

	public void setAffectiveCoord(double valence2, double arousal2) {
		int val = (int) (valence2*50) + 50;
		int aro = (int) (arousal2*50) + 50;
		if (val > 100)
			val = 100;
		if (aro > 100)
			aro = 100;
		setAffectiveCoord(val, aro);
	}
}
