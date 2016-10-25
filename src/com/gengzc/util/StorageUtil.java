package com.gengzc.util;

public class StorageUtil {

    /**
     * byte大小，既1.
     */
	public static final long BYTE = 1;

	/**
	 * 1kb所对应的byte数，既1024.
	 */
	public static final long KB = 1024 * BYTE;

	/**
	 * 1MB对应的byte数，1024*1024*1.
	 */
	public static final long MB = 1024 * KB;

	/**
	 * 1GB对应的byte数，1024*1024*1024*1.
	 */
	public static final long GB = 1024 * MB;

	/**
	 * 1TB对应的byte数，1024*1024*1024*1024*1.
	 */
	public static final long TB = 1024 * GB;

	/**
	 * 1PB对应的byte数，1024*1024*1024*1024*1024*1.
	 */
	public static final long PB = 1024 * TB;

	/**
	 * 1EB对应的byte数，1024*1024*1024*1024*1024*1024*1.
	 */
	public static final long EB = 1024 * PB;

	/**
	 * 字节转换成KB.
	 * @param bytes
	 * 			byte的大小
	 * @return KB
	 */
	public static double fromByteToKB(long bytes) {
		return (double) bytes / KB;
	}

	/**
	 * 字节转换成.
	 * @param bytes
	 * 			byte的大小
	 * @return MB
	 */
	public static double fromByteToMB(long bytes) {
		return (double) bytes / MB;
	}

	/**
	 * 字节转换成GB.
	 * @param bytes
	 * 			byte的大小
	 * @return GB
	 */
	public static double fromByteToGB(long bytes) {
		return (double) bytes / GB;
	}

	/**
	 * 字节转换成TB.
	 * @param bytes
	 * 			byte的大小
	 * @return TB
	 */
	public static double fromByteToTB(long bytes) {
		return (double) bytes / TB;
	}

	/**
	 * KB转换成字节.
	 * @param kb
	 * 			kb的大小
	 * @return byte
	 */
	public static long fromKBToByte(long kb) {
		return kb * KB;
	}

	/**
	 * MB转换成字节.
	 * @param mb
	 * 			MB的大小
	 * @return MB 
	 */
	public static long fromMBToByte(long mb) {
		return mb * MB;
	}

	/**
	 * GB转换成字节.
	 * @param gb
	 * 			GB的大小
	 * @return byte
	 */
	public static double fromGBToByte(long gb) {
		return (double) gb * GB;
	}

	/**
	 * GB转换成KB.
	 * @param gb
	 * 			GB的大小
	 * @return KB
	 */
	public static double fromGBToKB(long gb) {
		return (double) gb * MB;
	}

	/**
	 * GB转换成MB.
	 * @param gb
	 * 			GB的大小
	 * @return MB
	 */
	public static double fromGBToMB(long gb) {
		return (double) gb * KB;
	}

	/**
	 * MB转换成GB.
	 * @param mb
	 * 			MB的大小
	 * @return GB
	 */
	public static double fromMBToGB(long mb) {
		return (double) mb / KB;
	}

	/**
	 * MB转换成KB.
	 * @param mb
	 * 			MB的大小
	 * @return KB
	 */
	public static double fromMBToKB(long mb) {
		return (double) mb * KB;
	}

	/**
	 * KB转换成MB.
	 * @param kb
	 * 			KB的大小
	 * @return MB
	 */
	public static double fromKBToMB(long kb) {
		return (double) kb / KB;
	}

	/**
	 * KB转换成GB.
	 * @param kb
	 * 			KB的大小
	 * @return GB
	 */
	public static double fromKBToGB(long kb) {
		return (double) kb / MB;
	}
}
