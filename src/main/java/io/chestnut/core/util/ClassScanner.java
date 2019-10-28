package io.chestnut.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.LoggerFactory;

import io.chestnut.core.network.httpd.HttpHandle;
import io.chestnut.core.network.httpd.WebServlet;

import org.slf4j.Logger;

public class ClassScanner {
	public static final Logger logger = LoggerFactory.getLogger(ClassScanner.class.getName());

	public static void main(String args[]) throws Exception {
		Set<Class<?>> classSet = ClassScanner.getClasses("io.chestnut.core.service.serviceMrg.httpHandle");
		for (Class<?> class1 : classSet) {
			System.out.println(class1.getSimpleName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> httpServletInit(String path) {
		Map<String, T> httpServletMap = new HashMap<String, T>();
		Set<Class<?>> classSet = ClassScanner.getClasses(path);
		for (Class<?> clazz : classSet) {
			if (clazz.getSuperclass() == HttpHandle.class) {
				WebServlet servlet = clazz.getDeclaredAnnotation(WebServlet.class);
				Object httpHandler;
				try {
					httpHandler = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				HttpHandle handle = (HttpHandle) httpHandler;
				String name = "/" + clazz.getSimpleName();
				if (servlet != null) {
					handle.isPublicAuthority = servlet.isPublicAuthority();
					if(servlet.name() != null && !servlet.name().equals("")) {
						name = "/" + servlet.name();
					}
				}
				httpServletMap.put(name, (T) httpHandler);
			}
		}
		return  httpServletMap;
	}
	

	/**
	 * 
	 * @param pack 路径名字
	 * @return 返回值
	 */
	public static Set<Class<?>> getClasses(String pack) {
		logger.debug("start getClasses " + pack);
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean recursive = true;
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					logger.debug("从jar中开始加载文件");
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									packageName = name.substring(0, idx).replace('/', '.');
								}
								if ((idx != -1) || recursive) {
									if (name.endsWith(".class") && !entry.isDirectory()) {
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<Class<?>> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					
					classes.add(
							Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
