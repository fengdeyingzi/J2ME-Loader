package javax.microedition.shell;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import com.xl.game.tool.Coll;
import com.xl.game.tool.DisplayUtil;
import javax.microedition.shell.PadViewAB.CirPadButton;

/*
带ab键的摇杆键盘
*/
public class PadViewAB extends View implements Runnable
{

	@Override
	public void run()
	{
	  if(btn_pad.isDown() && btn_pad.getTouch()==0)
		{
			btn_pad.keyUp();
			btn_pad.keyDown();
			postDelayed(this,250);
		}
	}
	

	private Paint paint_line[];
	private Paint paint_background[];
	private Paint paint_high;
	private OnPadListener listener;
	private CirPadButton btn_pad;
	boolean ispost; // 是否发送定时器
	int _UP=-1,_DOWN=-2,_LEFT=-3,_RIGHT=-4;
	int _1=49,
	_2=50,
	_3=51,
	_4=52,
	_5=53,
	_6=54,
	_7=55,
	_8=56,
	_9=57;

	CirButton btn_a,btn_b;
	Runnable runnable;

	//按钮类
	public class CirButton
	{
		private int x;
		private int y;
		private int r;
		private int touch_id;
		private boolean isDown;
		int id;

		public CirButton(int r)
		{
			this.x=x;
			this.y=y;
			this.r=r;
		}
		
		public void setId(int id)
		{
			this.id=id;
		}

		public void setXY(int x,int y)
		{
			this.x=x;
			this.y=y;
		}

		//判断按钮是否点中
		public boolean isColl(int x,int y)
		{
			if(Coll.getLineSize(this.x,this.y,x,y) <= r)
			{
				return true;
			}
			return false;
		}
		//判断按钮是否按下
		public boolean isDown()
		{
			return this.isDown;
		}

		//判断手指是否按下
		public boolean isDown(int id)
		{
			return (this.touch_id==id)?true:false;

		}

		//按钮按下
		public void keyDown()
		{
			if(this.isDown==false)
			{
			this.isDown=true;
			if(listener!=null)
				listener.onKeyDown(id);
			}
		}
		public void keyDown(int touch_id)
		{
			if(this.isDown==false)
			{
			this.isDown=true;
			this.touch_id=touch_id;
			
				if(listener!=null && touch_id==0)
					listener.onKeyDown(id);
			}
		}

		//按钮释放
		public void keyUp()
		{
			if(this.isDown==true)
			{
			this.isDown=false;
				if(listener!=null)
					listener.onKeyUp(id);
			}
		}
		//设置手指id
		public void setTouch(int id)
		{
			this.touch_id = id;
		}

		//获取手指id
		public int getTouch()
		{
			return this.touch_id;
		}
		
		public void draw(Canvas canvas)
		{
			int index=0;
			if(isDown)index=1;
			//画ab键
			canvas.drawCircle(x,y,r*5/6,paint_background[index]);
			canvas.drawCircle(x,y,r,paint_line[index]);
			
			
		}

	}
	
	
	
//////////////////////////////////////////////////
	//按钮类
	public class CirPadButton
	{
		private int x;
		private int y;
		private int r_pad; //底盘半径
		private int r_smallpad; //操纵杆
		private int round; //角度值
		private int size; //圆心到操纵杆距离
		private int touch_id;
		private boolean isDown;
		private int key; //按下的按键 上下左右
    boolean isKeep;//是否连按
    

		
		public CirPadButton(int r)
		{
			this.x=x;
			this.y=y;
			this.r_pad=r;
			this.r_smallpad = r/2;
		}

		public void setXY(int x,int y)
		{
			this.x=x;
			this.y=y;
		}

		//判断按钮是否点中
		public boolean isColl(int x,int y)
		{
			if(Coll.getLineSize(this.x,this.y,x,y) <= r_pad)
			{
				/*
				 //获取角度
				 setRound((int)Coll.getRadiam(this.x,this.y,x,y));
				 //获取距离
				 setSize((int)Coll.getLineSize(x,y,this.x,this.y));
				 */
				return true;
			}
			return false;
		}

		//移动事件
		public void move(int x,int y)
		{
			//获取角度
			setRound((int)Coll.getRadiam(this.x,this.y,x,y));
			//获取距离
			setSize((int)Coll.getLineSize(x,y,this.x,this.y));
			if(getDecitation(getRound())!=key)
			{
				if(listener!=null)
				{
					listener.onKeyUp(key);
					key = getDecitation(getRound());
					listener.onKeyDown(key);
				}
			}

		}


