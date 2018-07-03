import java.awt.*;

import java.awt.event.*;

import java.util.List;//awt�����Ѿ���һ��List������Ҫ��ȷ����unit��List��
import java.util.Random;

/*import Tank.Direction;*/

import java.util.ArrayList;//��Ҫ��ȷ�������ArrayList��
public class TankClient extends Frame {
	
   public static final int GAME_WIDTH = 800;//游戏边框大小
   public static final int GAME_HEIGHT = 600;
	
	Tank myTank = new Tank(300,300,true,Tank.Direction.STOP,this,false);//创建一个主角坦克
	//创建墙
	Wall w1 = new Wall(400,100,20,250),w2= new Wall(300,450,267,20);
	 Wall w3 = new Wall(0,0,2,GAME_HEIGHT);//游戏边框碰撞检测
	 /*Wall w4 = new Wall(GAME_WIDTH,0,5,GAME_HEIGHT);*/
	 Wall w5 = new Wall(0,0,GAME_WIDTH,32);//游戏边框碰撞检测
	 /*Wall w6 = new Wall(0,GAME_HEIGHT,GAME_WIDTH,10);*/
	
	//创建家
	Home h = new Home(400,550,50,50);
	
	
	List<Missile> missiles = new ArrayList<Missile>();//创建炮弹集合
	List<Tank> tanks = new ArrayList<Tank>();//创建敌方坦克集合
	List<Explode> explodes = new ArrayList<Explode>();////创建爆炸画图集合
	
	int tanksNum=10;//敌方坦克总数初始为10
	
	Random r = new Random();//此随机数生成用于随机生成坦克随机位置
			
	Missile m = null;
	
	Image offScreenImage = null;
	
	public void paint(Graphics g) {
		
		if(tanks.size()==0) {
			tanksNum++;//如果死光了敌方坦克总数就加一；
			for(int i= 0 ;i<tanksNum;i++) {
	    		tanks.add(new Tank(r.nextInt(GAME_WIDTH),r.nextInt(GAME_HEIGHT),false,Tank.Direction.D,this,true));
	    	}//随机位置往敌方坦克集合添加敌方坦克对象
		}
		if(h.life==0) {//如果家血量没了
			Font f1 = g.getFont();
			myTank.setLive(false);
			Font f = new Font("宋体",Font.BOLD,30);
			g.setFont(f);
			g.drawString("按F2重新开始", 400, 300);
			g.setFont(f1);
		}
		Font f1 = g.getFont();
		Font f = new Font("宋体",Font.BOLD,15);
		g.setFont(f);
		g.drawString("'S' is Fire ", 10, 60);
		g.drawString("'D' is superFire", 10, 80);
		g.drawString("按'F7'自动化", 10, 120);
		g.drawString("按'F8'取消自动化", 10, 140);
		g.drawString("死后按'F2'重新开始", 10, 160);
		
		/*g.drawString("missile count:"+ missiles.size(), 10, 60);
		g.drawString("ecplode count:"+ explodes.size(), 10, 70);*/
		g.drawString("Ememy count:"+ tanks.size(), 10, 100);
		g.setFont(f1);
		for(int i=0;i<missiles.size();i++) {//循环判断炮弹集合里的炮弹情况
			Missile m = missiles.get(i);
			m.hitTanks(tanks);//先判断炮弹与敌方坦克的情况
			m.hitTank(myTank);//判断我方坦克情况
			if(myTank.getLife()<=0) {//如果我方坦克血量归零
				Font f2 = new Font("宋体",Font.BOLD,30);
				g.setFont(f);
				g.drawString("按F2重新开始", 250, 300);
			}
			m.hitWall(w1);
			m.hitWall(w2);
			m.hitHome(h);
			m.draw(g);
			//if(!m.isLive()) missiles.remove(m);
			//else m.draw(g);
		}
		for(int i=0;i<tanks.size();i++) {//循环生成坦克
			Tank t = tanks.get(i);
			t.collideWhitWall(w1);//与墙体作碰撞检测
			t.collideWhitWall(w2);
		    t.collideWhitWall(w3);
		    t.collideWhitWall(w5);
			t.draw(g);
		}
		for(int i=0;i<explodes.size();i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
			
		myTank.draw(g);
		w1.draw(g);
		w2.draw(g);
		w3.draw(g);
	/*	w4.draw(g);*/
		w5.draw(g);
		/*w6.draw(g);*/
		h.draw(g);
	}
/*	
  public void paint(Graphics g) {
	    if(m!=null)m.draw(g);
		myTank.draw(g);
	}*/

  public void update(Graphics g) {//双缓冲：先画到虚拟画面上再画到真正的图上 解决闪烁问题
		if(offScreenImage == null) {
			offScreenImage=this.createImage(GAME_WIDTH,GAME_HEIGHT);
		}
		
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GRAY);
		gOffScreen.fillRect(0, 0, GAME_WIDTH,GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

    public void lanuchFrame() {//设置窗口、与窗口里各类对象的参数
    	for(int i= 0 ;i<tanksNum;i++) {
    		tanks.add(new Tank(r.nextInt(GAME_WIDTH),r.nextInt(GAME_HEIGHT),false,Tank.Direction.D,this,true));
    		
    	}//循环往敌方坦克集合添加随机位置的坦克
	  this.setLocation(400,300);
	  this.setSize(GAME_WIDTH,GAME_HEIGHT);
	  this.setTitle("TankWar");
	  this.addWindowListener(new WindowAdapter() {

		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
		 
	  });
	  this.setResizable(false);
	  this.setBackground(Color.GRAY);
	  
	  this.addKeyListener(new KeyMonitor());
	  
	  setVisible(true);
	 new Thread(new PaintThread()).start();//new 画面重画线程
  }
	
public static void main(String[] args) {
	  TankClient tc = new TankClient();
	  tc.lanuchFrame();
    }

private class PaintThread implements Runnable{
	  public void run() {
		   while(true) {
			   repaint();
			   try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		               }
	                     }
   }
private class KeyMonitor extends KeyAdapter{//键盘监听类
	
	public void keyPressed(KeyEvent e) {
		myTank.keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e) {
		myTank.keyReleased(e);
	}
   }
 /* public Rectangle getRect() {//碰撞检测 
	   return new Rectangle(400,340,GAME_WIDTH,40);
  } 
   public Rectangle getRect1() {//碰撞检测 
	   return new Rectangle(400,300,5,GAME_HEIGHT);
  } 
  public Rectangle getRect2() {//碰撞检测 
	   return new Rectangle(400+GAME_WIDTH,300,5,GAME_HEIGHT);
  } 
  public Rectangle getRect3() {//碰撞检测 
	   return new Rectangle(400,300+GAME_HEIGHT,GAME_WIDTH,15);
  } */
}
