package lar.nxt.tests;

import java.util.Hashtable;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.util.Matrix;

public class BHelloWorld {

	public static void main(String[] args) {
		
		
		/**
		 * Bluetooth Node Creation
		 */
		BluetoothSlaveNode bnode = new BluetoothSlaveNode();
		
		
		/**
		 * CREATES INLINE BLUETOOTHNODE LISTENER
		 */
		bnode.addListener(new BluetoothSlaveNode.BluetoothSlaveNodeListener() {
			
			@Override
			public void consumeCommand(String str) {
				LCD.clear();
				LCD.drawString(str, 0, 0);
				
				
				SbunTarget2D robot = new SbunTarget2D();
				SbunTarget2D target = new SbunTarget2D();
				
				robot.angle = 10;
				
				
				
				
				double r = 0.028;
				double L = 0.05;
				double d = 0.115;
				Matrix D = new Matrix(2,2);
				D.set(0, 0, r/2);
				D.set(0, 1, r/2);
				D.set(1, 0, r/d);
				D.set(1, 1, -r/d);
				
				Matrix C = new Matrix(2,2);
				C.set(0,0, Math.cos(robot.angle));
				C.set(0,1, -L*Math.sin(robot.angle));
				C.set(1,0, Math.sin(robot.angle));
				C.set(1,1, L*Math.cos(robot.angle));
				
				Matrix A = C.times(D);
				
				double K = 10;
				double x_dot=-K*(robot.x-target.x);
				double y_dot=-K*(robot.y-target.y);
				
				
				Matrix P_dot = new Matrix(2,1);
				P_dot.set(0, 0, x_dot );
				P_dot.set(1, 0, y_dot );
				
				Matrix omegas = A.inverse().times(P_dot);
				
				
				double VK = 1;
				
				int w_r = (int)(VK*omegas.get(0, 0));
				int w_l = (int)(VK*omegas.get(1, 0));
				
				
				Motor.A.setSpeed(Math.abs(w_l));
				Motor.C.setSpeed(Math.abs(w_r));//(int)(VK*omegas.get(1, 0)));
				
				
				if(w_l>=0){
					Motor.A.forward();
				}else{
					Motor.A.backward();
				}
				
				if(w_r>=0){
					Motor.C.forward();
				}else{
					Motor.C.backward();
				}
				
			}
		});
		
		/**
		 * Wait for connection and start
		 */
		LCD.drawString("Waiting...", 0, 0);
		bnode.waitAndStart();
		LCD.clear();
		
		
		/**
		 * Wait for ESC Button press
		 */
		LCD.drawString("Press exit", 0, 0);
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){
			
		}
		
		bnode.forceClose();
	}

}
