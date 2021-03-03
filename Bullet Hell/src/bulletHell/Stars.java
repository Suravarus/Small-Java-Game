package bulletHell;

import java.awt.Rectangle;
import java.util.Random;

class Stars {
	Random rand = new Random();
	final int width = rand.nextInt(3)+2;
	final int height = width;
	double speed = rand.nextDouble() / 20;
	double movement = 0;
	Rectangle body = new Rectangle(0, 0, width, height);
	public void x(int i) { //sets the x position of the star
		body.x = i;
	}
	public void y(int i) { //sets the y position of the star.
		body.y = i;
	}
	public int[] body () {
		int[] i = new int[] {body.x, body.y,body.height,body.width};
		return i;
	}
	public void move() {
		movement += speed;
		body.y += movement;
		movement = movement % 1;
	}
}
