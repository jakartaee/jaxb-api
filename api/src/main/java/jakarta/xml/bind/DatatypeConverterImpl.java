/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;

/**
 * This class is the Jakarta XML Binding CI's default implementation of the
 * {@link DatatypeConverterInterface}.
 *
 * <p>
 * When client applications specify the use of the static print/parse
 * methods in {@link DatatypeConverter}, it will delegate
 * to this class.
 *
 * <p>
 * This class is responsible for whitespace normalization.
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li></ul>
 * @since JAXB 2.1
 */
final class DatatypeConverterImpl implements DatatypeConverterInterface {

    /**
     * To avoid re-creating instances, we cache one instance.
     */
    public static final DatatypeConverterInterface theInstance = new DatatypeConverterImpl();

    protected DatatypeConverterImpl() {
    }

    @Override
    public String parseString(String lexicalXSDString) {
        return lexicalXSDString;
    }

    @Override
    public BigInteger parseInteger(String lexicalXSDInteger) {
        return _parseInteger(lexicalXSDInteger);
    }

    public static BigInteger _parseInteger(CharSequence s) {
        return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    @Override
    public String printInteger(BigInteger val) {
        return _printInteger(val);
    }

    public static String _printInteger(BigInteger val) {
        if (null == val) throw new IllegalArgumentException("val is null");
        return val.toString();
    }

    @Override
    public int parseInt(String s) {
        return _parseInt(s);
    }

    /**
     * Faster but less robust {@code String->int} conversion.
     * <p>
     * Note that:
     * <ol>
     *  <li>XML Schema allows '+', but {@link Integer#valueOf(String)} is not.
     *  <li>XML Schema allows leading and trailing (but not in-between) whitespaces.
     *      {@link Integer#valueOf(String)} doesn't allow any.
     * </ol>
     */
    public static int _parseInt(CharSequence s) {
        int len = s.length();
        int sign = 1;

        int r = 0;

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            if (WhiteSpaceProcessor.isWhiteSpace(ch)) {
                // skip whitespace
            } else if ('0' <= ch && ch <= '9') {
                r = r * 10 + (ch - '0');
            } else if (ch == '-') {
                sign = -1;
            } else if (ch == '+') {
                // noop
            } else {
                throw new NumberFormatException("Not a number: " + s);
            }
        }

        return r * sign;
    }

    @Override
    public long parseLong(String lexicalXSLong) {
        return _parseLong(lexicalXSLong);
    }