		//判断按钮是否按下
		public boolean isDown()
		{
			return this.isDown;
		}
		//按钮按下
		public void keyDown()
		{

			if(!this.isDown)
			{
				this.isDown=true;
				if(listener!=null && this.touch_id==0)
					listener.onKeyDown(key);
			}

		}
		public void keyDown(int id,int x,int y)
		{
			//获取角度
			setRound((int)Coll.getRadiam(this.x,this.y,x,y));
			//获取距离
			setSize((int)Coll.getLineSize(x,y,this.x,this.y));
			//生成key
			key = getDecitation(getRound());
			this.touch_id=id;
			this.keyDown();
		}

		//按钮释放
		public void keyUp()
		{
			if(isDown)
			{
				this.isDown=false;
				this.size=0;
				if(listener!=null)
				{
					listener.onKeyUp(key);
				}
			}
		}

		//设置角度值
		public void setRound(int r)
		{
			this.round = r;
			if(this.round>=360)
			{
				this.round = this.round%360;
			}
			if(this.round<0)
			{
				this.round = this.round%360+360;
			}
		}
		//获取角度值
		public int getRound()
		{
			return this.round;
		}
		//设置距离值
		public void setSize(int size)
		{
			this.size = size;
			if(this.size>r_pad)
			{
				this.size=r_pad;
			}
		}
		//获取距离值
		public int getSize()
		{
			return this.size;
		}


		//设置手指id
		public void setTouch(int id)
		{
			this.touch_id = id;
		}

		//获取手指id
		public int getTouch()
		{
			return this.touch_id;
		}

		public void draw(Canvas canvas)
		{
			Point point=new Point();
			Coll.toSpin(x,y, size,size,round,point);
			int index=0;
			if(isDown) // 画高亮光标
			{
				index = 1;
				canvas.drawCircle(point.x,point.y,r_smallpad*6/4,paint_high);
			}

			//画摇杆键盘
			{
				canvas.drawCircle(x,y,r_pad,paint_line[index]);
				canvas.drawCircle(x,y,r_pad, paint_background[index]);
				canvas.drawCircle(point.x,point.y,r_smallpad, paint_line[index]);
			  canvas.drawCircle(point.x,point.y,r_smallpad, paint_background[index]);
			}

			

			//绘制手指id
			paint_high.setTextSize(30);
			canvas.drawText(""+getTouch(),80,80,paint_high);

		}

	}

	
	
	//根据角度获取方向 
	/*
	1          2           3
	204 248    249 293    294 338
	4          5           6
	159 203               339 22
	7          8           9
	114 158    68 113     23 67
	*/
	private int getDecitation(int span)
	{
		if((span>=339 && span<=360) || (span>=0 && span<=22))
		{
			return _RIGHT;
		}
		else if(span>=23 && span<=67)
		{
			return _9;
		}
		else if(span>=68 && span<=113)
		{
			return _DOWN;
		}
		else if(span>=114 && span<=158)
		{
			return _7;
		}
		else if(span>=159 && span<=203)
		{
			return _LEFT;
		}
		else if(span>=204 && span<=248)
		{
			return _1;
		}
		else if(span>=249 && span<=293)
		{
			return _UP;
		}
		else if(span>=294 && span<=338)
		{
			return _3;
		}
		else
		{
			return _UP;
		}
	}



	public PadViewAB(Context context)
	{
		super(context);
		initView();
	}

	////////////////////////////////////////////////////
	private void initView()
	{
		this.runnable=this;
		this.btn_pad = new CirPadButton(DisplayUtil.dip2px(getContext(),60));
		int r = DisplayUtil.dip2px(getContext(),30);
		paint_high = new Paint();
		paint_high.setColor(0x8080baf0);
		paint_high.setAntiAlias(true);
		paint_line = new Paint[2];
		paint_line[0]= new Paint();
		paint_line[1] = new Paint();
		paint_line[0].setAntiAlias(true);
		paint_line[0].setStrokeWidth(DisplayUtil.dip2px(getContext(),2));
		paint_line[0].setColor(0xe0f0f0f0);
		paint_line[0].setStyle(Paint.Style.STROKE);

		paint_line[1].setAntiAlias(true);
		paint_line[1].setStrokeWidth(DisplayUtil.dip2px(getContext(),2));
		paint_line[1].setColor(0xe060a0f0);
		paint_line[1].setStyle(Paint.Style.STROKE);

		paint_background = new Paint[2];
		paint_background[0] = new Paint();
		paint_background[1] = new Paint();
		paint_background[0].setAntiAlias(true);
		paint_background[0].setColor(0x80808080);
		paint_background[0].setStyle(Paint.Style.FILL);

		paint_background[1].setAntiAlias(true);
		paint_background[1].setColor(0x60f0f0f0);
		paint_background[1].setStyle(Paint.Style.FILL);

		this.btn_a = new CirButton(r);
		this.btn_b=new CirButton(r);
    this.btn_a.setId(-5);
		this.btn_b.setId(48);
	}


