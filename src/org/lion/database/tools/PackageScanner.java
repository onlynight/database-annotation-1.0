package org.lion.database.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lion.database.annotation.Table;
import org.lion.database.annotation.Tables;

/**
 * package scanner tools. you can use this tools to scan the table classes
 * 
 * @author onlynight
 * 
 */
public class PackageScanner {

	public static Set<Class<?>> scanForTable(String packageToScan) {
		Set<Class<?>> set = scan(packageToScan);
		Set<Class<?>> res = new LinkedHashSet<Class<?>>();
		for (Class<?> clazz : set) {
			if (clazz.isAnnotationPresent(Table.class)) {
				res.add(clazz);
			}
			else if ( clazz.isAnnotationPresent(Tables.class) ) {
				res.add(clazz);
			}
		}
		return res;
	}

	/**
	 * �Ӱ�packageToScanage�л�ȡ���е�Class
	 * 
	 * @param packageToScan
	 * @return
	 */
	public static Set<Class<?>> scan(String packageToScan) {

		// ��һ��class��ļ���
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// �Ƿ�ѭ������
		boolean recursive = true;
		// ��ȡ�������� �������滻
		String packageToScanageName = packageToScan;
		String packageToScanageDirName = packageToScanageName.replace('.', '/');
		// ����һ��ö�ٵļ��� ������ѭ�����������Ŀ¼�µ�things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(packageToScanageDirName);
			// ѭ��������ȥ
			while (dirs.hasMoreElements()) {
				// ��ȡ��һ��Ԫ��
				URL url = dirs.nextElement();
				// �õ�Э�������
				String protocol = url.getProtocol();
				// ��������ļ�����ʽ�����ڷ�������
				if ("file".equals(protocol)) {
					// System.err.println("file���͵�ɨ��");
					// ��ȡ��������·��
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// ���ļ��ķ�ʽɨ���������µ��ļ� ����ӵ�������
					findAndAddClassesInpackageToScanageByFile(
							packageToScanageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// �����jar���ļ�
					// ����һ��JarFile
					System.err.println("jar���͵�ɨ��");
					JarFile jar;
					try {
						// ��ȡjar
						jar = ((JarURLConnection) url.openConnection())
								.getJarFile();
						// �Ӵ�jar�� �õ�һ��ö����
						Enumeration<JarEntry> entries = jar.entries();
						// ͬ���Ľ���ѭ������
						while (entries.hasMoreElements()) {
							// ��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// �������/��ͷ��
							if (name.charAt(0) == '/') {
								// ��ȡ������ַ���
								name = name.substring(1);
							}
							// ���ǰ�벿�ֺͶ���İ�����ͬ
							if (name.startsWith(packageToScanageDirName)) {
								int idx = name.lastIndexOf('/');
								// �����"/"��β ��һ����
								if (idx != -1) {
									// ��ȡ���� ��"/"�滻��"."
									packageToScanageName = name.substring(0,
											idx).replace('/', '.');
								}
								// ������Ե�����ȥ ������һ����
								if ((idx != -1) || recursive) {
									// �����һ��.class�ļ� ���Ҳ���Ŀ¼
									if (name.endsWith(".class")
											&& !entry.isDirectory()) {
										// ȥ�������".class" ��ȡ����������
										String className = name
												.substring(packageToScanageName
														.length() + 1, name
														.length() - 6);
										try {
											// ��ӵ�classes
											classes.add(Class
													.forName(packageToScanageName
															+ '.' + className));
										} catch (ClassNotFoundException e) {
											// log
											// .error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						// log.error("��ɨ���û�������ͼʱ��jar����ȡ�ļ�����");
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * ���ļ�����ʽ����ȡ���µ�����Class
	 * 
	 * @param packageToScanageName
	 * @param packageToScanagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInpackageToScanageByFile(
			String packageToScanageName, String packageToScanagePath,
			final boolean recursive, Set<Class<?>> classes) {
		// ��ȡ�˰���Ŀ¼ ����һ��File
		File dir = new File(packageToScanagePath);
		// ��������ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("�û�������� " + packageToScanageName + " ��û���κ��ļ�");
			return;
		}
		// ������� �ͻ�ȡ���µ������ļ� ����Ŀ¼
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// �Զ�����˹��� �������ѭ��(������Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
			public boolean accept(File file) {
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// ѭ�������ļ�
		for (File file : dirfiles) {
			// �����Ŀ¼ �����ɨ��
			if (file.isDirectory()) {
				findAndAddClassesInpackageToScanageByFile(packageToScanageName
						+ "." + file.getName(), file.getAbsolutePath(),
						recursive, classes);
			} else {
				// �����java���ļ� ȥ�������.class ֻ��������
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					// ��ӵ�������ȥ
					// classes.add(Class.forName(packageToScanageName + '.' +
					// className));
					// �����ظ�ͬѧ�����ѣ�������forName��һЩ���ã��ᴥ��static������û��ʹ��classLoader��load�ɾ�
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageToScanageName + '.' + className));
				} catch (ClassNotFoundException e) {
					// log.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
					e.printStackTrace();
				}
			}
		}
	}
}
