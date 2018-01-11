package com.xl.game.tool;
import android.graphics.*;
/*
碰撞检测 

参考资料
 http://www.cnblogs.com/sevenyuan/p/7125642.html
*/
public class Coll
{
		
	//判断两条线是否相交
	public boolean isIntersect(double px1,double py1,double px2,double py2,double px3,double py3,double px4,double py4)//p1-p2 is or not intersect with p3-p4

	{

		boolean flag = false;

		double d = (px2-px1)*(py4-py3) - (py2-py1)*(px4-px3);

		if(d!=0)

		{

			double r = ((py1-py3)*(px4-px3)-(px1-px3)*(py4-py3))/d;

			double s = ((py1-py3)*(px2-px1)-(px1-px3)*(py2-py1))/d;

			if((r>=0) && (r <= 1) && (s >=0) && (s<=1))

			{

				flag = true;

			}

		}

		return flag;

	}

	/**
	 * 判断两条线是否相交 a 线段1起点坐标 b 线段1终点坐标 c 线段2起点坐标 d 线段2终点坐标 intersection 相交点坐标
	 * reutrn 是否相交: 0 : 两线平行 -1 : 不平行且未相交 1 : 两线相交
	 */
	public int segIntersect(Point A, Point B, Point C, Point D) {
		Point intersection = new Point();
		if (Math.abs(B.y - A.y) + Math.abs(B.x - A.x) + Math.abs(D.y - C.y)
				+ Math.abs(D.x - C.x) == 0) {
			if ((C.x - A.x) + (C.y - A.y) == 0) {
				System.out.println("ABCD是同一个点！");
			} else {
				System.out.println("AB是一个点，CD是一个点，且AC不同！");
			}
			return 0;
		}
		if (Math.abs(B.y - A.y) + Math.abs(B.x - A.x) == 0) {
			if ((A.x - D.x) * (C.y - D.y) - (A.y - D.y) * (C.x - D.x) == 0) {
				System.out.println("A、B是一个点，且在CD线段上！");
			} else {
				System.out.println("A、B是一个点，且不在CD线段上！");
			}
			return 0;
		}
		if (Math.abs(D.y - C.y) + Math.abs(D.x - C.x) == 0) {
			if ((D.x - B.x) * (A.y - B.y) - (D.y - B.y) * (A.x - B.x) == 0) {
				System.out.println("C、D是一个点，且在AB线段上！");
			} else {
				System.out.println("C、D是一个点，且不在AB线段上！");
			}
			return 0;
		}
		if ((B.y - A.y) * (C.x - D.x) - (B.x - A.x) * (C.y - D.y) == 0) {
//   System.out.println("线段平行，无交点！");
			return 0;
		}

		intersection
			.x= (((B.x - A.x) * (C.x - D.x)
						* (C.y - A.y) - C.x
						* (B.x - A.x) * (C.y - D.y) + A
						.x * (B.y - A.y) * (C.x - D.x))
						/ ((B.y - A.y) * (C.x - D.x) - (B
						.x - A.x) * (C.y - D.y)));
		intersection
			.y = (((B.y - A.y) * (C.y - D.y)
						* (C.x - A.x) - C.y
						* (B.y - A.y) * (C.x - D.x) + A
						.y * (B.x - A.x) * (C.y - D.y))
						/ ((B.x - A.x) * (C.y - D.y) - (B
						.y - A.y) * (C.x - D.x)));
		if ((intersection.x - A.x) * (intersection.x - B.x) <= 0
				&& (intersection.x - C.x)
				* (intersection.x - D.x) <= 0
				&& (intersection.y - A.y)
				* (intersection.y - B.y) <= 0
				&& (intersection.y - C.y)
				* (intersection.y - D.y) <= 0) {
			if(  (A.x == C.x && A.y == C.y ) || ( A.x == D.x && A.y == D.y )
				 || (B.x == C.x && B.y == C.y ) || ( B.x == D.x && B.y == D.y )){

				System.out.println("线段相交于端点上");
				return 2;

			}else {
				System.out.println("线段相交于点(" + intersection.x + ","
													 + intersection.y + ")！");
				return 1; // '相交
			}

		} else {
//   System.out.println("线段相交于虚交点(" + intersection.x + ","
//     + intersection.y + ")！");
			return -1; // '相交但不在线段上
		}
	}


//判断点(x,y)是否在矩形区域(rectx,recty,rectw,recth)内
	public static boolean isPointCollisionRect(int x,int y,int rectx,int recty,int rectw,int recth)
	{
		if(x>=rectx && x<rectx+rectw && y>=recty && y<recty+recth)
		{
			return true;
		}
		return false;
	}


// 矩形和圆形碰撞检测
	public static boolean IsCircleCollisionRect(float circleXPos, float circleYPos, float radius, float rectX, float rectY, float rectW, float rectH)
	{
    float arcR  = radius;
    float arcOx = circleXPos;
    float arcOy = circleYPos;

    //分别判断矩形4个顶点与圆心的距离是否<=圆半径；如果<=，说明碰撞成功   
    if(((rectX-arcOx) * (rectX-arcOx) + (rectY-arcOy) * (rectY-arcOy)) <= arcR * arcR)   
			return true;   
    if(((rectX+rectW-arcOx) * (rectX+rectW-arcOx) + (rectY-arcOy) * (rectY-arcOy)) <= arcR * arcR)   
			return true;   
    if(((rectX-arcOx) * (rectX-arcOx) + (rectY+rectH-arcOy) * (rectY+rectH-arcOy)) <= arcR * arcR)   
			return true;   
    if(((rectX+rectW-arcOx) * (rectX+rectW-arcOx) + (rectY+rectH-arcOy) * (rectY+rectH-arcOy)) <= arcR * arcR)   
			return true;

    //判断当圆心的Y坐标进入矩形内时X的位置，如果X在(rectX-arcR)到(rectX+rectW+arcR)这个范围内，则碰撞成功   
    float minDisX = 0;   
    if(arcOy >= rectY && arcOy <= rectY + rectH)
    {   
			if(arcOx < rectX)   
				minDisX = rectX - arcOx;   
			else if(arcOx > rectX + rectW)   
				minDisX = arcOx - rectX - rectW;   
			else    
				return true;   
			if(minDisX <= arcR)   
				return true;   
    }

    //判断当圆心的X坐标进入矩形内时Y的位置，如果X在(rectY-arcR)到(rectY+rectH+arcR)这个范围内，则碰撞成功
    float minDisY = 0;   
    if(arcOx >= rectX && arcOx <= rectX + rectW)
    {   
			if(arcOy < rectY)   
				minDisY = rectY - arcOy;   
			else if(arcOy > rectY + rectH)   
				minDisY = arcOy - rectY - rectH;   
			else  
				return true;   
			if(minDisY <= arcR)   
				return true;   
    }

    return false; 
	}

// 线段和线段碰撞检测
	/*
	 int IsLineCollisionLine(int p1, int p2, int p3, int p4)
	 {
	 float x1 = p1.x, x2 = p2.x, x3 = p3.x, x4 = p4.x;
	 float y1 = p1.y, y2 = p2.y, y3 = p3.y, y4 = p4.y;

	 float d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
	 // If d is zero, there is no intersection
	 if (d == 0) 
	 return FALSE;

	 // Get the x and y
	 float pre = (x1*y2 - y1*x2), post = (x3*y4 - y3*x4);
	 float x = ( pre * (x3 - x4) - (x1 - x2) * post ) / d;
	 float y = ( pre * (y3 - y4) - (y1 - y2) * post ) / d;

	 // Check if the x and y coordinates are within both lines
	 if ( x < MIN(x1, x2) || x > MAX(x1, x2) ||
	 x < MIN(x3, x4) || x > MAX(x3, x4) )
	 return FALSE;

	 if ( y < MIN(y1, y2) || y > MAX(y1, y2) ||
	 y < MIN(y3, y4) || y > MAX(y3, y4) ) 
	 return FALSE;

	 return TURE;
	 }
	 */

//矩形碰撞检测 (屏幕坐标系)
	public static boolean isCollisionRect(int x1, int y1, int w1, int h1, int x2, int  y2, int w2, int h2) 
	{  
		//Logcat.e(""+x1 + " " + y1 + " " + w1+ " "  + h1+ " "  + x2+ " "  + y2+ " "  + w2+ " "  + h2);

		//当矩形1 位于矩形2 的左侧  
		if (x1 > x2 && x1 >= x2 + w2)
		{  
			return false;  
			//当矩形1 位于矩形2 的右侧  
		} 
		else if (x1 < x2 && x1 + w1 <= x2)
		{ 
			return false;  
			//当矩形1 位于矩形2 的上方  
		} 
		else if (y1 > y2 && y1 >= y2+ h2) 
		{  
			return false;  
			//当矩形1 位于矩形2 的下方  
		} 
		else if (y1 < y2 && y1 + h1 <= y2) 
		{  
			return false;  
		}  

		//所有不会发生碰撞都不满足时，肯定就是碰撞了  
		return false;  
	}

//矩形碰撞检测 (笛卡尔坐标系)
	public static boolean isCollisionRect2(int x1, int y1, int w1, int h1, int x2, int  y2, int w2, int h2) 
	{  
		//Logcat.e(""+x1 + " " + y1 + " " + w1+ " "  + h1+ " "  + x2+ " "  + y2+ " "  + w2+ " "  + h2);

		//当矩形1 位于矩形2 的左侧  
		if (x1 > x2 && x1 >= x2 + w2)
		{  
			return false;  
			//当矩形1 位于矩形2 的右侧  
		} 
		else if (x1 < x2 && x1 + w1 <= x2)
		{ 
			return false;  
			//当矩形1 位于矩形2 的上方  
		} 
		else if (y1 > y2 && y1+h1 >= y2) 
		{  
			return false;  
			//当矩形1 位于矩形2 的下方  
		} 
		else if (y1 < y2 && y1  <= y2-h2) 
		{  
			return false;  
		}  

		//所有不会发生碰撞都不满足时，肯定就是碰撞了  
		return false;  
	}
	
