package ua.naiksoftware.j2meloader;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.os.Build;

public class BaseActivity extends AppCompatActivity
{
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if(Build.VERSION.SDK_INT<11)
		{
			if(event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode()==KeyEvent.KEYCODE_BACK)
			{
				this.onBackPressed();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
