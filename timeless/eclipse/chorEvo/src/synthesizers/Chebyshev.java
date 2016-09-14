package synthesizers;

import poly.ChebyshevPolynomial;
import poly.Polynomial;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class Chebyshev extends UGen{
	
	Polynomial poly;

	public Chebyshev(AudioContext arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
	}
	
	public Chebyshev(AudioContext arg0, int order) {
		super(arg0, 1, 1);
		poly = ChebyshevPolynomial.T(order);
	}

	@Override
	public void calculateBuffer() {
		for (int i = 0; i < bufferSize; i++) {
			bufOut[0][i] = (float) poly.evaluate(bufIn[0][i]);
		}
	}

}
