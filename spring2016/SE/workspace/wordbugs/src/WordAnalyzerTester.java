/*
 * http://www.horstmann.com/bigj/help/debugger/tutorial.html
 */
public class WordAnalyzerTester {
    public void testRepeats(String provided, char expected) {
        WordAnalyzer wa = new WordAnalyzer(provided);
        assert wa.firstRepeatedCharacter() == expected;
    }

    public void testMultiple(String provided, char expected) {
        WordAnalyzer wa = new WordAnalyzer(provided);
        assert wa.firstMultipleCharacter() == expected;
    }

    public void testCounts(String provided, int expected) {
        WordAnalyzer wa = new WordAnalyzer(provided);
        assert wa.countRepeatedCharacters() == expected;
    }

    public static void main(String[] args) {
        WordAnalyzerTester wordAnalyzerTester = new WordAnalyzerTester();

        wordAnalyzerTester.testRepeats("aardvark", 'a');
        wordAnalyzerTester.testRepeats("roomate", 'o');
        wordAnalyzerTester.testRepeats("mate", (char)0);
        wordAnalyzerTester.testRepeats("test", (char)0);

        wordAnalyzerTester.testMultiple("missisippi", 'i');
        wordAnalyzerTester.testMultiple("mate", (char)0);
        //wordAnalyzerTester.testMultiple("test", 't');

        wordAnalyzerTester.testCounts("mississippiii", 4);
        wordAnalyzerTester.testCounts("test", 0);
        wordAnalyzerTester.testCounts("aabbcdaaaabb", 4);
        
        System.out.println("Tests completed");
    }
}
