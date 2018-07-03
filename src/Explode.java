import java.awt.Color;
import java.awt.Graphics;

public class Explode {
   private int x,y,w,h;
   private boolean live =true;
   private TankClient tc;
   
   public Explode(int x,int y,TankClient tc) {
	   this.x=x;
	   this.y=y;
	   this.tc=tc;
   }
   
   int[] dimmeter = {4,7,12,18,28,32,49,30,14,6};
   
   int step=0;
   
   public void draw(Graphics g) {
	   if(!live) {
		   tc.explodes.remove(this);
		   return;
	   }
	   if(step ==dimmeter.length) {
		   this.live=false;
		   step=0;
		   return;
	   }
	   Color c =g.getColor();
	   g.setColor(Color.YELLOW);
	   g.fillOval(x, y, dimmeter[step], dimmeter[step]);
	   g.setColor(c);
	   step++;
   }
   
   
}
