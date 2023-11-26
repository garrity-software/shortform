package gs.uuid;

import java.util.UUID;
import java.util.Arrays;

/**
 * UUID serialization and deserialization. This is a direct copy of Jackson
 * Databind (also under the Apache 2.0 license at time of writing) with
 * extremely minor modifications to remove dashes from the output and to
 * likewise support parsing with/without dashes.
 */
public final class UUIDFormat {
    private UUIDFormat() {}

    private final static char[] HEX_CHARS = "0123456789abcdef".toCharArray();


    private final static int[] HEX_DIGITS = new int[127];

    static {
        Arrays.fill(HEX_DIGITS, -1);
        for (int i = 0; i < 10; ++i) { HEX_DIGITS['0' + i] = i; }
        for (int i = 0; i < 6; ++i) {
            HEX_DIGITS['a' + i] = 10 + i;
            HEX_DIGITS['A' + i] = 10 + i;
        }
    }

    /**
     * <p>Render the given UUID as a 32-character string using lowercase
     * hexadecimal without dashes.</p>
     *
     * @param uuid The UUID to render.
     * @return Hexadecimal representation of the UUID.
     */
    public static String toHex(final UUID uuid) {
        final char[] ch = new char[32];

        // Example:
        // 9bbe7b63-7928-49c8-a14f-67098b6e4642
        final long msb = uuid.getMostSignificantBits();

        // Handle the first 8 characters (9bbe7b63)
        _appendInt((int) (msb >> 32), ch, 0);

        int i = (int) msb;
        // Handle the next 4 characters (7928) (Section 2)
        _appendShort(i >>> 16, ch, 8);

        // Handle the next 4 characters (49c8) (Section 3)
        _appendShort(i, ch, 12);

        final long lsb = uuid.getLeastSignificantBits();

        // Handle the next 4 characters (a14ff) (Section 4)
        _appendShort((int) (lsb >>> 48), ch, 16);

        // Handle the next 4 characters (6709) (Section 5)
        _appendShort((int) (lsb >>> 32), ch, 20);

        // Handle the final 8 characters (8b6e4642) (Section 5)
        _appendInt((int) lsb, ch, 24);

        return new String(ch, 0, 32);
    }

    /**
     * <p>Render the given UUID as a 16-byte array.</p>
     *
     * @param uuid The UUID to render.
     * @return 16-byte array.
     */
    public static byte[] toBytes(final UUID uuid) {
        return _asBytes(uuid);
    }

    /**
     * <p>Parse the given hexadecimal string as a UUID. This method supports
     * both 32-character (no dash) and 36-character (dashes) representations,
     * and will automatically choose based on input length.</p>
     *
     * @param id The string representation to parse.
     * @return The parsed UUID.
     */
    public static UUID fromHex(final String id) {
        final int len = id.length();

        if (len == 32) {
            // Deserialize without dashes.

            // Get the first 8 characters from index 0
            long l1 = intFromChars(id, 0);
            l1 <<= 32;

            // Get the second 4 characters from index 8
            long l2 = ((long) shortFromChars(id, 8)) << 16;

            // Get the third 4 characters from index 12
            l2 |= shortFromChars(id, 12);
            long hi = l1 + l2;

            // Get the next two sets of 4 characters from indexes 16 and 20
            // respectively.
            int i1 = (shortFromChars(id, 16) << 16) | shortFromChars(id, 20);
            l1 = i1;
            l1 <<= 32;

            // Get the final 8 characters from index 24
            l2 = intFromChars(id, 24);
            l2 = (l2 << 32) >>> 32;
            long lo = l1 | l2;

            return new UUID(hi, lo);
        } else if (len == 36) {
            // Deserialize with dashes.
            if ((id.charAt(8) != '-') || (id.charAt(13) != '-')
                    || (id.charAt(18) != '-') || (id.charAt(23) != '-')) {
                throw new IllegalArgumentException("Malformed UUID: 36-character representation does not contain correct dashes.");
                    }
            long l1 = intFromChars(id, 0);
            l1 <<= 32;
            long l2 = ((long) shortFromChars(id, 9)) << 16;
            l2 |= shortFromChars(id, 14);
            long hi = l1 + l2;

            int i1 = (shortFromChars(id, 19) << 16) | shortFromChars(id, 24);
            l1 = i1;
            l1 <<= 32;
            l2 = intFromChars(id, 28);
            l2 = (l2 << 32) >>> 32; // sign removal, Java-style. Ugh. [Note: Retained this comment from Jackson :) ]
            long lo = l1 | l2;

            return new UUID(hi, lo);
        } else {
            throw new IllegalArgumentException("UUID hexadecimal strings must be either 32 characters or 36 characters long.");
        }
    }

    public static UUID fromBytes(final byte[] bytes) {
        return _fromBytes(bytes);
    }

    private static void _appendShort(int bits, char[] ch, int offset) {
        ch[offset] = HEX_CHARS[(bits >> 12) & 0xF];
        ch[++offset] = HEX_CHARS[(bits >> 8) & 0xF];
        ch[++offset] = HEX_CHARS[(bits >> 4) & 0xF];
        ch[++offset] = HEX_CHARS[bits  & 0xF];
    }

    private static void _appendInt(int bits, char[] ch, int offset) {
        _appendShort(bits >> 16, ch, offset);
        _appendShort(bits, ch, offset+4);
    }

    private final static void _appendInt(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 24);
        buffer[++offset] = (byte) (value >> 16);
        buffer[++offset] = (byte) (value >> 8);
        buffer[++offset] = (byte) value;
    }

    private final static byte[] _asBytes(UUID uuid) {
        byte[] buffer = new byte[16];
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        _appendInt((int) (hi >> 32), buffer, 0);
        _appendInt((int) hi, buffer, 4);
        _appendInt((int) (lo >> 32), buffer, 8);
        _appendInt((int) lo, buffer, 12);
        return buffer;
    }

    private static int intFromChars(String str, int index) {
        return (byteFromChars(str, index) << 24)
            + (byteFromChars(str, index+2) << 16)
            + (byteFromChars(str, index+4) << 8)
            + byteFromChars(str, index+6);
    }

    private static int shortFromChars(String str, int index) {
        return (byteFromChars(str, index) << 8) + byteFromChars(str, index+2);
    }

    private static int byteFromChars(String str, int index) {
        final char c1 = str.charAt(index);
        final char c2 = str.charAt(index+1);

        if (c1 <= 127 && c2 <= 127) {
            int hex = (HEX_DIGITS[c1] << 4) | HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }

        throw new IllegalArgumentException("Invalid hexadecimal character detected in byte at index " + index);
    }

    private static UUID _fromBytes(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes");
        }
        return new UUID(_long(bytes, 0), _long(bytes, 8));
    }

    private static long _long(byte[] b, int offset) {
        long l1 = ((long) _int(b, offset)) << 32;
        long l2 = _int(b, offset+4);
        // faster to just do it than check if it has sign
        l2 = (l2 << 32) >>> 32; // to get rid of sign
        return l1 | l2;
    }

    private static int _int(byte[] b, int offset) {
        return (b[offset] << 24) | ((b[offset+1] & 0xFF) << 16) | ((b[offset+2] & 0xFF) << 8) | (b[offset+3] & 0xFF);
    }
}