	static float ex_toRad(float a)
	{
		return (a*3.14159265f)/180;
	}


	//点(x,y)旋转指定弧度r，得到旋转后的坐标
	//旋转一条水平线，得到旋转后的坐标
//参数：旋转中心点(px,py)，旋转横向半径rx，旋转纵向半径ry， 旋转后坐标指针(*x,*y)
	public static void toSpin(int px,int py,int rx,int ry,int r,Point point)
	{

		point.x=(int)( px+(rx)*Math.cos(ex_toRad(r)));
		point.y=(int)( py+(ry)*Math.sin(ex_toRad(r)));


	}

	//求两点之间距离 可用于计算圆与圆的碰撞(当两个圆的圆心距离大于它们的半径只和，那么碰撞成功)
	public static double getLineSize(double x,double y,double x2,double y2)
	{

		return Math.sqrt( (x2-x)*(x2-x)+ (y2-y)*(y2-y));
	}



	//求点与点之间弧度
	public static double getRadiam(float x,float y,float rx,float ry)
	{
		float dx,dy;
		dx=rx-x;
		dy=ry-y;
		double r=(Math.atan((dy) / (dx))*180/3.14159265f);

		//右下角
		if(dx>=0 && dy>=0)
			r=r;
		//左下角
		else if(dx<=0&& dy>=0)
			r=90+r+90;
		//左上角
		else if(dx<=0&&dy<=0)
			r=180+r;
		//右上角
		else if(dx>=0 && dy<=0)
			r=90+r+270;

		return r;
	}
	
	
	/**  封装方法：检测边到点距离 */
	static double pointToLine(int x1, int y1, int x2, int y2, int x0, int y0) {
	double space = 0;
	double a, b, c;
	a = getLineSize(x1, y1, x2, y2);// 线段的长度
	b = getLineSize(x1, y1, x0, y0);// (x1,y1)到点的距离
	c = getLineSize(x2, y2, x0, y0);// (x2,y2)到点的距离
	if (c <= 0.000001 || b <= 0.000001) {
	space = 0;
	return space;
	}
	if (a <= 0.000001) {
	space = b;
	return space;
	}
	if (c * c >= a * a + b * b) {
	space = b;
	return space;
	}
	if (b * b >= a * a + c * c) {
	space = c;
	return space;
	}
	double p = (a + b + c) / 2;// 半周长
	double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
	space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
	return space;
	}
	
	
}
