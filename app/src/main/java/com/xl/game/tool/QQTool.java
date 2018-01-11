package com.xl.game.tool;
import android.content.*;
import android.net.*;
import android.content.pm.*;

public class QQTool
{
	
	//加入指定QQ群：
	public static void goToGroup(Context context,String uin)
	{
	String url11 = "mqqwpa://im/chat?chat_type=group&uin="+uin+"&version=1";
	context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
	}
	
	//加入指定QQ
	public static void goToQQ(Context context,String uin)
	{
		String url11 = "mqqwpa://im/chat?chat_type=wpa&uin="+uin+"&version=1";
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
		
	}
	public static boolean joinQQGroup(Context context, String str) {
        Context context2 = context;
        String str2 = str;
        Intent intent =  new Intent();
        Intent intent3 = intent;
        intent = intent3;
        StringBuffer stringBuffer = new StringBuffer();
        intent = intent.setData(Uri.parse(stringBuffer.append("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D").append(str2).toString()));
        try {
            context2.startActivity(intent3);
            return true;
        } catch (Exception e) {
            Exception exception = e;
            return false;
        }
    }
	
	public static boolean startToQQ(Context context) {
        Context context2 = context;
        String str = "com.tencent.mobileqq";
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        Intent intent3 = intent;
        intent = intent3.addCategory("android.intent.category.LAUNCHER");
        String str2 = (String) null;
        for (ResolveInfo resolveInfo : context2.getPackageManager().queryIntentActivities(intent3, 0)) {
            if (str.equals(resolveInfo.activityInfo.packageName)) {
                str2 = resolveInfo.activityInfo.name;
                break;
            }
        }
        if (str2 == null || str2.length() <= 0) {
            return false;
        }
        intent = intent3;
        ComponentName componentName = new ComponentName(str, str2);
        intent = intent.setComponent(componentName);
        intent = intent3.setFlags(270532608);
        context2.startActivity(intent3);
        return true;
    }
	
	
	
}
