/*
 * 
 */
package net.ameba.cassandra.web.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


/**
 * {@link ByteArray} provides utility functions to convert byte array.
 */
public class ByteArray {
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	/**
	 * Get the next binary
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] next(byte[] bytes) {
		byte[] next = new byte[bytes.length];
		System.arraycopy(bytes, 0, next, 0, next.length);
		for (int i = next.length-1; i >= 0; i--) {
			int b = (next[i] & 0xff);
			if (b == 0xff) {
				next[i] = 0;
			} else {
				next[i] = (byte)((b+1) & 0xff);
				break;
			}
		}
		return next;
	}
	
	/**
	 * Get the previous binary
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] previous(byte[] bytes) {
		byte[] prev = new byte[bytes.length];
		System.arraycopy(bytes, 0, prev, 0, prev.length);
		for (int i = prev.length-1; i >= 0; i--) {
			int b = (prev[i] & 0xff);
			if (b == 0) {
				prev[i] = (byte)(255 & 0xff);
			} else {
				prev[i] = (byte)((b-1) & 0xff);
				break;
			}
		}
		return prev;
	}
	
	/**
	 * Get binary as short value.
	 * 
	 * @param bytes
	 * @return
	 */
	public static short toShort(byte[] bytes) {
		return (short)(
			((bytes[0] & 0xff) << 8) | 
			((bytes[1] & 0xff) << 0)
		);
	}
	
	/**
	 * Get binary as int value.
	 * 
	 * @param bytes
	 * @return
	 */
	public static int toInt(byte[] bytes) {
		return 
			((bytes[0] & 0xff) << 24) |
			((bytes[1] & 0xff) << 16) |
			((bytes[2] & 0xff) << 8) |
			(bytes[3] & 0xff);
	}
	
	/**
	 * Get binary as long value.
	 * @param bytes
	 * @return
	 */
	public static long toLong(byte[] bytes) {
		return (long)(
			((long)(bytes[0] & 0xff) << 56) |
			((long)(bytes[1] & 0xff) << 48) |
			((long)(bytes[2] & 0xff) << 40) |
			((long)(bytes[3] & 0xff) << 32) |
			((long)(bytes[4] & 0xff) << 24) |
			((long)(bytes[5] & 0xff) << 16) |
			((long)(bytes[6] & 0xff) << 8) |
			((long)(bytes[7] & 0xff) << 0)
		);
	}
	
	public static float toFloat(byte[] bytes) {
		return Float.intBitsToFloat(toInt(bytes));
	}
	
	public static double toDouble(byte[] bytes) {
		return Double.longBitsToDouble(toLong(bytes));
	}
	
	public static char toChar(byte[] bytes) {
		return (char)(
				((bytes[0] & 0xff) << 8) | 
				((bytes[1] & 0xff) << 0)
			);
	}
	
	/**
	 * Get binary as UTF8 stirng.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toUTF(byte[] bytes) {
		return new String(bytes, UTF8);
	}
	
	/**
	 * Get binary as Inet adress.
	 * @param bytes
	 * @return
	 */
	public static InetAddress toAddress(byte[] bytes) {
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException ex) {
			return null;
		}
	}

	/**
	 * Convert integer to byte array.
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] toBytes(int i) {
		return new byte[] {
				(byte)(i >> 24),
				(byte)(i >> 16),
				(byte)(i >>  8),
				(byte)(i >> 0)
		};
	}
	
	/**
	 * Convert long to byte array.
	 * 
	 * @param l
	 * @return
	 */
	public static byte[] toBytes(long l) {
		return new byte[] {
				(byte)(l >> 56),
				(byte)(l >> 48),
				(byte)(l >> 40),
				(byte)(l >> 32),
				(byte)(l >> 24),
				(byte)(l >> 16),
				(byte)(l >>  8),
				(byte)(l >> 0)
		};
	}
	
	/**
	 * Convert short to byte array.
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] toBytes(short s) {
		return new byte[] {
				(byte)(s >> 8),
				(byte)(s >> 0)
		};
	}
	
	/**
	 * Convert float to byte array.
	 * @param f
	 * @return
	 */
	public static byte[] toBytes(float f) {
		return toBytes(Float.floatToIntBits(f));
	}
	
	/**
	 * Convert double to byte array.
	 * @param d
	 * @return
	 */
	public static byte[] toBytes(double d) {
		return toBytes(Double.doubleToLongBits(d));
	}
	
	/**
	 * Convert char to byte array.
	 * @param c
	 * @return
	 */
	public static byte[] toBytes(char c) {
		return new byte[] {
				(byte)(c >> 8),
				(byte)(c >> 0)
		};
	}
	
	/**
	 * Convert utf8 string to byte array.
	 * @param s
	 * @return
	 */
	public static byte[] toBytes(String s) {
		return s.getBytes(UTF8);
	}
}
