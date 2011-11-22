package net.guto.loader;

import java.io.File;
import java.io.IOException;

public class SourceLoader extends AbstractLoader {
	private String classPath;

	private String binPath;

	private String sourcePath;

	public SourceLoader(ClassLoader parent) {
		super(parent);
	}

	public SourceLoader(ClassLoader parent, String classPath,
			String sourcePath, String binPath) {
		super(parent);
		this.classPath = classPath;
		this.sourcePath = sourcePath;
		this.binPath = binPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setBinaryPath(String binPath) {
		this.binPath = binPath;
	}

	private boolean compile(String javaFile) throws IOException {
		int ret = -1;
		Process p = null;
		// System.out.println("CCL: Compiling " + javaFile + "...");
		String command = "javac -classpath " + classPath + " -d " + binPath
				+ " " + javaFile;
		p = Runtime.getRuntime().exec(command);
		System.out.println(command);
		try {
			p.waitFor();
			ret = p.exitValue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret == 0;
	}

	// private boolean javacCompiler (String javaFile) throws IOException {
	// return false;
	// }
	//
	// private boolean jikesCompiler (String javaFile) throws IOException {
	// return true;
	// }

	public Class<?> specificLoad(String name, boolean resolve)
			throws ClassNotFoundException {
		Class clas = null;
		clas = findLoadedClass(name);
		String fileStub = name.replace('.', '/');
		String javaFilename = sourcePath + '/' + fileStub + ".java";
		String classFilename = binPath + '/' + fileStub + ".class";
		File javaFile = new File(javaFilename);
		File classFile = new File(classFilename);
		if (javaFile.exists()
				&& (!classFile.exists() || javaFile.lastModified() > classFile
						.lastModified())) {
			try {
				if (!compile(javaFilename) || !classFile.exists()) {
					throw new ClassNotFoundException("Compile failed: "
							+ javaFilename);
				}
			} catch (IOException ie) {
				throw new ClassNotFoundException(ie.toString());
			}
		}
		try {
			byte raw[] = getBytes(classFilename);
			clas = defineClass(name, raw, 0, raw.length);
		} catch (IOException e) {
			System.out.println("warning! Invalid defineClass Attempt");
		}
		if (clas == null) {
			clas = findSystemClass(name);
		}
		if (resolve && clas != null)
			resolveClass(clas);
		if (clas == null)
			throw new ClassNotFoundException(name);
		return clas;
	}

	@SuppressWarnings("unchecked")
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		if (isSystemCode(name))
			return findSystemClass(name);
		System.out.println("oi");
		Class clas = null;
		clas = findLoadedClass(name);
		String fileStub = name.replace('.', '/');
		String javaFilename = sourcePath + '/' + fileStub + ".java";
		String classFilename = binPath + '/' + fileStub + ".class";
		File javaFile = new File(javaFilename);
		File classFile = new File(classFilename);
		if (!javaFile.exists())
			System.out.println("Source nao encontrada:" + javaFilename);
		if (!classFile.exists())
			System.out.println("Class nao encontrada:" + classFilename);
		if (javaFile.exists()
				&& (!classFile.exists() || javaFile.lastModified() > classFile
						.lastModified())) {
			try {
				if (!compile(javaFilename) || !classFile.exists()) {
					throw new ClassNotFoundException("Compile failed: "
							+ javaFilename);
				}
			} catch (IOException ie) {
				throw new ClassNotFoundException(ie.toString());
			}
		}
		try {
			byte raw[] = getBytes(classFilename);
			clas = defineClass(name, raw, 0, raw.length);
		} catch (IOException e) {
			System.out.println("warning! Invalid defineClass Attempt");
		}
		if (clas == null) {
			clas = findSystemClass(name);
		}
		if (resolve && clas != null)
			resolveClass(clas);
		if (clas == null)
			throw new ClassNotFoundException(name);
		return clas;
	}
}
