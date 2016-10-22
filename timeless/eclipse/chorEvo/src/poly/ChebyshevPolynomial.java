package poly;

public class ChebyshevPolynomial {
    static final Polynomial twoX = new Polynomial(2, 0);
    static final Polynomial one = new Polynomial(1);
    static final Polynomial oneX = new Polynomial(1, 0);

    /**
     * Calculates Chebyshev polynomial of specified integer order. Recursively generated using
     * relation Tk+1(x) = 2xTk(x) - Tk-1(x)
     * 
     * @return Chebyshev polynomial of specified order
     */
    public static Polynomial T(int order) {
        if (order == 0)
            return one;
        else if (order == 1)
            return oneX;
        else
            return Polynomial.minus(Polynomial.mult(T(order - 1), (twoX)), T(order - 2));
    }
    
    public static void main(String[] args) {
		ChebyshevPolynomial c = new ChebyshevPolynomial();
		Polynomial third = c.T(3);
		System.out.println(third);
		System.out.println(third.evaluate(2));
	}
}