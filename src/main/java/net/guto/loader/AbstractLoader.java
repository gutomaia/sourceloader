package net.guto.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
@SuppressWarnings("unchecked")
public abstract class AbstractLoader extends ClassLoader {
	
	private Hashtable classArrays;

	private URL urlBase;

	public AbstractLoader(ClassLoader parent) {
		super(parent);
		classArrays = new Hashtable();
	}

	protected final void hasUpdate(String classname) {

	}

	protected final boolean isActive() {
		return true;
	}

	protected final boolean isRemote() {
		return true;
	}

	public void setUrlBase(String base) {
		try {
			if (!(base.endsWith("/")))
				base = base + "/";
			urlBase = new URL(base);
		} catch (Exception e) {
			throw new IllegalArgumentException(base);
		}
	}

	@SuppressWarnings("unchecked")
	protected final Class remoteFindClass(String name)
			throws ClassNotFoundException {
		String urlName = name.replace('.', '/');
		byte buf[];
		Class clas;

		// SecurityManager sm = System.getSecurityManager();
		// if (sm != null) {
		// int i = name.lastIndexOf('.');
		// if (i >= 0)
		// sm.checkPackageDefinition(name.substring(0, i));
		// }

		buf = (byte[]) classArrays.get(urlName);
		if (buf != null) {
			clas = defineClass(name, buf, 0, buf.length, null);
			return clas;
		}
		try {
			URL url = new URL(urlBase, urlName + ".class");
			System.out.println("Loading " + url);
			InputStream is = url.openConnection().getInputStream();
			buf = getClassBytes(is);
			clas = defineClass(name, buf, 0, buf.length);
			return clas;
		} catch (Exception e) {
			System.out.println("Can't load " + name + ": " + e);
			return null;
		}
	}

	private byte[] getClassBytes(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		boolean eof = false;
		while (!eof) {
			try {
				int i = bis.read();
				if (i == -1)
					eof = true;
				else
					baos.write(i);
			} catch (IOException e) {
				return null;
			}
		}
		return baos.toByteArray();
	}

	protected final boolean isSystemCode(String name) {
		return name.startsWith("java.") || name.startsWith("sun.")
				|| name.startsWith("javax.")
				|| name.startsWith("net.guto.loader");

	}

	protected final byte[] getBytes(String filename) throws IOException {
		File file = new File(filename);
		long len = file.length();
		byte raw[] = new byte[(int) len];
		FileInputStream fin = new FileInputStream(file);
		long r = fin.read(raw);
		if (r != len) {
			throw new IOException("Can't read all, " + r + " != " + len);
		}
		fin.close();
		return raw;
	}

}
