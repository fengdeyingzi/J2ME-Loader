package javax.microedition.shell;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;
import com.xl.BaseConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.event.CanvasEvent;
import javax.microedition.lcdui.event.EventQueue;
import javax.microedition.midlet.MIDlet;
import javax.microedition.util.ContextHolder;
import ua.naiksoftware.j2meloader.R;
import ua.naiksoftware.util.Log;
import android.view.ViewGroup;

public class AppMicroActivity extends MicroActivity implements OnPadListener
{
  PadViewAB padview;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		BaseConfig.isApp=true;
		BaseConfig.activity=this;
		setVirtualKeyboard();
		applyConfiguration();
		super.onCreate(savedInstanceState);
		padview=new PadViewAB(this);
		padview.setOnPadListener(this);
		ViewGroup contentview =  (ViewGroup) findViewById(android.R.id.content);
		contentview.addView(padview);
	}
	
	//XL 获取Canvas
	private Canvas getCanvas()
	{
		if(current!=null && current instanceof Canvas)
		{
			return (Canvas)current;
		}
		return null;
	}

	//XL 发送按键事件
	private void postKeyEvent(int type,int code)
	{
		current. postEvent(CanvasEvent.getInstance(getCanvas(), type, code));

	}
	
	

	private void applyConfiguration() {
		try {
			int fontSizeSmall = BaseConfig.fontSizeSmall;
			int fontSizeMedium = BaseConfig.fontSizeMedium;
			int fontSizeLarge = BaseConfig.fontSizeLarge;
			boolean fontApplyDimensions = BaseConfig.fontApplyDimensions;

			int screenWidth = BaseConfig.screenWidth;
			int screenHeight = BaseConfig.screenHeight;
			int screenBackgroundColor = BaseConfig.screenBackgroundColor;
			int screenScaleRatio = BaseConfig.screenScaleRatio;
			boolean screenScaleToFit = BaseConfig.screenScaleToFit;
			boolean screenKeepAspectRatio = BaseConfig.screenKeepAspectRatio;
			boolean screenFilter = BaseConfig.screenFilter;
			boolean immediateMode = BaseConfig.immediateMode;
			boolean clearBuffer = BaseConfig.clearBuffer;

			Font.setSize(Font.SIZE_SMALL, fontSizeSmall);
			Font.setSize(Font.SIZE_MEDIUM, fontSizeMedium);
			Font.setSize(Font.SIZE_LARGE, fontSizeLarge);
			Font.setApplyDimensions(fontApplyDimensions);

			Canvas.setVirtualSize(screenWidth, screenHeight, screenScaleToFit,
														screenKeepAspectRatio, screenScaleRatio);
			Canvas.setFilterBitmap(screenFilter);
			EventQueue.setImmediate(immediateMode);
			Canvas.setBackgroundColor(screenBackgroundColor);
			Canvas.setClearBuffer(clearBuffer);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void setVirtualKeyboard() {
		/*
		int vkAlpha = sbVKAlpha.getProgress();
		int vkDelay = Integer.parseInt(tfVKHideDelay.getText().toString());
		int vkColorBackground = Integer.parseInt(tfVKBack.getText().toString(),
																						 16);
		int vkColorForeground = Integer.parseInt(tfVKFore.getText().toString(),
																						 16);
		int vkColorBackgroundSelected = Integer.parseInt(tfVKSelBack.getText()
																										 .toString(), 16);
		int vkColorForegroundSelected = Integer.parseInt(tfVKSelFore.getText()
																										 .toString(), 16);
		int vkColorOutline = Integer.parseInt(tfVKOutline.getText().toString(),
																					16);

		VirtualKeyboard vk = new VirtualKeyboard();

		vk.setOverlayAlpha(vkAlpha);
		vk.setHideDelay(vkDelay);
    /*
		if (keylayoutFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(keylayoutFile);
				DataInputStream dis = new DataInputStream(fis);
				vk.readLayout(dis);
				fis.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
		}

		vk.setColor(VirtualKeyboard.BACKGROUND, vkColorBackground);
		vk.setColor(VirtualKeyboard.FOREGROUND, vkColorForeground);
		vk.setColor(VirtualKeyboard.BACKGROUND_SELECTED,
								vkColorBackgroundSelected);
		vk.setColor(VirtualKeyboard.FOREGROUND_SELECTED,
								vkColorForegroundSelected);
		vk.setColor(VirtualKeyboard.OUTLINE, vkColorOutline);
/*
		VirtualKeyboard.LayoutListener listener = new VirtualKeyboard.LayoutListener() {
			public void layoutChanged(VirtualKeyboard vk) {
				try {
					FileOutputStream fos = new FileOutputStream(keylayoutFile);
					DataOutputStream dos = new DataOutputStream(fos);
					vk.writeLayout(dos);
					fos.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		vk.setLayoutListener(listener);
		*/
		ContextHolder.setVk(null);
	}
	
	
	public void loadMIDlet() {
		ArrayList<String> midlets = new ArrayList<>();
		LinkedHashMap<String, String> params = loadManifest(BaseConfig.startMIDlet);
		MIDlet.initProps(params);
		for (LinkedHashMap.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().matches("MIDlet-[0-9]+")) {
				midlets.add(entry.getValue());
			}
		}
		int size = midlets.size();
		String[] midletsNameArray = new String[size];
		String[] midletsClassArray = new String[size];
		for (int i = 0; i < size; i++) {
			String tmp = midlets.get(i);
			midletsClassArray[i] = tmp.substring(tmp.lastIndexOf(',') + 1).trim();
			midletsNameArray[i] = tmp.substring(0, tmp.indexOf(',')).trim();
		}
		if (size == 0) {
			Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
			finish();
		} else if (size == 1) {
			startMidlet(midletsClassArray[0]);
		} else if (size > 1) {
			//showMidletDialog(midletsNameArray, midletsClassArray);
		}
	}
	
	public  LinkedHashMap<String, String> loadManifest(String filename) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
			String line;
			int index;
			while ((line = br.readLine()) != null) {
				index = line.indexOf(':');
				if (index > 0) {
					params.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
				}
				if (line.length() > 0 && Character.isWhitespace(line.charAt(0))) {
					Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
					Map.Entry<String, String> entry = null;
					while (iter.hasNext()) {
						entry = iter.next();
					}
					params.put(entry.getKey(), entry.getValue() + line.substring(1));
				}
			}
			br.close();
		} catch (Throwable t) {
			System.out.println("getAppProperty() will not be available due to " + t.toString());
		}
		return params;
	}
	
	public void startMidlet(String mainClass) {
		//File dexSource = new File(pathToMidletDir, ConfigActivity.MIDLET_DEX_FILE);
		//File dexTargetDir = new File(getApplicationInfo().dataDir, ConfigActivity.TEMP_DEX_DIR);
		
		//File dexTarget = new File(dexTargetDir, ConfigActivity.MIDLET_DEX_FILE);
		try {
			//FileUtils.copyFileUsingChannel(dexSource, dexTarget);
			//ClassLoader loader = new MyClassLoader(dexTarget.getAbsolutePath(),															 getApplicationInfo().dataDir, null, getClassLoader(), pathToMidletDir + ConfigActivity.MIDLET_RES_DIR);
			Log.d("inf", "load main: " + mainClass + " from dex:" );
			//MIDlet midlet = (MIDlet) loader.loadClass(mainClass).newInstance();
			MIDlet midlet = (MIDlet) Class.forName(mainClass).newInstance();
			midlet.startApp();
			loaded = true;
		} catch (Throwable t) {
			Log.d("err", t.toString() + "/n" + t.getMessage());
			//showErrorDialog(t.getMessage());
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onKeyDown(Canvas.KEY_SOFT_RIGHT);
				return true;
			case KeyEvent.KEYCODE_MENU:
				onKeyDown(Canvas.KEY_FIRE);
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onKeyUp(Canvas.KEY_SOFT_RIGHT);
				return true;
			case KeyEvent.KEYCODE_MENU:
				onKeyUp(Canvas.KEY_FIRE);
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onKeyDown(int keycode)
	{
		postKeyEvent(CanvasEvent.KEY_PRESSED,keycode);
	}

	@Override
	public void onKeyUp(int keycode)
	{
		postKeyEvent(CanvasEvent.KEY_RELEASED, keycode);
	}
}
