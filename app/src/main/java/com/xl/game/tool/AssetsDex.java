package com.xl.game.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import dalvik.system.DexFile;

/*
 在程序初始化的地方，如Application或者启动的Activity上，用AssetsDex.install(context)

*/



public class AssetsDex {

	static final String TAG = "AssetsDex";
	private static final int MAX_SUPPORTED_SDK_VERSION = 20;
	private static final int MIN_SDK_VERSION = 4;
	private static final Set<String> installedApk = new HashSet<String>();
	private static boolean installed = false;

	private AssetsDex() {
	}

	public static void install(Context context) {
		if (installed) {
			return;
		}
		ensureLibs(context);
		Log.i(TAG, "install");
		if (Build.VERSION.SDK_INT < MIN_SDK_VERSION) {
			throw new RuntimeException("Multi dex installation failed. SDK "
					+ Build.VERSION.SDK_INT
					+ " is unsupported. Min SDK version is " + MIN_SDK_VERSION
					+ ".");
		}
		try {
			ApplicationInfo applicationInfo = getApplicationInfo(context);
			if (applicationInfo == null) {
				// Looks like running on a test Context, so just return without
				// patching.
				return;
			}
			synchronized (installedApk) {
				String apkPath = applicationInfo.sourceDir;
				if (installedApk.contains(apkPath)) {
					return;
				}
				installedApk.add(apkPath);
				if (Build.VERSION.SDK_INT > MAX_SUPPORTED_SDK_VERSION) {
					Log.w(TAG,
							"MultiDex is not guaranteed to work in SDK version "
									+ Build.VERSION.SDK_INT
									+ ": SDK version higher than "
									+ MAX_SUPPORTED_SDK_VERSION
									+ " should be backed by "
									+ "runtime with built-in multidex capabilty but it's not the "
									+ "case here: java.vm.version=\""
									+ System.getProperty("java.vm.version")
									+ "\"");
				}
				/*
				 * The patched class loader is expected to be a descendant of
				 * dalvik.system.BaseDexClassLoader. We modify its
				 * dalvik.system.DexPathList pathList field to append additional
				 * DEX file entries.
				 */
				ClassLoader loader;
				try {
					loader = context.getClassLoader();
				} catch (RuntimeException e) {
					/*
					 * Ignore those exceptions so that we don't break tests
					 * relying on Context like a android.test.mock.MockContext
					 * or a android.content.ContextWrapper with a null base
					 * Context.
					 */
					Log.w(TAG,
							"Failure while trying to obtain Context class loader. "
									+ "Must be running in test mode. Skip patching.",
							e);
					return;
				}
				if (loader == null) {
					// Note, the context class loader is null when running
					// Robolectric tests.
					Log.e(TAG,
							"Context class loader is null. Must be running in test mode. "
									+ "Skip patching.");
					return;
				}
				File dexDir = context.getDir("outdex", Context.MODE_PRIVATE);
				File[] szFiles = dexDir.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".zip");
					}
				});
				List<File> files = new ArrayList<File>();
				for (File f : szFiles) {
					Log.i(TAG, "load file:" + f.getName());
					files.add(f);
				}
				Log.i(TAG, "loader before:" + context.getClassLoader());
				installSecondaryDexes(loader, dexDir, files);
				Log.i(TAG, "loader end:" + context.getClassLoader());
			}
		} catch (Exception e) {
			Log.e(TAG, "Multidex installation failure", e);
			throw new RuntimeException("Multi dex installation failed ("
					+ e.getMessage() + ").");
		}
		installed = true;
		Log.i(TAG, "install done");
	}

	private static ApplicationInfo getApplicationInfo(Context context)
			throws NameNotFoundException {
		PackageManager pm;
		String packageName;
		try {
			pm = context.getPackageManager();
			packageName = context.getPackageName();
		} catch (RuntimeException e) {
			/*
			 * Ignore those exceptions so that we don't break tests relying on
			 * Context like a android.test.mock.MockContext or a
			 * android.content.ContextWrapper with a null base Context.
			 */
			Log.w(TAG,
					"Failure while trying to obtain ApplicationInfo from Context. "
							+ "Must be running in test mode. Skip patching.", e);
			return null;
		}
		if (pm == null || packageName == null) {
			// This is most likely a mock context, so just return without
			// patching.
			return null;
		}
		ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName,
				PackageManager.GET_META_DATA);
		return applicationInfo;
	}

	public static void ensureLibs(Context context) {

		AssetManager assetManager = context.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			File outdex = context.getDir("outdex", Context.MODE_PRIVATE);
			outdex.mkdir();
			File dex = context.getDir("outdex", Context.MODE_PRIVATE);
			dex.mkdir();
			in = assetManager.open("classes.zip");
			File f = new File(dex, "classes.zip");
			if (f.exists() && f.length() == in.available()) {
				Log.i(TAG, "classes.zip no change");
				return;
			}
			Log.i(TAG, "classes.zip chaneged");
			out = new FileOutputStream(f);

			byte[] buffer = new byte[102400];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			Log.i(TAG, "classes.zip copy over");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void installSecondaryDexes(ClassLoader loader, File dexDir,
			List<File> files) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException,
			InvocationTargetException, NoSuchMethodException, IOException {
		if (!files.isEmpty()) {
			if (Build.VERSION.SDK_INT >= 19) {
				V19.install(loader, files, dexDir);
			} else if (Build.VERSION.SDK_INT >= 14) {
				V14.install(loader, files, dexDir);
			} else {
				V4.install(loader, files);
			}
		}
	}

	/**
	 * Locates a given field anywhere in the class inheritance hierarchy.
	 *
	 * @param instance
	 *            an object to search the field into.
	 * @param name
	 *            field name
	 * @return a field object
	 * @throws NoSuchFieldException
	 *             if the field cannot be located
	 */
	private static Field findField(Object instance, String name)
			throws NoSuchFieldException {
		for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz
				.getSuperclass()) {
			try {
				Field field = clazz.getDeclaredField(name);
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				return field;
			} catch (NoSuchFieldException e) {
				// ignore and search next
			}
		}
		throw new NoSuchFieldException("Field " + name + " not found in "
				+ instance.getClass());
	}

	/**
	 * Locates a given method anywhere in the class inheritance hierarchy.
	 *
	 * @param instance
	 *            an object to search the method into.
	 * @param name
	 *            method name
	 * @param parameterTypes
	 *            method parameter types
	 * @return a method object
	 * @throws NoSuchMethodException
	 *             if the method cannot be located
	 */
	private static Method findMethod(Object instance, String name,
			Class<?>... parameterTypes) throws NoSuchMethodException {
		for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz
				.getSuperclass()) {
			try {
				Method method = clazz.getDeclaredMethod(name, parameterTypes);
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				return method;
			} catch (NoSuchMethodException e) {
				// ignore and search next
			}
		}
		throw new NoSuchMethodException("Method " + name + " with parameters "
				+ Arrays.asList(parameterTypes) + " not found in "
				+ instance.getClass());
	}

	/**
	 * Replace the value of a field containing a non null array, by a new array
	 * containing the elements of the original array plus the elements of
	 * extraElements.
	 * 
	 * @param instance
	 *            the instance whose field is to be modified.
	 * @param fieldName
	 *            the field to modify.
	 * @param extraElements
	 *            elements to append at the end of the array.
	 */
	private static void expandFieldArray(Object instance, String fieldName,
			Object[] extraElements) throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field jlrField = findField(instance, fieldName);
		Object[] original = (Object[]) jlrField.get(instance);
		Object[] combined = (Object[]) Array.newInstance(original.getClass()
				.getComponentType(), original.length + extraElements.length);
		System.arraycopy(original, 0, combined, 0, original.length);
		System.arraycopy(extraElements, 0, combined, original.length,
				extraElements.length);
		Log.i(TAG, "install 4");
		jlrField.set(instance, combined);
		Log.i(TAG, "install 5");
	}

	/**
	 * Installer for platform versions 19.
	 */
	private static final class V19 {
		private static void install(ClassLoader loader,
				List<File> additionalClassPathEntries, File optimizedDirectory)
				throws IllegalArgumentException, IllegalAccessException,
				NoSuchFieldException, InvocationTargetException,
				NoSuchMethodException {
			/*
			 * The patched class loader is expected to be a descendant of
			 * dalvik.system.BaseDexClassLoader. We modify its
			 * dalvik.system.DexPathList pathList field to append additional DEX
			 * file entries.
			 */
			Log.i(TAG, "install 1");
			Field pathListField = findField(loader, "pathList");
			Object dexPathList = pathListField.get(loader);
			Log.i(TAG, "install 2");
			ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
			expandFieldArray(
					dexPathList,
					"dexElements",
					makeDexElements(dexPathList, new ArrayList<File>(
							additionalClassPathEntries), optimizedDirectory,
							suppressedExceptions));
			Log.i(TAG, "install 3");
			if (suppressedExceptions.size() > 0) {
				for (IOException e : suppressedExceptions) {
					Log.w(TAG, "Exception in makeDexElement", e);
				}
				Field suppressedExceptionsField = findField(loader,
						"dexElementsSuppressedExceptions");
				IOException[] dexElementsSuppressedExceptions = (IOException[]) suppressedExceptionsField
						.get(loader);
				if (dexElementsSuppressedExceptions == null) {
					dexElementsSuppressedExceptions = suppressedExceptions
							.toArray(new IOException[suppressedExceptions
									.size()]);
				} else {
					IOException[] combined = new IOException[suppressedExceptions
							.size() + dexElementsSuppressedExceptions.length];
					suppressedExceptions.toArray(combined);
					System.arraycopy(dexElementsSuppressedExceptions, 0,
							combined, suppressedExceptions.size(),
							dexElementsSuppressedExceptions.length);
					dexElementsSuppressedExceptions = combined;
				}
				suppressedExceptionsField.set(loader,
						dexElementsSuppressedExceptions);
			}
		}

		/**
		 * A wrapper around
		 * {@code private static final dalvik.system.DexPathList#makeDexElements}
		 * .
		 */
		private static Object[] makeDexElements(Object dexPathList,
				ArrayList<File> files, File optimizedDirectory,
				ArrayList<IOException> suppressedExceptions)
				throws IllegalAccessException, InvocationTargetException,
				NoSuchMethodException {
			Log.i(TAG, "install 9");
			Method makeDexElements = findMethod(dexPathList, "makeDexElements",
					ArrayList.class, File.class, ArrayList.class);
			return (Object[]) makeDexElements.invoke(dexPathList, files,
					optimizedDirectory, suppressedExceptions);
		}
	}

	/**
	 * Installer for platform versions 14, 15, 16, 17 and 18.
	 */
	private static final class V14 {
		private static void install(ClassLoader loader,
				List<File> additionalClassPathEntries, File optimizedDirectory)
				throws IllegalArgumentException, IllegalAccessException,
				NoSuchFieldException, InvocationTargetException,
				NoSuchMethodException {
			/*
			 * The patched class loader is expected to be a descendant of
			 * dalvik.system.BaseDexClassLoader. We modify its
			 * dalvik.system.DexPathList pathList field to append additional DEX
			 * file entries.
			 */
			Field pathListField = findField(loader, "pathList");
			Object dexPathList = pathListField.get(loader);
			expandFieldArray(
					dexPathList,
					"dexElements",
					makeDexElements(dexPathList, new ArrayList<File>(
							additionalClassPathEntries), optimizedDirectory));
		}

		/**
		 * A wrapper around
		 * {@code private static final dalvik.system.DexPathList#makeDexElements}
		 * .
		 */
		private static Object[] makeDexElements(Object dexPathList,
				ArrayList<File> files, File optimizedDirectory)
				throws IllegalAccessException, InvocationTargetException,
				NoSuchMethodException {
			Method makeDexElements = findMethod(dexPathList, "makeDexElements",
					ArrayList.class, File.class);
			return (Object[]) makeDexElements.invoke(dexPathList, files,
					optimizedDirectory);
		}
	}

	/**
	 * Installer for platform versions 4 to 13.
	 */
	private static final class V4 {
		private static void install(ClassLoader loader,
				List<File> additionalClassPathEntries)
				throws IllegalArgumentException, IllegalAccessException,
				NoSuchFieldException, IOException {
			/*
			 * The patched class loader is expected to be a descendant of
			 * dalvik.system.DexClassLoader. We modify its fields mPaths,
			 * mFiles, mZips and mDexs to append additional DEX file entries.
			 */
			int extraSize = additionalClassPathEntries.size();
			Field pathField = findField(loader, "path");
			StringBuilder path = new StringBuilder(
					(String) pathField.get(loader));
			String[] extraPaths = new String[extraSize];
			File[] extraFiles = new File[extraSize];
			ZipFile[] extraZips = new ZipFile[extraSize];
			DexFile[] extraDexs = new DexFile[extraSize];
			for (ListIterator<File> iterator = additionalClassPathEntries
					.listIterator(); iterator.hasNext();) {
				File additionalEntry = iterator.next();
				String entryPath = additionalEntry.getAbsolutePath();
				path.append(':').append(entryPath);
				int index = iterator.previousIndex();
				extraPaths[index] = entryPath;
				extraFiles[index] = additionalEntry;
				extraZips[index] = new ZipFile(additionalEntry);
				extraDexs[index] = DexFile.loadDex(entryPath, entryPath
						+ ".dex", 0);
			}
			pathField.set(loader, path.toString());
			expandFieldArray(loader, "mPaths", extraPaths);
			expandFieldArray(loader, "mFiles", extraFiles);
			expandFieldArray(loader, "mZips", extraZips);
			expandFieldArray(loader, "mDexs", extraDexs);
		}
	}

}