    public static long _parseLong(CharSequence s) {
        return Long.parseLong(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    @Override
    public short parseShort(String lexicalXSDShort) {
        return _parseShort(lexicalXSDShort);
    }

    public static short _parseShort(CharSequence s) {
        return (short) _parseInt(s);
    }

    @Override
    public String printShort(short val) {
        return _printShort(val);
    }

    public static String _printShort(short val) {
        return String.valueOf(val);
    }

    @Override
    public BigDecimal parseDecimal(String content) {
        return _parseDecimal(content);
    }

    public static BigDecimal _parseDecimal(CharSequence content) {
        content = WhiteSpaceProcessor.trim(content);

        if (content.length() <= 0) {
            return null;
        }

        return new BigDecimal(content.toString());

        // from purely XML Schema perspective,
        // this implementation has a problem, since
        // in xs:decimal "1.0" and "1" is equal whereas the above
        // code will return different values for those two forms.
        //
        // the code was originally using com.sun.msv.datatype.xsd.NumberType.load,
        // but a profiling showed that the process of normalizing "1.0" into "1"
        // could take non-trivial time.
        //
        // also, from the user's point of view, one might be surprised if
        // 1 (not 1.0) is returned from "1.000"
    }

    @Override
    public float parseFloat(String lexicalXSDFloat) {
        return _parseFloat(lexicalXSDFloat);
    }

    public static float _parseFloat(CharSequence _val) {
        String s = WhiteSpaceProcessor.trim(_val).toString();
        /* Incompatibilities of XML Schema's float "xfloat" and Java's float "jfloat"

         * jfloat.valueOf ignores leading and trailing whitespaces,
        whereas this is not allowed in xfloat.
         * jfloat.valueOf allows "float type suffix" (f, F) to be
        appended after float literal (e.g., 1.52e-2f), whereas
        this is not the case of xfloat.

        gray zone
        ---------
         * jfloat allows ".523". And there is no clear statement that mentions
        this case in xfloat. Although probably this is allowed.
         *
         */

        if (s.equals("NaN")) {
            return Float.NaN;
        }
        if (s.equals("INF")) {
            return Float.POSITIVE_INFINITY;
        }
        if (s.equals("-INF")) {
            return Float.NEGATIVE_INFINITY;
        }

        if (s.isEmpty()
                || !isDigitOrPeriodOrSign(s.charAt(0))
                || !isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
            throw new NumberFormatException();
        }

        // these screening process is necessary due to the wobble of Float.valueOf method
        return Float.parseFloat(s);
    }

    @Override
    public String printFloat(float v) {
        return _printFloat(v);
    }

    public static String _printFloat(float v) {
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    @Override
    public double parseDouble(String lexicalXSDDouble) {
        return _parseDouble(lexicalXSDDouble);
    }

    public static double _parseDouble(CharSequence _val) {
        String val = WhiteSpaceProcessor.trim(_val).toString();

        if (val.equals("NaN")) {
            return Double.NaN;
        }
        if (val.equals("INF")) {
            return Double.POSITIVE_INFINITY;
        }
        if (val.equals("-INF")) {
            return Double.NEGATIVE_INFINITY;
        }

        if (val.isEmpty()
                || !isDigitOrPeriodOrSign(val.charAt(0))
                || !isDigitOrPeriodOrSign(val.charAt(val.length() - 1))) {
            throw new NumberFormatException(val);
        }


        // these screening process is necessary due to the wobble of Float.valueOf method
        return Double.parseDouble(val);
    }

    @Override
    public boolean parseBoolean(String lexicalXSDBoolean) {
        return _parseBoolean(lexicalXSDBoolean);
    }

    public static Boolean _parseBoolean(CharSequence literal) {
        if (literal == null) {
            throw new IllegalArgumentException("String \"null\" is not valid boolean value.");
        }

        int i = 0;
        int len = literal.length();
        char ch;
        boolean value = false;

        if (literal.length() <= 0) {
            throw new IllegalArgumentException("String \"\" is not valid boolean value.");
        }

        do {
            ch = literal.charAt(i++);
        } while (WhiteSpaceProcessor.isWhiteSpace(ch) && i < len);

        int strIndex = 0;

        switch (ch) {
            case '1':
                value = true;
                break;
            case '0':
                value = false;
                break;
            case 't':
                String strTrue = "rue";
                do {
                    ch = literal.charAt(i++);
                } while ((strTrue.charAt(strIndex++) == ch) && i < len && strIndex < 3);

                if (strIndex == 3 && strTrue.charAt(strIndex - 1) == ch) {
                    value = true;
                } else {
                    throw new IllegalArgumentException("String \"" + literal + "\" is not valid boolean value.");
                }

                break;
            case 'f':
                String strFalse = "alse";
                do {
                    ch = literal.charAt(i++);
                } while ((strFalse.charAt(strIndex++) == ch) && i < len && strIndex < 4);


                if (strIndex == 4 && strFalse.charAt(strIndex - 1) == ch) {
                    value = false;
                } else {
                    throw new IllegalArgumentException("String \"" + literal + "\" is not valid boolean value.");
                }

                break;
        }

        while (i < len && WhiteSpaceProcessor.isWhiteSpace(literal.charAt(i))) {
            i++;
        }

        if (i == len) {
            return value;
        }
        throw new IllegalArgumentException("String \"" + literal + "\" is not valid boolean value.");
    }

    @Override
    public String printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    public static String _printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    @Override
    public byte parseByte(String lexicalXSDByte) {
        return _parseByte(lexicalXSDByte);
    }

    public static byte _parseByte(CharSequence literal) {
        return (byte) _parseInt(literal);
    }

    @Override
    public String printByte(byte val) {
        return _printByte(val);
    }

    public static String _printByte(byte val) {
        return String.valueOf(val);
    }

    @Override
    public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
        return _parseQName(lexicalXSDQName, nsc);
    }

    /**
     * @return null if fails to convert.
     */
    public static QName _parseQName(CharSequence text, NamespaceContext nsc) {
        int length = text.length();

        // trim whitespace
        int start = 0;
        while (start < length && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start))) {
            start++;
        }

        int end = length;
        while (end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1))) {
            end--;
        }

        if (end == start) {
            throw new IllegalArgumentException("input is empty");
        }


        String uri;
        String localPart;
        String prefix;

        // search ':'
        int idx = start + 1;    // no point in searching the first char. that's not valid.
        while (idx < end && text.charAt(idx) != ':') {
            idx++;
        }

        if (idx == end) {
            uri = nsc.getNamespaceURI("");
            localPart = text.subSequence(start, end).toString();
            prefix = "";
        } else {
            // Prefix exists, check everything
            prefix = text.subSequence(start, idx).toString();
            localPart = text.subSequence(idx + 1, end).toString();
            uri = nsc.getNamespaceURI(prefix);
            // uri can never be null according to javadoc,
            // but some users reported that there are implementations that return null.
            if (uri == null || uri.isEmpty()) // crap. the NamespaceContext interface is broken.
            // error: unbound prefix
            {
                throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
            }
        }

        return new QName(uri, localPart, prefix);
    }

    @Override
    public Calendar parseDateTime(String lexicalXSDDateTime) {
        return _parseDateTime(lexicalXSDDateTime);
    }

    public static GregorianCalendar _parseDateTime(CharSequence s) {
        String val = WhiteSpaceProcessor.trim(s).toString();
        return datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
    }

    @Override
    public String printDateTime(Calendar val) {
        return _printDateTime(val);
    }

    public static String _printDateTime(Calendar val) {
        if (null == val) throw new IllegalArgumentException("val is null");
        return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
    }

    @Override
    public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
        return _parseBase64Binary(lexicalXSDBase64Binary);
    }

    @Override
    public byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    @Override
    public String printHexBinary(byte[] data) {
        if (null == data) throw new IllegalArgumentException("data is null");
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    @Override
    public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
        return _parseLong(lexicalXSDUnsignedInt);
    }

    @Override
    public String printUnsignedInt(long val) {
        return _printLong(val);
    }

    @Override
    public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
        return _parseInt(lexicalXSDUnsignedShort);
    }

    @Override
    public Calendar parseTime(String lexicalXSDTime) {
        return datatypeFactory.newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
    }

    @Override
    public String printTime(Calendar val) {
        if (null == val) throw new IllegalArgumentException("val is null");
        return CalendarFormatter.doFormat("%h:%m:%s%z", val);
    }

    @Override
    public Calendar parseDate(String lexicalXSDDate) {
        return datatypeFactory.newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
    }

    @Override
    public String printDate(Calendar val) {
        return _printDate(val);
    }

    public static String _printDate(Calendar val) {
        if (null == val) throw new IllegalArgumentException("val is null");
        return CalendarFormatter.doFormat("%Y-%M-%D%z",val);
    }

    @Override
    public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
        return lexicalXSDAnySimpleType;
