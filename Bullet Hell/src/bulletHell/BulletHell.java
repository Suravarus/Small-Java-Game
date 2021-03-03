package bulletHell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;


public class BulletHell implements ActionListener, MouseListener, KeyListener {
	
	public static BulletHell bulletHell;
	public final int HEIGHT = 1000, WIDTH = 1800; //dimensions of the game
	public Renderer renderer = new Renderer();
	public Rectangle ship; //the Ship
	public Rectangle power; //the powerup when it spawns
	
	public boolean gameOver, roundStarting, started, paused, restartable = true, newHighScore, shooting, powerUp; //boolean variables
	public int ticks = 0, round = 1, highestRound, enemyAmount, enemyLeft, wave, playerX1, playerX2, playerY1, playerY2, cooldown = 1000, score, highScore, shots = 1, enhances = 0, delay = 0; //int variables
	public double enemySpeed, heliMotion, bulletMotion, playerSlow, playerFast, yMotion, xMotion;
	public int[] bulletsDelete, helisDelete;
	
	public ArrayList<Stars> stars; //stars in the background
	public ArrayList<Rectangle> helis; //enemies
	public ArrayList<Rectangle> bullets; //player bullets
	public Random rand = new Random();
	
	public BulletHell() {
		playerFast = 8;
		playerSlow = (Math.sqrt(2 * (playerFast * playerFast)) / 2); //calculates correct slow speed based off fast speed
		
		helisDelete = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0};
		bulletsDelete = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0};
		
		JFrame jframe = new JFrame();
		Timer timer = new Timer(10, this);
		
		jframe.add(renderer);
		jframe.setTitle("Bullet Hell"); //window name
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//close program when window closes
		jframe.setSize(WIDTH, HEIGHT);//size of the window
		jframe.addMouseListener(this);//mouse listener
		jframe.addKeyListener(this);//key listener
		jframe.setResizable(false);//makes window unresizable
		jframe.setVisible(true);//makes visible
		
		stars = new ArrayList<Stars>(); //stars in the background
		helis = new ArrayList<Rectangle>(); //array of helis
		bullets = new ArrayList<Rectangle>(); //array of bullets
		
		addShip();
		addStars();
		timer.start();
	}
	
	public void start() {
		if (gameOver) {
			round = 1;
			score = 0;
			newHighScore = false;
			enhances = 0;
			delay = 0;
			shots = 1;
		}
		restartable = false;
		gameOver = false;
		started = true;
		roundStarting = true;
		ticks = 0;
	}
	
	public void shoot() {
		if(shots % 2 == 1) {//1, 3, 5 shots
			bullets.add(new Rectangle(ship.x + (2 * ((ship.width - 3) / 4)), ship.y - 1, 4, 10)); //center
			if (shots % 3 == 0 || shots % 5 == 0) {//3, 5 shots 
				bullets.add(new Rectangle(ship.x + (((ship.width - 3) / 4)), ship.y - 1, 4, 10)); //center left
				bullets.add(new Rectangle(ship.x + (3 * ((ship.width - 3) / 4)), ship.y - 1, 4, 10)); //center right
				if  (shots % 5 == 0) { //5 shots
					bullets.add(new Rectangle(ship.x, ship.y - 1, 4, 10)); //left
					bullets.add(new Rectangle(ship.x + ship.width - 4, ship.y - 1, 4, 10)); //right
				}
			}
		}
		else if (shots % 4 == 0) {//4 shots
			bullets.add(new Rectangle(ship.x + ((ship.width) / 5) - 1, ship.y - 1, 4, 10)); //left
			bullets.add(new Rectangle(ship.x + (2 * ((ship.width) / 5)) - 1, ship.y - 1, 4, 10)); //left center
			bullets.add(new Rectangle(ship.x + (3 * ((ship.width) / 5)) - 1, ship.y - 1, 4, 10)); //right center
			bullets.add(new Rectangle(ship.x + (4 * ((ship.width) / 5)) - 1, ship.y - 1, 4, 10)); //right
		}
		else {//2 shots
			bullets.add(new Rectangle(ship.x + (((ship.width - 2) / 3)), ship.y - 1, 4, 10)); //left
			bullets.add(new Rectangle(ship.x + (2 * ((ship.width - 3) / 3)), ship.y - 1, 4, 10)); //right
		}
		cooldown = 0;
	}
	
	public void addStars() {
		Stars star = new Stars();
		star.x(rand.nextInt(WIDTH));
		star.y(rand.nextInt(7));
		stars.add(star);
		for (int i = 1; i < 500; i++) {
			stars.add(new Stars());
			stars.get(i).x(rand.nextInt(WIDTH));
			stars.get(i).y(rand.nextInt(7) + stars.get(i-1).body.y);
		}
	}
	
	public void addPowerUp() {
		power = new Rectangle(rand.nextInt(WIDTH - 25), 50, 25, 25);
	}
	
	public void addShip() {
		ship = new Rectangle(WIDTH / 2 - 15, HEIGHT / 2 - 15, 32, 32); //the ship
	}
	
	public void addHeli (boolean initial){
		int width = 35;
		int height = 80;
		int waveSize; //how many helis will spawn this wave
		int heliScan = 0;
		int y;
			if (enemyLeft > 0 && (helis.size() <= 80)) {
				if (enemyLeft < 5) {
					if (enemyAmount >= 50) 
						waveSize = 7;
					else 
						waveSize = 4;
				}
				else if(enemyLeft >= enemyAmount / 5) 
					waveSize = enemyAmount / 5;
				else if (10 < enemyAmount / 5){
					waveSize = 10;
				}
				else
					waveSize = 0;
			}
			else
				waveSize = 0;
			if (10 < enemyAmount / 5) 
				waveSize = 10;
			else 
				waveSize = enemyAmount / 5;
		if(initial) {
			y = -10;
			helis.add(new Rectangle(rand.nextInt(WIDTH - width), y, width, height)); //spawns heli in random x position high up
			enemyLeft--;
			heliScan = helis.size() - 1;
			wave++;
		}
		else {
			y = helis.get(helis.size()-1).y - 240;
			helis.add(new Rectangle(rand.nextInt(WIDTH - width), y, width, height)); //spawns heli in random x position high up
			enemyLeft--;
			heliScan = helis.size() - 1;
			wave++;
		}
		for (int i = 1; i < waveSize; i++) {
			helis.add(new Rectangle(rand.nextInt(WIDTH - width), y, width, height)); //spawns heli in random x position high up
			for (int j = 0; j < i; j++) {
				if(Math.abs(helis.get(helis.size()-1).x - helis.get(heliScan + j).x) < 36) {//if one heli spawns in another, respawn it. Essentially intersection is being calculated here.
					helis.remove(helis.get(helis.size()-1));
					i--;
					enemyLeft++;
					break;
				}
			}
			enemyLeft--;
		}
		if (enemyLeft < enemyAmount / 2 && !powerUp) {
			if (rand.nextDouble() <= 1.) { //spawn chance
				powerUp = true;
				addPowerUp();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ticks++;
		cooldown++;
		if(started && !paused) {
			if (roundStarting && ticks >= 40) {
				enemySpeed = ((double)round * .20) + 5;
				if (enemySpeed > 15)
					enemySpeed = 15;
				enemyAmount = (int) ((double)round * 5);
				if (enemyAmount > 30)
					enemyAmount = 30;
				 //adds correct number of helis
				enemyLeft = enemyAmount;
				heliMotion = 0;
				addHeli(true);
				wave = 1;
				roundStarting = false;
			}
			
			if (powerUp) {
				power.y += playerSlow/2;
				if (power.intersects(ship)) {
					enhances++;
					if (enhances <= 14) {
						if ((rand.nextDouble() < .25 && shots < 5) || delay >= 40) {
							shots++;
						}
						else {
							delay += 4;
						}
					}
					else {
						score += 5;
					}
					power = null;
					powerUp = false;
				}
				else if (power.y > HEIGHT) {
					power = null;
					powerUp = false;
				}
			}
			
			
			heliMotion += enemySpeed;
			for (Rectangle heli : helis) {
				heli.y += (int) heliMotion;
				if(ship.intersects(heli)) { //heli killing player
					enemySpeed = 0.0;
					gameOver = true;
					playerX1 = 0;
					playerX2 = 0;
					playerY1 = 0;
					playerY2 = 0;
					ticks = 0;
					if (round > highestRound)
						highestRound = round;
					if (score > highScore) {
						highScore = score;
						newHighScore = true;
					}
				}
			}
			
			heliMotion = heliMotion % 1;

			for(int i = bullets.size() - 1; i >= 0; i--) { //looking at all bullets
				Rectangle bullet = bullets.get(i);
				if(bullet.y + bullet.height < 100)  //removes old bullets
					bullets.remove(bullet);
				}
			
			for(int i = helis.size() - 1; i >= 0; i--) { //looking at all helis
				Rectangle heli = helis.get(i);
				if(heli.y + heli.height > HEIGHT + heli.height)  //removes old helis
					helis.remove(heli);
				}
		
			if (enemyLeft > 0)
				addHeli(false);
			
			if (helis.size() == 0 && !gameOver && !roundStarting && power == null) {
				round++;
				started = false;
				ticks = 0;
				restartable = true;
			}
		}
		
		if((!gameOver || restartable) && !paused) { //only can move after collision detection checks
			
			collision(); //includes bullet movement, and bullet collision with helis.
			move(); //movement
			
			if (shooting && cooldown >= (50 - delay))
				shoot();
			
			if (ship.x < 0) //wall collision
				ship.x = 0;
			if (ship.x > WIDTH - 32)
				ship.x = WIDTH - 32;
			if (ship.y < 100)
				ship.y = 100;
			if (ship.y > HEIGHT - 55)
				ship.y = HEIGHT - 55;
		}
		
		if (gameOver && started) { //when you get a gameOver, reset ticks and mark that the game is not started.
			ticks = 0;
			started = false;
		}
		
		if (gameOver && ticks > 20 && !restartable) { //delay to be able to restart after loss + cleanup
			clear(helis);
			clear(bullets);
			power = null;
			addShip();
			restartable = true;
			powerUp = false;
		}
		
		if (!paused) {
			for(int i = stars.size() - 1; i >= 0; i--) { //looking at all stars
				Stars star = stars.get(i);
				star.move();
				if(star.body.y + star.body.height > HEIGHT + star.body.height)  //removes old stars
					star.body.y -= HEIGHT;
					star.speed = rand.nextDouble() / 20;
				}
		}
		
		if (ticks > 2147000004) //lowers tick count to prevent overflow
			ticks = 5004;
		
		if (cooldown > 2147000000) //lowers cooldown to prevent overflow
			cooldown = 5000;
		
		renderer.repaint();
	}
	
	public void paintStars(Graphics g, Stars star) {
		int[] A = star.body();
		g.setColor(Color.yellow);
		g.fillRect(A[0], A[1], A[2], A[3]);
	}

	public void paintHelis(Graphics g, Rectangle heli) { //helis (enemies) coloring
		g.setColor(Color.darkGray);
		g.fillRect(heli.x, heli.y, heli.width, heli.height);
	}
	
	public void paintBullets(Graphics g, Rectangle bullet) { //paints bullets shot by the player
		g.setColor(Color.white);
		g.fillRect(bullet.x,  bullet.y,  bullet.width, bullet.height);
	}
	
	public void repaint(Graphics g) {
		
		g.setColor(Color.black); //field
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		for (Stars star: stars)
			paintStars(g, star);
		
		if (powerUp) {
			g.setColor(Color.red);
			g.fillRect(power.x, power.y, power.width, power.height);
		}
		
		for (Rectangle bullet: bullets)
			paintBullets(g, bullet);
		
		if (started || !restartable) //painting helis
			for (Rectangle heli: helis)
				paintHelis(g, heli);
		
		g.setColor(Color.cyan); //UI background
		g.fillRect(0, 0, WIDTH, 100);
		
		g.setColor(Color.darkGray); //color the ship
		g.fillRect(ship.x, ship.y, ship.width, ship.height);
		
		g.setColor(Color.gray);
		g.setFont(new Font("Ariel", 1, 50 )); 
		
		g.drawString("Round: " + String.valueOf(round) + " " + ticks, 10, 100); //display score
		if (restartable && !gameOver && !started) {
			g.drawString("Press space to start!", WIDTH/4, HEIGHT /2 - 50);
		}
		
		if (gameOver) {
			g.drawString("Game over!", WIDTH/4, HEIGHT /2 - 150);
			g.drawString("Press space to restart!", WIDTH/4, HEIGHT /2 - 50);
			g.drawString("Score: " + score, 750, 138);
			if (highestRound > 1) {
				g.setColor(Color.red.darker());
				g.drawString("Highest round: " + highestRound, 1300, 100);
			}
			if (highScore > 0) {
				g.setColor(Color.blue.darker().darker());
				g.drawString("High score: " + highScore, 700, 70);
				if (newHighScore) {
					g.setColor(Color.magenta);
					g.drawString("New high score!!!", 675, 600);
				}
			}
		}
		if (paused) {
			g.drawString("Paused...", 750, 70);
			g.drawString("Score: " + score, 750, 138);
		}
	}
	
	public static void main(String[] args) {
		bulletHell = new BulletHell();
	}
	
	public void clear(ArrayList<Rectangle> clears) {
		for(int i = clears.size() - 1; i >= 0; i--) {  //looking at all rectangles in the arraylist
			Rectangle clear = clears.get(i);
			clears.remove(clear);
		}
	}
	
	public void collision() {
		bulletMotion += 2 * playerFast; //bullet movement is here
		for (Rectangle bullet : bullets) {
			bullet.y -= (int) bulletMotion;
		}
		bulletMotion = bulletMotion % 1;
		
		for(int i = bullets.size() - 1; i >= 0; i--) {  //looking at all rectangles in the arraylist
			Rectangle bullet = bullets.get(i);
			if(helis.size() != 0) {
				for(int j = helis.size() - 1; j >= 0; j--) {
					Rectangle heli = helis.get(j);
					if (bullet.intersects(heli)) {
						bulletsDelete[bulletsDelete[10]] = i;
						helisDelete[bulletsDelete[10]] = j;
						bulletsDelete[10]++;
						helisDelete[10]++;
						break;
					}
				}
			}
		}
		for (int i = helisDelete[10] - 1; i >= 0; i--) { //deleting helis and bullets which intersect
			if (helisDelete[i+1] != helisDelete[i]) {
				Rectangle heli = helis.get(helisDelete[i]);
				helis.remove(heli);
				score++;
			}
		}
		for (int i = 0; bulletsDelete[10] > i; i++) {
			if ((bulletsDelete[i+1] != bulletsDelete[i] || i == bulletsDelete[10] - 1) && bulletsDelete[i] != -1) {
				if(helisDelete[i] != helisDelete[i+1]) {
					Rectangle bullet = bullets.get(bulletsDelete[i]);
					bullets.remove(bullet);
				}
			}
		}//2 bullets hitting different helis on the same frame is sometimes an issue.
		
		if (helisDelete[10] != 0) {
			for (int i = 0; i < 10; i++) {
				bulletsDelete[i] = -1;
				helisDelete[i] = -1;
			}
			bulletsDelete[10] = 0;
			helisDelete[10] = 0;
		}

	}
	
	public void move() { //moving the player
		if ((playerX1 != 0 || playerX2 != 0) && (playerY1 != 0 || playerY2 != 0)) { //add slow speed if slow
			xMotion = xMotion + (playerSlow * playerX1);
			xMotion = xMotion + (playerSlow * playerX2);
			yMotion = yMotion + (playerSlow * playerY1);
			yMotion = yMotion + (playerSlow * playerY2);
		}
		else { //add fast speed if fast. one or variables will = 0, so only one direction of movement max.
			xMotion = xMotion + (playerFast * playerX1);
			xMotion = xMotion + (playerFast * playerX2);
			yMotion = yMotion + (playerFast * playerY1);
			yMotion = yMotion + (playerFast * playerY2);
		}
		
		ship.x += (int)xMotion; //moves ship
		ship.y += (int)yMotion;
		
		xMotion = xMotion % 1; //subtracts how much they've moved.
		yMotion = yMotion % 1;
	}
	
	public void slow(char direction) {
		if (direction == 'w') {
			if (playerY1 == -1)
				playerY1 = 0;
		}
		else if (direction == 'a') {
			if (playerX1 == -1)
				playerX1 = 0;
		}
		else if (direction == 's') {
			if (playerY2 == 1)
				playerY2 = 0;
		}
		else if (direction == 'd') {
			if (playerX2 == 1)
				playerX2 = 0;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) { //arrow keys or wasd to move
		char direction = e.getKeyChar();
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			direction = 'w';
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			direction = 's';
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			direction = 'a';
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			direction = 'd';
		}
		switch (direction) {
		case 'w': playerY1 = -1;
			break;
		case 'a': playerX1 = -1;
			break;
		case 'd': playerX2 = 1;
			break;
		case 's': playerY2 = 1;
		}
		if (direction == ' ') 
			if (restartable) 
				start();
		if (e.getKeyCode() == 27 && !gameOver) //escape pauses
			paused = !paused;
	}

	@Override
	public void keyReleased(KeyEvent e) { //stops motion when key is released and going in that direction.
		char direction = e.getKeyChar();
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			direction = 'w';
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			direction = 's';
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			direction = 'a';
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			direction = 'd';
		}
		slow(direction);

	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		if ((!gameOver || restartable) && cooldown > 0 && !paused) {
			shoot();
			shooting = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		shooting = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

}