import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import seltar.motion.Motion;

public class Main extends PApplet {
	int USER = 2;
	int mode = 0;

	Motion positions[] = new Motion[3];
	int isActive[] = { -1, -1, -1 };
	String activeIcons[] = { "", "", "" };
	String activeIconsTypes[] = { "pdf", "pdf", "pdf" };

	PFont bigfont;
	PFont smallfont;

	String iconNames[] = { "pdf", "mail", "app", "doc", "flash", "help", "jpg",
			"link", "mov", "mp3", "ppt", "time", "txt", "xls" };
	PImage avatar = loadImage("avatars/chloe6.jpg");

	HashMap<String, PImage> icons = new HashMap<String, PImage>();

	public static void main(String args[]) {
		// PApplet.main(new String[] { "--present", "Main" });
		PApplet.main(new String[] { "Main" });
	}

	public void setup() {
		for (int i = 0; i < iconNames.length; i++)
			icons
					.put(iconNames[i], loadImage("icons/" + iconNames[i]
							+ ".png"));

		bigfont = createFont("Arial", 65);
		smallfont = createFont("Arial", 15);
		textFont(bigfont);
		textAlign(CENTER);

		positions[0] = new Motion(0, 1500);
		positions[1] = new Motion(500, 1500);
		positions[2] = new Motion(1000, 1500);
		positions[0].setConstant(25);
		positions[1].setConstant(25);
		positions[2].setConstant(25);

		size(800, 600);
		frameRate(100);

		Thread thread1 = new Thread(new RunnableThread(), "thread1");
		thread1.start();
	}

	public void draw() {
		background(0);

		if (mode == 1) // avatar mode
			image(avatar, width / 2 - avatar.width / 2, 10);
		else // files mode
		{
			scale(.5f, .5f);
			for (int i = 0; i < 3; i++) {
				if (isActive[i] > 0) {
					positions[i].followTo(positions[i].getX(), 100);
					positions[i].move();
				} else {
					positions[i].move();
					positions[i].followTo(positions[i].getX(), 1500);
				}

				pushMatrix();
				translate(positions[i].getX(), positions[i].getY());
				image(icons.get(activeIconsTypes[i]), 0, 0);

				text(activeIcons[i], 256, 600);
				popMatrix();
			}
		}

	}

	class RunnableThread implements Runnable {

		Thread runner;

		public RunnableThread() {
		}

		public RunnableThread(String threadName) {
			runner = new Thread(this, threadName); // (1) Create a new thread.
			System.out.println(runner.getName());
			runner.start(); // (2) Start the thread.
		}

		public void run() {

			while (true) {
				String lines[] = loadStrings("http://cmu.chrisharrison.net/cgi-bin/getMode.py?owner="
						+ USER);

				mode = Integer.parseInt(lines[0]); // 0 = files, 1 = avatar

				lines = loadStrings("http://cmu.chrisharrison.net/cgi-bin/getAllFiles.py?owner="
						+ USER);
				// println(lines);

				for (int i = 0; i < 3; i++)
					isActive[i] = -1;

				for (int i = 0; i < lines.length; i++) {
					String tokens[] = lines[i].split(",");
					int loc = Integer.parseInt(tokens[5].trim()) - 1;

					if (Integer.parseInt(tokens[5].trim()) > 0) {
						activeIcons[loc] = tokens[1].trim();
						activeIconsTypes[loc] = tokens[2].trim();
						isActive[loc] = 1;
					}
				}

				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