//        return (String)SimpleURType.theInstance._createValue( lexicalXSDAnySimpleType, null );
    }

    @Override
    public String printString(String val) {
//        return StringType.theInstance.convertToLexicalValue( val, null );
        return val;
    }

    @Override
    public String printInt(int val) {
        return _printInt(val);
    }

    public static String _printInt(int val) {
        return String.valueOf(val);
    }

    @Override
    public String printLong(long val) {
        return _printLong(val);
    }

    public static String _printLong(long val) {
        return String.valueOf(val);
    }

    @Override
    public String printDecimal(BigDecimal val) {
        return _printDecimal(val);
    }

    public static String _printDecimal(BigDecimal val) {
        if (null == val) throw new IllegalArgumentException("val is null");
        return val.toPlainString();
    }

    @Override
    public String printDouble(double v) {
        return _printDouble(v);
    }

    public static String _printDouble(double v) {
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    @Override
    public String printQName(QName val, NamespaceContext nsc) {
        return _printQName(val, nsc);
    }

    public static String _printQName(QName val, NamespaceContext nsc) {
        // Double-check
        String qname;
        String prefix = nsc.getPrefix(val.getNamespaceURI());
        String localPart = val.getLocalPart();

        if (prefix == null || prefix.isEmpty()) { // be defensive
            qname = localPart;
        } else {
            qname = prefix + ':' + localPart;
        }

        return qname;
    }

    @Override
    public String printBase64Binary(byte[] val) {
        return _printBase64Binary(val);
    }

    @Override
    public String printUnsignedShort(int val) {
        return String.valueOf(val);
    }

    @Override
    public String printAnySimpleType(String val) {
        return val;
    }

    /**
     * Just return the string passed as a parameter but
     * installs an instance of this class as the DatatypeConverter
     * implementation. Used from static fixed value initializers.
     */
    public static String installHook(String s) {
        DatatypeConverter.setDatatypeConverter(theInstance);
        return s;
    }
// base64 decoder
    private static final byte[] decodeMap = initDecodeMap();
    private static final byte PADDING = 127;

    private static byte[] initDecodeMap() {
        byte[] map = new byte[128];
        int i;
        for (i = 0; i < 128; i++) {
            map[i] = -1;
        }

        for (i = 'A'; i <= 'Z'; i++) {
            map[i] = (byte) (i - 'A');
        }
        for (i = 'a'; i <= 'z'; i++) {
            map[i] = (byte) (i - 'a' + 26);
        }
        for (i = '0'; i <= '9'; i++) {
            map[i] = (byte) (i - '0' + 52);
        }
        map['+'] = 62;
        map['/'] = 63;
        map['='] = PADDING;

        return map;
    }

    /**
     * computes the length of binary data speculatively.
     *
     * <p>
     * Our requirement is to create byte[] of the exact length to store the binary data.
     * If we do this in a straight-forward way, it takes two passes over the data.
     * Experiments show that this is a non-trivial overhead (35% or so is spent on
     * the first pass in calculating the length.)
     *
     * <p>
     * So the approach here is that we compute the length speculatively, without looking
     * at the whole contents. The obtained speculative value is never less than the
     * actual length of the binary data, but it may be bigger. So if the speculation
     * goes wrong, we'll pay the cost of reallocation and buffer copying.
     *
     * <p>
     * If the base64 text is tightly packed with no indentation nor illegal char
     * (like what most web services produce), then the speculation of this method
     * will be correct, so we get the performance benefit.
     */
    private static int guessLength(String text) {
        final int len = text.length();

        // compute the tail '=' chars
        int j = len - 1;
        for (; j >= 0; j--) {
            byte code = decodeMap[text.charAt(j)];
            if (code == PADDING) {
                continue;
            }
            if (code == -1) // most likely this base64 text is indented. go with the upper bound
            {
                return text.length() / 4 * 3;
            }
            break;
        }

        j++;    // text.charAt(j) is now at some base64 char, so +1 to make it the size
        int padSize = len - j;
        if (padSize > 2) // something is wrong with base64. be safe and go with the upper bound
        {
            return text.length() / 4 * 3;
        }

        // so far this base64 looks like it's unindented tightly packed base64.
        // take a chance and create an array with the expected size
        return text.length() / 4 * 3 - padSize;
    }

    /**
     * @param text
     *      base64Binary data is likely to be long, and decoding requires
     *      each character to be accessed twice (once for counting length, another
     *      for decoding.)
     * <p>
     *      A benchmark showed that taking {@link String} is faster, presumably
     *      because JIT can inline a lot of string access (with data of 1K chars, it was twice as fast)
     */
    public static byte[] _parseBase64Binary(String text) {
        if (null == text || text.length() % 4 != 0) {
            throw new IllegalArgumentException("base64 text invalid.");
        }
        final int buflen = guessLength(text);
        final byte[] out = new byte[buflen];
        int o = 0;

        final int len = text.length();
        int i;

        final byte[] quadruplet = new byte[4];
        int q = 0;

        // convert each quadruplet to three bytes.
        for (i = 0; i < len; i++) {
            char ch = text.charAt(i);
            byte v = decodeMap[ch];

            if (v != -1) {
                quadruplet[q++] = v;
            }

            if (q == 4) {

                // quadruplet is now filled.
                out[o++] = (byte) ((quadruplet[0] << 2) | (quadruplet[1] >> 4));
                if (quadruplet[2] != PADDING) {
                    out[o++] = (byte) ((quadruplet[1] << 4) | (quadruplet[2] >> 2));
                }
                if (quadruplet[3] != PADDING) {
                    out[o++] = (byte) ((quadruplet[2] << 6) | (quadruplet[3]));
                }
                q = 0;
            }
        }

        if (buflen == o) // speculation worked out to be OK
        {
            return out;
        }

        // we overestimated, so need to create a new buffer
        byte[] nb = new byte[o];
        System.arraycopy(out, 0, nb, 0, o);
        return nb;
    }
    private static final char[] encodeMap = initEncodeMap();

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        int i;
        for (i = 0; i < 26; i++) {
            map[i] = (char) ('A' + i);
        }
        for (i = 26; i < 52; i++) {
            map[i] = (char) ('a' + (i - 26));
        }
        for (i = 52; i < 62; i++) {
            map[i] = (char) ('0' + (i - 52));
        }
        map[62] = '+';
        map[63] = '/';

        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 0x3F];
    }

    public static byte encodeByte(int i) {
        return (byte) encodeMap[i & 0x3F];
    }

    public static String _printBase64Binary(byte[] input) {
        if (null == input) throw new IllegalArgumentException("input is null");
        return _printBase64Binary(input, 0, input.length);
    }

    public static String _printBase64Binary(byte[] input, int offset, int len) {
        char[] buf = new char[((len + 2) / 3) * 4];
        int ptr = _printBase64Binary(input, offset, len, buf, 0);
        assert ptr == buf.length;
        return new String(buf);
    }

    /**
     * Encodes a byte array into a char array by doing base64 encoding.
     * <p>
     * The caller must supply a big enough buffer.
     *
     * @return
     *      the value of {@code ptr+((len+2)/3)*4}, which is the new offset
     *      in the output buffer where the further bytes should be placed.
     */
    public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
        // encode elements until only 1 or 2 elements are left to encode
        int remaining = len;
        int i;
        for (i = offset;remaining >= 3; remaining -= 3, i += 3) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(
                    ((input[i] & 0x3) << 4)
                    | ((input[i + 1] >> 4) & 0xF));
            buf[ptr++] = encode(
                    ((input[i + 1] & 0xF) << 2)
                    | ((input[i + 2] >> 6) & 0x3));
            buf[ptr++] = encode(input[i + 2] & 0x3F);
        }
        // encode when exactly 1 element (left) to encode
        if (remaining == 1) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(((input[i]) & 0x3) << 4);
            buf[ptr++] = '=';
            buf[ptr++] = '=';
        }
        // encode when exactly 2 elements (left) to encode
        if (remaining == 2) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(((input[i] & 0x3) << 4)
                    | ((input[i + 1] >> 4) & 0xF));
            buf[ptr++] = encode((input[i + 1] & 0xF) << 2);
            buf[ptr++] = '=';
        }
        return ptr;
    }

    /**
     * Encodes a byte array into another byte array by first doing base64 encoding
     * then encoding the result in ASCII.
     * <p>
     * The caller must supply a big enough buffer.
     *
     * @return
     *      the value of {@code ptr+((len+2)/3)*4}, which is the new offset
     *      in the output buffer where the further bytes should be placed.
     */
    public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr) {
        byte[] buf = out;
        int remaining = len;
        int i;
        for (i=offset; remaining >= 3; remaining -= 3, i += 3 ) {
            buf[ptr++] = encodeByte(input[i]>>2);
            buf[ptr++] = encodeByte(
                        ((input[i]&0x3)<<4) |
                        ((input[i+1]>>4)&0xF));
            buf[ptr++] = encodeByte(
                        ((input[i+1]&0xF)<<2)|
                        ((input[i+2]>>6)&0x3));
            buf[ptr++] = encodeByte(input[i+2]&0x3F);
        }
        // encode when exactly 1 element (left) to encode
        if (remaining == 1) {
            buf[ptr++] = encodeByte(input[i]>>2);
            buf[ptr++] = encodeByte(((input[i])&0x3)<<4);
            buf[ptr++] = '=';
            buf[ptr++] = '=';
        }
        // encode when exactly 2 elements (left) to encode
        if (remaining == 2) {
            buf[ptr++] = encodeByte(input[i]>>2);
            buf[ptr++] = encodeByte(
                        ((input[i]&0x3)<<4) |
                        ((input[i+1]>>4)&0xF));
            buf[ptr++] = encodeByte((input[i+1]&0xF)<<2);
            buf[ptr++] = '=';
        }

        return ptr;
    }

    private static CharSequence removeOptionalPlus(CharSequence s) {
        int len = s.length();

        if (len <= 1 || s.charAt(0) != '+') {
            return s;
        }

        s = s.subSequence(1, len);
        char ch = s.charAt(0);
        if ('0' <= ch && ch <= '9') {
            return s;
        }
        if ('.' == ch) {
            return s;
        }

        throw new NumberFormatException();
    }

    private static boolean isDigitOrPeriodOrSign(char ch) {
        if ('0' <= ch && ch <= '9') {
            return true;
        }
        if (ch == '+' || ch == '-' || ch == '.') {
            return true;
        }
        return false;
    }
    private static final DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new Error(e);
        }
    }

    private static final class CalendarFormatter {

        public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
            int fidx = 0;
            int flen = format.length();
            StringBuilder buf = new StringBuilder();

            while (fidx < flen) {
                char fch = format.charAt(fidx++);

                if (fch != '%') {  // not a meta character
                    buf.append(fch);
                    continue;
                }

                // seen meta character. we don't do error check against the format
                switch (format.charAt(fidx++)) {
                    case 'Y': // year
                        formatYear(cal, buf);
                        break;

                    case 'M': // month
                        formatMonth(cal, buf);
                        break;

                    case 'D': // days
                        formatDays(cal, buf);
                        break;

                    case 'h': // hours
                        formatHours(cal, buf);
                        break;

                    case 'm': // minutes
                        formatMinutes(cal, buf);
                        break;

                    case 's': // parse seconds.
                        formatSeconds(cal, buf);
                        break;

                    case 'z': // time zone
                        formatTimeZone(cal, buf);
                        break;

                    default:
                        // illegal meta character. impossible.
                        throw new InternalError();
                }
            }

            return buf.toString();
        }

        private static void formatYear(Calendar cal, StringBuilder buf) {
            int year = cal.get(Calendar.YEAR);

            String s;
            if (year <= 0) // negative value
            {
                s = Integer.toString(1 - year);
            } else // positive value
            {
                s = Integer.toString(year);
            }

            while (s.length() < 4) {
                s = '0' + s;
            }
            if (year <= 0) {
                s = '-' + s;
            }

            buf.append(s);
        }

        private static void formatMonth(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.MONTH) + 1, buf);
        }

        private static void formatDays(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.DAY_OF_MONTH), buf);
        }

        private static void formatHours(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.HOUR_OF_DAY), buf);
        }

        private static void formatMinutes(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.MINUTE), buf);
        }

        private static void formatSeconds(Calendar cal, StringBuilder buf) {
            formatTwoDigits(cal.get(Calendar.SECOND), buf);
            if (cal.isSet(Calendar.MILLISECOND)) { // milliseconds
                int n = cal.get(Calendar.MILLISECOND);
                if (n != 0) {
                    String ms = Integer.toString(n);
                    while (ms.length() < 3) {
                        ms = '0' + ms; // left 0 paddings.
                    }
                    buf.append('.');
                    buf.append(ms);
                }
            }
        }

        /** formats time zone specifier. */
        private static void formatTimeZone(Calendar cal, StringBuilder buf) {
            TimeZone tz = cal.getTimeZone();

            if (tz == null) {
                return;
            }

            // otherwise print out normally.
            int offset = tz.getOffset(cal.getTime().getTime());

            if (offset == 0) {
                buf.append('Z');
                return;
            }

            if (offset >= 0) {
                buf.append('+');
            } else {
                buf.append('-');
                offset *= -1;
            }

            offset /= 60 * 1000; // offset is in milliseconds

            formatTwoDigits(offset / 60, buf);
            buf.append(':');
            formatTwoDigits(offset % 60, buf);
        }

        /** formats Integer into two-character-wide string. */
        private static void formatTwoDigits(int n, StringBuilder buf) {
            // n is always non-negative.
            if (n < 10) {
                buf.append('0');
            }
            buf.append(n);
        }
    }
}
