package jakarta.xml.bind;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DatatypeConverterImplTest {

    @Test
    public void parseIntegerAndLongKeepBehaviorWithoutRemoveOptionalPlus() {
        // the following code can be used to explore all 370 Unicode characters that are considered digits
        // all of these are accepted by BigInteger and Long.parseLong as valid digits
        // while only ASCII '0' to '9' are valid from an XML Schema perspective
        //
        // note that the old implementation only accepted ASCII digits after a leading plus sign but didn't care
        // for the rest of the string, so there is a mixture of correct and incorrect input validation

//        long numbersInUnicode = 0;
//        for (int ch = 0; ch <= Character.MAX_VALUE; ch++) {
//            int number = Character.digit(ch, 10);
//            if (number >= 0) {
//                numbersInUnicode++;
//                System.out.println("Code point " + ch + " is number " + number);
//            }
//        }
//        System.out.println("Total number of Unicode characters that are digits: " + numbersInUnicode);

        assertFalse(DataTypeConverterImplOld.codePath1Visited &&
                DataTypeConverterImplOld.codePath2Visited &&
                DataTypeConverterImplOld.codePath3Visited &&
                DataTypeConverterImplOld.codePath4Visited);

        // code path 1: empty string or just plus or minus
        String[] zeroLengthInputs = {"", "+", "-"};
        for (String input : zeroLengthInputs) {
            assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseInteger(input));
            assertThrows(NumberFormatException.class, () -> DataTypeConverterImplOld._parseInteger(input));
            assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseLong(input));
            assertThrows(NumberFormatException.class, () -> DataTypeConverterImplOld._parseLong(input));
        }

        // code path 1: single char string or no leading plus
        String[] testInputs = {"5", "0", "-3", "1234567890","-1234567890"};
        for (String input : testInputs) {
            assertEquals(DatatypeConverterImpl._parseInteger(input), DataTypeConverterImplOld._parseInteger(input));
            assertEquals(DatatypeConverterImpl._parseLong(input), DataTypeConverterImplOld._parseLong(input));
        }

        // code paths 2, 3, 4: leading plus followed by digit, dot, or invalid char
        // I would love to test all Unicode codepoints here, but unfortunately 1632 (Arabic-Indic Digit Zero) is the
        // first codepoint that is a valid digit but not an ASCII digit, so we limit the test to 1631 for now
//        for (int ch = 0; ch <= Character.MAX_VALUE; ch++) {
        for (int ch = 0; ch <= 1631; ch++) {
            String input = "+" + (char) ch + "123";
            System.out.println("Testing input: " + ch);
            System.out.println("Testing input: \"" + input + "\"");
            if (ch >= '0' && ch <= '9') {
                // code path 2
                assertEquals(DatatypeConverterImpl._parseInteger(input), DataTypeConverterImplOld._parseInteger(input));
                assertEquals(DatatypeConverterImpl._parseLong(input), DataTypeConverterImplOld._parseLong(input));
            } else {
                // code paths 3 and 4
                assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseInteger(input));
                assertThrows(NumberFormatException.class, () -> DataTypeConverterImplOld._parseInteger(input));
                assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseLong(input));
                assertThrows(NumberFormatException.class, () -> DataTypeConverterImplOld._parseLong(input));
            }
        }

        assertTrue(DataTypeConverterImplOld.codePath1Visited &&
                DataTypeConverterImplOld.codePath2Visited &&
                DataTypeConverterImplOld.codePath3Visited &&
                DataTypeConverterImplOld.codePath4Visited);
    }

    // copy of old implementation plus boolean flags to track code paths
    private static final class DataTypeConverterImplOld  {
        static boolean codePath1Visited = false;
        static boolean codePath2Visited = false;
        static boolean codePath3Visited = false;
        static boolean codePath4Visited = false;

        public static BigInteger _parseInteger(CharSequence s) {
            return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
        }

        public static long _parseLong(CharSequence s) {
            return Long.parseLong(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
        }

        private static CharSequence removeOptionalPlus(CharSequence s) {
            int len = s.length();

            if (len <= 1 || s.charAt(0) != '+') {
                codePath1Visited = true;
                return s;
            }

            s = s.subSequence(1, len);
            char ch = s.charAt(0);
            if ('0' <= ch && ch <= '9') {
                codePath2Visited = true;
                return s;
            }
            if ('.' == ch) {
                codePath3Visited = true;
                return s;
            }

            codePath4Visited = true;
            throw new NumberFormatException();
        }
    }


}