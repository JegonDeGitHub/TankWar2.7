import java.awt.*;
import java.util.List;

public class Missile {
	int x,y;
	
    Tank.Direction dir;
    
    public static final int XSPEED = 12;
    public static final int YSPEED = 12;
    
    public static final int HEIGHT= 10;
	public static final int WIDTH= 10;
	
	private boolean live = true;//炮弹生死
	
	private boolean good = true;//炮弹好坏
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}
	private TankClient tc;
	
   public boolean isLive() {
		return live;
	}

public Missile(int x, int y, Tank.Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

public Missile(int x,int y,boolean good,Tank.Direction dir ,TankClient tc)  {
	 this(x,y,dir);
	 this.tc=tc;
	 this.good=good;
}
   
   public void draw(Graphics g) {
	   Color c = g.getColor();
	   if(!live) return;
	   if(good)
	    g.setColor(Color.BLUE);
	   else
		   g.setColor(Color.RED);
	   g.fillOval(x, y, WIDTH,HEIGHT);
	   g.setColor(c);
	   
	   move();
   }

  private void move() {
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
			if(x<0 || y<0 ||x>TankClient.GAME_WIDTH || y >TankClient.GAME_HEIGHT) {
				live = false;
				tc.missiles.remove(this);//如果超出屏幕就从集合抹除
			}
       }
  
     public boolean hitTank(Tank t) {
    	 if(this.live && this.getRect().intersects(t.getRect()) && t.isLive() && this.good!=t.isGood() ) {
    		 if(t.isGood()) {//如果是己方坦克就扣血 
     		    t.setLife(t.getLife()-2);
     			if(t.getLife()<=0)
     			t.setLive(false);
     		}else {//否则就一炮死
     			t.setLive(false);
     		}
    		 this.live=false;
    		 Explode e = new Explode(x,y,tc);
    		 tc.explodes.add(e);
    		 return true;
    	 }
    	 return false;
     }
  public boolean hitTanks(List<Tank> tanks) {
	  for(int i=0;i<tanks.size();i++) {
	   if(hitTank(tanks.get(i))) {
		  return true;
	   }
	  }
	  return false;
  }
  public boolean hitWall(Wall w) {
  	 if(this.live && this.getRect().intersects(w.getRect())) {
  		 this.live=false;
  		 return true;
  	 }
  	 return false;
  }
  public boolean hitHome(Home h) {//炮弹击中家里
	  if(!this.good && this.live && this.getRect().intersects(h.getRect())) {
		  //如果不是我方炮弹 && 活着 && intersects碰撞检测为true
		  if(this.good) {//如果是我方坦克的炮弹 ，这枚炮弹就死亡
			  this.live=false;
		  }
	  		 h.life-=2;
	  		 this.live=false;
	  		 return true;
	  	 }
	  	 return false;
  }
  public Rectangle getRect() {//体积检测 返回一个长方体的位置宽高
  	return new Rectangle(x,y,WIDTH,HEIGHT);
  }
}
