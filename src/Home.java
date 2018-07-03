import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Home {
   int x,y,w,h;
   int life=10;
   BloodBar hb = new BloodBar();
   public Home(int x,int y,int w,int h) {
	   this.x=x;
     	this.y=y;
    	this.w=w;
    	this.h=h;
   }
   public void draw(Graphics g) {
	   Font f =g.getFont();
	   Font f1 = new Font("宋体",Font.BOLD,20);
	   g.setFont(f1);
   	   g.drawRect(x, y, w, h);
   	   g.drawString("Home", x, y+30);
   	   hb.draw(g);
   	   g.setFont(f);
   }
   public Rectangle getRect() {
   	return new Rectangle(x,y,w,h);
   }
   private class BloodBar
   {//内部血条类
	   public void draw(Graphics g) 
	   {
		   Color c = g.getColor();
		   g.setColor(Color.RED);
		   g.drawRect(x, y-10, w,10);
		   int w1 = w * life/10;//判断血量
		   g.fillRect(x, y-10, w1, 10);
		   g.setColor(c);
	   }
   }
}
