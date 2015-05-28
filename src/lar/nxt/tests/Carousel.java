package lar.nxt.tests;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class Carousel {

	public static void main(String[] args) throws Exception {

		float speed = 5.0f;
		LCD.drawString("Carousel!", 0, 0);

		int b = Button.waitForAnyEvent(100);
		while (b != Button.ID_ESCAPE) {

			if(b==Button.ID_RIGHT){
				speed+=5.0f;
			}
			if(b==Button.ID_LEFT){
				speed-=5.0f;
			}
			
			Motor.A.setSpeed(speed);
			if(speed>0){
				Motor.A.forward();	
			}else{
				Motor.A.backward();
			}
			
			b = Button.waitForAnyEvent(100);
		}
	}
}
