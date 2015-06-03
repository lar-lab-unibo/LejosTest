package lar.nxt.tests;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lar.nxt.unicycle.Donkey;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.Sound;
import lejos.nxt.TachoMotorPort;
import lejos.util.Matrix;

public class EchoTest {

	public static void main(String[] args) throws Exception {
		
		/**
		 * Bluetooth Node Creation
		 */
		BluetoothSlaveNode bnode = new BluetoothSlaveNode();
		
		NXTMotor m_left = new NXTMotor(MotorPort.A);
		NXTMotor m_right = new NXTMotor(MotorPort.C);
		
		Donkey donkey = new Donkey(Motor.A, Motor.C, 0.028, 0.115, 0.05);
		
		 
		/**
		 * CREATES INLINE BLUETOOTHNODE LISTENER
		 */
		bnode.addListener(new BluetoothSlaveNode.BluetoothSlaveNodeListener() {
			
			@Override
			public void consumeCommand(String str) {
				
				SbunCommand command = SbunCommand.parseCommandString(str);
				if(command.name.equals(CommandName.BUZZ)){
					Sound.buzz();
					LCD.clear();
				}
				if(command.name.equals(CommandName.POSES)){
					
					//LCD.clear();
					
					//Inserisce nell'hashtable tutti i vettori Q
					Hashtable<String , SbunTarget2D> table = (Hashtable<String , SbunTarget2D>)command.payload;
					
					//Prende i vettori q in base al nome
					SbunTarget2D robot = table.get("/lar_marker_202");
					SbunTarget2D target = table.get("/lar_marker_200");
					
					SbunTarget2D time = table.get("time");
					LCD.drawString("theta:"+robot.angle*180/Math.PI, 0, 5);
					
					donkey.setGoal(target.x, target.y, target.angle);
					donkey.updateOdometry(robot.x, robot.y, robot.angle);
					
					
					
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
		donkey.mainThread.join();
	}
}
