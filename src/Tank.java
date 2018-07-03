import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import org.omg.PortableInterceptor.DISCARDING;

public class Tank {
	
	public static final int XSPEED= 7;
	public static final int YSPEED= 7;
	
	public static final int HEIGHT= 30;
	public static final int WIDTH= 30;
	
	TankClient tc;
	
	private boolean automation=false;//是否自动化
	
	public boolean isAutomation() {
		return automation;
	}

	public void setAutomation(boolean automation) {
		this.automation = automation;
	}

	BloodBar bb = new BloodBar();
	
	private int life=10;//生命值
	
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	private int x,y;
	
	private int oldx,oldy;//上一步位置
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
    private boolean live=true;//生死
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	private boolean good = true;
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}
   
	private boolean bL=false,bU=false,bR=false,bD=false;//方向布尔值
	
	enum Direction {L,LU,U,RU,R,RD,D,LD,STOP};//枚举八个方向
	
	private static Random r = new Random();//多个对象共享一个随机数生成器；
	private Direction dir = Direction.STOP;//初始化移动方向为停止
	private Direction ptDir = Direction.D;//初始化炮筒方向朝下
	
	private int step =r.nextInt(12)+3;
	
	public Tank(int x,int y,boolean good,Direction dir,TankClient tc,boolean am) {
		this(x,y,good);
		this.tc=tc;
		this.dir=dir;
		this.automation=am;
	}
	
	public Tank(int x, int y,boolean good) {
		this.x = x;
		this.y = y;
		this.oldx=x;
		this.oldy=y;
		this.good=good;
	}
	
	public void draw(Graphics g) {
		Color c = g.getColor();
		if(!live || this.collideWhitWall(tc.w1) ||this.collideWhitWall(tc.w2) 
				||this.collideWhitWall(tc.w3) ||this.collideWhitWall(tc.w5)) {
			if(!good) {
			  tc.tanks.remove(this);
			 }
			return;
		}
		if(good)
		g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.fillOval(x, y, WIDTH,HEIGHT);
		g.setColor(c);
		if(this.good) {
			bb.draw(g);//血条
		}
		switch(ptDir) {
		case L: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x, y+ Tank.HEIGHT/2);
			break;
		case LU:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x, y);
			break;
		case U: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x+Tank.WIDTH/2, y);
			break;
		case RU: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x+ Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x+Tank.WIDTH, y+ Tank.HEIGHT/2);
			break;
		case RD: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH, y+ Tank.HEIGHT);
			break;
		case D: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x+Tank.WIDTH/2, y+ Tank.HEIGHT);
			break;
		case LD: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x, y+ Tank.HEIGHT);
			break;
		case STOP:break;
		}
		move();

	}
	
	
	
	void move() {
		
		this.oldx=x;
		this.oldy=y;
		
		switch(dir) {
		case L: 
			x-=XSPEED;break;
		case LU:
			x-=XSPEED;y-=YSPEED;break;
		case U: 
			y-=YSPEED;break;
		case RU: 
			x+=XSPEED;y-=YSPEED;break;
		case R:
			x+=XSPEED;break;
		case RD: 
			x+=XSPEED;y+=YSPEED;break;
		case D: 
			y+=YSPEED;break;
		case LD: 
			x-=XSPEED;y+=YSPEED;break;
		case STOP:break;
		}
		if(this.dir!= Direction.STOP) {
			this.ptDir =this.dir;
		}
		/*if(x<0) x=0;
		if(y>30) y=30;*/
		if(x+ Tank.WIDTH>TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT >TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		
		if(!good || automation==true) {//如果是坏蛋或者自己按下F7令自动化变为true
			Direction[] dirs = Direction.values();
			//枚举类型可以用.values变成数组
			if(step == 0) {
				step=r.nextInt(12)+3;//随机生成指定步数
				int rn = r.nextInt(dirs.length);//获取方向数组的下标值
			    dir = dirs[rn];//方向等于随机获取方向数组的下标值
			 }
			  step--;
			  if(r.nextInt(40)>38) {//防止敌方开火过于密集,敌方坦克需要随机生成40以内的数大于38才开火
			    this.fire();
			  }
			  Random r1 = new Random();//创建一个专门用于已方坦克产生随机数的对象 否则将按照敌方随机数计算 开火密集；
			  if(good && r1.nextInt(40)>30)//如果是自己 开始执行自动化就超级开火
				  this.fire1();
	      }
		
	}
	
	
	public void keyPressed(KeyEvent e) {//键盘按下
		int key = e.getKeyCode();
		switch(key) {
		   case KeyEvent.VK_LEFT:
			  bL = true;
			  break;
		   case KeyEvent.VK_UP:
			  bU = true;
			  break;
		   case KeyEvent.VK_RIGHT:
			  bR = true;
			  break;
		   case KeyEvent.VK_DOWN:
			  bD= true;
			  break;    
		}
		locateDirection();//判断方向
	}
	
	void locateDirection() {//判断八个方向
		if(bL && !bU && !bR && !bD) dir = Direction.L;
		else if(bL && bU && !bR && !bD) dir = Direction.LU;
		else if(!bL && bU && !bR && !bD) dir = Direction.U;
		else if(!bL && bU && bR && !bD) dir = Direction.RU;
		else if(!bL && !bU && bR && !bD) dir = Direction.R;
		else if(!bL && !bU && bR && bD) dir = Direction.RD;
		else if(!bL && !bU && !bR && bD) dir = Direction.D;
		else if(bL && !bU && !bR && bD) dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
	}

	public void keyReleased(KeyEvent e) {//键盘抬起...方向布尔值变回false 不影响下一次方向判断
		int key = e.getKeyCode();
		switch(key) {
		   case KeyEvent.VK_F7://启动自动化
			   this.automation=true;
			   break;
		   case KeyEvent.VK_F8://取消自动化
			   this.automation=false;
			   break;
		   case KeyEvent.VK_F2:
			   tc.h.life=10;
			   this.life=10;
			   this.live=true;
			   break;
		   case KeyEvent.VK_S:
			   fire();
			   break;
		   case KeyEvent.VK_LEFT:
			  bL = false;
			  break;
		   case KeyEvent.VK_UP:
			  bU = false;
			  break;
		   case KeyEvent.VK_RIGHT:
			  bR = false;
			  break;
		   case KeyEvent.VK_DOWN:
			  bD= false;
			  break;    
		   case KeyEvent.VK_D:
				  if(good) {
					  fire1();
				  }
				  break;    
		}
		locateDirection();
	}
   
    public Missile fire() {//开火
    	
    	if(!live) return null;
       int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
       int y = this.y + Tank.HEIGHT/2 - Missile.WIDTH/2;
       if(good) {
	    Missile m = new Missile(x,y,good,ptDir,this.tc);
        tc.missiles.add(m);
        return m;
       }
       else {
    	   Missile m = new Missile(x,y,false,ptDir,this.tc);
	       tc.missiles.add(m);
	       return m;
	    }
	   
      }
    public Missile fire(Direction dir) {//根据方向开火
    	
    	if(!live) return null;
       int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
       int y = this.y + Tank.HEIGHT/2 - Missile.WIDTH/2;
       if(good) {
	    Missile m = new Missile(x,y,good,dir,this.tc);
        tc.missiles.add(m);
        return m;
       }
       else {
    	   Missile m = new Missile(x,y,false,dir,this.tc);
	       tc.missiles.add(m);
	       return m;
	    }
	   
      }
    
    public  Missile fire1() {//八个方向的开火
    	Direction[] dir = Direction.values();
    	if(!live) return null;
    	for(int i=0;i<8;i++) {
    		tc.missiles.add(fire(dir[i]));
    	}
		return null;
    }
    private void stay() {//停止就等于回到上一步
    	this.x=oldx;
    	this.y=oldy; 
    }
    public boolean collideWhitWall(Wall w) {
    	if(this.live && this.getRect().intersects(w.getRect())){
    		this.stay();//如果撞到墙就停止
    		return true;
    	}
    	return false;
    }
    public Rectangle getRect() {
    	return new Rectangle(x,y,WIDTH,HEIGHT);
    }
     private class BloodBar
    {//内部血条类
 	   public void draw(Graphics g) 
 	   {
 		   Color c = g.getColor();
 		   g.setColor(Color.RED);
 		   g.drawRect(x, y-10, WIDTH,10);
 		   int w = WIDTH * life/10;//判断血量
 		   g.fillRect(x, y-10, w, 10);
 		   g.setColor(c);
 	   }
    }
}   
