/*
 * J2ME Loader
 * Copyright (C) 2015-2016 Nickolay Savchenko
 * Copyright (C) 2017 Nikita Shakarun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ua.naiksoftware.j2meloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.dx.command.Main;

import org.microemu.android.asm.AndroidProducer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipException;

import javax.microedition.shell.ConfigActivity;

import ua.naiksoftware.util.FileUtils;
import ua.naiksoftware.util.Log;
import ua.naiksoftware.util.ZipUtils;

public class JarConverter extends AsyncTask<String, String, Boolean> {

	private static final String tag = "JarConverter";

	private final Context context;
	private String err = "Void error";
	private ProgressDialog dialog;

	private String appDir;
	private final File dirTmp;
	private static String targetJarName;

	public JarConverter(MainActivity context) {
		this.context = context;
		dirTmp = context.getDir("tmp",0);
		dirTmp.mkdirs();
	}

	@Override
	protected Boolean doInBackground(String... p1) {
		String pathToJar = p1[0];
		targetJarName = pathToJar.substring(pathToJar.lastIndexOf('/') + 1);
		String pathConverted = p1[1];
		Log.e(tag, "doInBackground$ pathToJar=" + pathToJar + " pathConverted="
				+ pathConverted);
		File inputJar = new File(pathToJar);
		File fixedJar;
		try {
			Log.e(tag,"开始fix");
			fixedJar = fixJar(inputJar);
		} catch (IOException e) {
			e.printStackTrace();
			err = "Can't convert\n";
			deleteTemp();
			return false;
		}
		if (!ZipUtils.unzip(fixedJar, dirTmp)) {
			err = "Brocken jar";
			deleteTemp();
			return false;
		}
		appDir = FileUtils.loadManifest(
				new File(dirTmp, "/META-INF/MANIFEST.MF")).get("MIDlet-Name");
		if (appDir == null) {
			err = "Brocken manifest";
			deleteTemp();
			return false;
		}
		appDir = appDir.replace(":", "").replace("/", "");
		File appConverted = new File(pathConverted, appDir);
		FileUtils.deleteDirectory(appConverted);
		appConverted.mkdirs();
		Log.e(tag, "appConverted=" + appConverted.getPath());
		Main.main(new String[]{
				"--dex", "--no-optimize", "--output=" + appConverted.getPath()
				+ ConfigActivity.MIDLET_DEX_FILE, fixedJar.getAbsolutePath()});
		File conf = new File(dirTmp, "/META-INF/MANIFEST.MF");
		try {
			FileUtils.copyFileUsingChannel(conf, new File(appConverted, ConfigActivity.MIDLET_CONF_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Extract other resources from jar.
		FileUtils.moveFiles(dirTmp.getPath(), pathConverted + appDir
				+ ConfigActivity.MIDLET_RES_DIR, new FilenameFilter() {
			public boolean accept(File dir, String fname) {
				if (fname.endsWith(".class") || fname.endsWith(".jar.jar")) {
					return false;
				} else {
					return true;
				}
			}
		});
		deleteTemp();
		return true;
	}

	@Override
	public void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setMessage(context.getText(R.string.converting_message));
		dialog.setTitle(R.string.converting_wait);
		dialog.show();
	}

	@Override
	public void onPostExecute(Boolean result) {
		Toast toast;
		if (result) {
			toast = Toast.makeText(context, context.getResources().getString(R.string.convert_complete) + " " + appDir, Toast.LENGTH_LONG);
			((MainActivity) context).updateApps();
		} else {
			toast = Toast.makeText(context, err, Toast.LENGTH_LONG);
		}
		dialog.dismiss();
		toast.show();
	}

	private File fixJar(File inputJar) throws IOException {
		Log.e(tag,"fixJar...");
		File fixedJar = new File(dirTmp, inputJar.getName() + ".jar");
		try {
			Log.e(tag,"processJar..."+inputJar.getPath()+" "+fixedJar.getPath());
			AndroidProducer.processJar(inputJar, fixedJar, true);
		} catch (ZipException e) {
			Log.e(tag,"processJar出错...");
			File unpackedJarFolder = new File(context.getApplicationInfo().dataDir, "tmp_fix");
			ZipUtils.unzip(inputJar, unpackedJarFolder);

			File repackedJar = new File(dirTmp, inputJar.getName());
			ZipUtils.zipFileAtPath(unpackedJarFolder, repackedJar);

			AndroidProducer.processJar(repackedJar, fixedJar, true);
			FileUtils.deleteDirectory(unpackedJarFolder);
			repackedJar.delete();
		}
		return fixedJar;
	}

	private void deleteTemp() {
		// Delete temp files
		FileUtils.deleteDirectory(dirTmp);
		File uriDir = new File(context.getApplicationInfo().dataDir, "uri_tmp");
		if (uriDir.exists()) {
			FileUtils.deleteDirectory(uriDir);
		}
	}

	public static String getTargetJarName() {
		return targetJarName;
	}
}