	public void setOnPadListener(OnPadListener listener)
	{
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int index=0;
		int x=0,y=0;
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN: //按键按下 不一定是1号手指 因为1号手指可能被其它view获得
				//获取手指排列序号
				index=event.getActionIndex();
				if(index >=0)
				{
					x = (int)event.getX(index);
					y= (int)event.getY(index);

					if(btn_pad.isColl(x,y))
					{
						btn_pad.keyDown(0,x,y);
						btn_pad.move(x,y);
						if(!ispost)
						{
            postDelayed(this,250); ispost=true;
						}
					}
					else if(btn_a.isColl(x,y))
					{
						btn_a.keyDown(0);
					}
					else if(btn_b.isColl(x,y))
					{
						btn_b.keyDown(0);
					}
					else
						return false;
				}

				break;
			case MotionEvent.ACTION_POINTER_1_DOWN: //1号手指按下
				//通过手指id寻找序号
				index=event.findPointerIndex(0);
				if(index >=0)
				{
					x = (int)event.getX(index);
					y= (int)event.getY(index);

					if(btn_pad.isColl(x,y))
					{
						btn_pad.keyDown(0,x,y);
						btn_pad.move(x,y);
					}
					else if(btn_a.isColl(x,y))
					{
						btn_a.keyDown(0);
					}
					else if(btn_b.isColl(x,y))
					{
						btn_b.keyDown(0);
					}
					else
						return false;
				}

				break;
			case MotionEvent.ACTION_POINTER_2_DOWN: //2号手指按下
				//
				index=event.findPointerIndex(1);
				if(index >=0)
				{
					x = (int)event.getX(index);
					y= (int)event.getY(index);

					if(btn_pad.isColl(x,y))
					{
						btn_pad.keyDown(1,x,y);
						btn_pad.move(x,y);
					}
					else if(btn_a.isColl(x,y))
					{
						btn_a.keyDown(1);
					}
					else if(btn_b.isColl(x,y))
					{
						btn_b.keyDown(1);
					}
					else
						return false;
				}

				break;
			case MotionEvent.ACTION_MOVE: //移动，需要获取手指id
				//获取手指索引
				index = event.findPointerIndex(btn_pad.getTouch());
				if(index>=0)
				{
					x = (int)event.getX(index);
					y= (int)event.getY(index);
					if(btn_pad.isDown())
						btn_pad.move(x,y);
				}
				else
					return false;
				break;
			case MotionEvent.ACTION_POINTER_1_UP: //1号手指抬起
				if(btn_pad.getTouch()==0)
				{
					btn_pad.keyUp();
					if(ispost)
					{
					removeCallbacks(this);
					ispost=false;
				  }
				}
				if(btn_a.getTouch()==0)
				  btn_a.keyUp();
				if(btn_b.getTouch()==0)
					btn_b.keyUp();
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				if(btn_pad.getTouch()==1)
				{
					btn_pad.keyUp();
					if(ispost)
					{
						removeCallbacks(this);
						ispost=false;
					}
				}
				if(btn_a.getTouch()==1)
					btn_a.keyUp();
				if(btn_b.getTouch()==1)
					btn_b.keyUp();
				break;
			case MotionEvent.ACTION_UP: //按键释放 不一定是1号手指
				if(ispost)
				{
					removeCallbacks(this);
					ispost=false;
				}
			  btn_pad.keyUp();
				btn_a.keyUp();
				btn_b.keyUp();
				break;
		}
		invalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(canvas);
		btn_pad.draw(canvas);
    btn_a.draw(canvas);
		btn_b.draw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		// TODO: Implement this method
		super.onLayout(changed, left, top, right, bottom);
		int width= right-left;
		int height = bottom -top;
		this.btn_pad.setXY(width*1/4,height-width*1/4);
		this.btn_b.setXY(width* 5/6-DisplayUtil.dip2px(getContext(),60),height-DisplayUtil.dip2px(getContext(),90));
		this.btn_a.setXY(width* 5/6,height-DisplayUtil.dip2px(getContext(),50));
		
	}




}

