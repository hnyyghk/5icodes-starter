/**
 * Copyright 2016 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * </p>
 **/

package com.vip.saturn.embed;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EmbeddedSaturn {

	private Object main;
	private Object saturnApplication;

	public void start() throws Exception {


		String namespace = System.getProperty("saturn.app.namespace", System.getenv("SATURN_APP_NAMESPACE"));
		String executorName = System.getProperty("saturn.app.executorName", System.getenv("SATURN_APP_EXECUTOR_NAME"));
		if (namespace == null || namespace.isEmpty()) {
			throw new Exception("saturn.app.namespace is not set");
		}

		ClassLoader executorClassLoader = EmbeddedSaturn.class.getClassLoader();

		final List<String> argList = new ArrayList<>();

		argList.add("-namespace");
		argList.add(namespace);

		if (executorName != null && !executorName.isEmpty()) {
			argList.add("-executorName");
			argList.add(executorName);
		}

		Class<?> mainClass = executorClassLoader.loadClass("com.vip.saturn.job.executor.Main");
		main = mainClass.newInstance();
		Method launchMethod = mainClass.getMethod("launchInner", String[].class, ClassLoader.class, ClassLoader.class);

		String[] args = new String[argList.size()];
		int i = 0;
		for (String arg : argList) {
			args[i] = arg;
			i++;
		}

		launchMethod.invoke(main, args, executorClassLoader, executorClassLoader);
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a deletion fails, the method stops attempting
	 * to delete and returns "false".
	 */
	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	private void unzip(File zip, File directory) throws IOException {
		ZipFile zipFile = new ZipFile(zip);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.isDirectory()) {
					File temp = new File(directory + File.separator + zipEntry.getName());
					temp.mkdirs();
					continue;
				}
				BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				try {
					File f = new File(directory + File.separator + zipEntry.getName());
					File f_p = f.getParentFile();
					if (f_p != null && !f_p.exists()) {
						f_p.mkdirs();
					}
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
					try {
						int len = -1;
						byte[] bs = new byte[2048];
						while ((len = bis.read(bs, 0, 2048)) != -1) {
							bos.write(bs, 0, len);
						}
						bos.flush();
					} finally {
						bos.close();
					}
				} finally {
					bis.close();
				}
			}
		} finally {
			zipFile.close();
		}
	}

	public void stop() throws Exception {
		if (main != null) {
			main.getClass().getMethod("shutdown").invoke(main);
		}
	}

	public void stopGracefully() throws Exception {
		if (main != null) {
			main.getClass().getMethod("shutdownGracefully").invoke(main);
		}
	}

	public Object getSaturnApplication() {
		return saturnApplication;
	}

	public void setSaturnApplication(Object saturnApplication) {
		this.saturnApplication = saturnApplication;
	}

}
