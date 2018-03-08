package com.acn.gzmcs.smscombiner.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.ProxyInputStream;

public class BomInputStream extends ProxyInputStream {
	public static final String UTF8_BOM = "\uFEFF";
	private final boolean include;
	private final List<ByteOrderMark> boms;
	private ByteOrderMark byteOrderMark;
	private int[] firstBytes;
	private int fbLength;
	private int fbIndex;
	private int markFbIndex;
	private boolean markedAtStart;

	public BomInputStream(InputStream delegate) {
		this(delegate, false, new ByteOrderMark[] { ByteOrderMark.UTF_8 });
	}

	public BomInputStream(InputStream delegate, boolean include) {
		this(delegate, include, new ByteOrderMark[] { ByteOrderMark.UTF_8 });
	}

	public BomInputStream(InputStream delegate, ByteOrderMark[] boms) {
		this(delegate, false, boms);
	}

	public BomInputStream(InputStream delegate, boolean include,
			ByteOrderMark[] boms) {
		super(delegate);
		if ((boms == null) || (boms.length == 0)) {
			throw new IllegalArgumentException("No BOMs specified");
		}
		this.include = include;
		this.boms = Arrays.asList(boms);
	}

	public boolean hasBOM() throws IOException {
		return getBOM() != null;
	}

	public boolean hasBOM(ByteOrderMark bom) throws IOException {
		if (!this.boms.contains(bom)) {
			throw new IllegalArgumentException(
					"Stream not configure to detect " + bom);
		}
		return (this.byteOrderMark != null) && (getBOM().equals(bom));
	}

	public ByteOrderMark getBOM() throws IOException {
		if (this.firstBytes == null) {
			int max = 0;
			for (ByteOrderMark bom : this.boms) {
				max = Math.max(max, bom.length());
			}
			this.firstBytes = new int[max];
			for (int i = 0; i < this.firstBytes.length; i++) {
				this.firstBytes[i] = this.in.read();
				this.fbLength += 1;
				if (this.firstBytes[i] < 0) {
					break;
				}
				this.byteOrderMark = find();
				if (this.byteOrderMark != null) {
					if (this.include)
						break;
					this.fbLength = 0;

					break;
				}
			}
		}
		return this.byteOrderMark;
	}

	public String getBOMCharsetName() throws IOException {
		getBOM();
		return this.byteOrderMark == null ? null : this.byteOrderMark
				.getCharsetName();
	}

	private int readFirstBytes() throws IOException {
		getBOM();
		return this.fbIndex < this.fbLength ? this.firstBytes[(this.fbIndex++)]
				: -1;
	}

	private ByteOrderMark find() {
		for (ByteOrderMark bom : this.boms) {
			if (matches(bom)) {
				return bom;
			}
		}
		return null;
	}

	private boolean matches(ByteOrderMark bom) {
		if (bom.length() != this.fbLength) {
			return false;
		}
		for (int i = 0; i < bom.length(); i++) {
			if (bom.get(i) != this.firstBytes[i]) {
				return false;
			}
		}
		return true;
	}

	public int read() throws IOException {
		int b = readFirstBytes();
		return b >= 0 ? b : this.in.read();
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		int firstCount = 0;
		int b = 0;
		while ((len > 0) && (b >= 0)) {
			b = readFirstBytes();
			if (b >= 0) {
				buf[(off++)] = (byte) (b & 0xFF);
				len--;
				firstCount++;
			}
		}
		int secondCount = this.in.read(buf, off, len);
		return secondCount < 0 ? firstCount : firstCount + secondCount;
	}

	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	public synchronized void mark(int readlimit) {
		this.markFbIndex = this.fbIndex;
		this.markedAtStart = (this.firstBytes == null);
		this.in.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		this.fbIndex = this.markFbIndex;
		if (this.markedAtStart) {
			this.firstBytes = null;
		}

		this.in.reset();
	}

	public long skip(long n) throws IOException {
		while ((n > 0L) && (readFirstBytes() >= 0)) {
			n -= 1L;
		}
		return this.in.skip(n);
	}
}
