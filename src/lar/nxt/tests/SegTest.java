package lar.nxt.tests;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lar.nxt.segway.Sbunnoway;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GyroSensor;
import lejos.robotics.EncoderMotor;
import lejos.robotics.navigation.Segoway;

public class SegTest {

	public static Sbunnoway sbunno = null;
	public static int speed_left = 0;
	public static int speed_right = 0;
	
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
				SbunCommand command = SbunCommand.parseCommandString(str);
				if(command.name.equals(CommandName.BUZZ)){
					Sound.buzz();
					LCD.clear();
				}
				if(command.name.equals(CommandName.POSES)){
					Graphics g = new Graphics();
					
					Hashtable<String , SbunTarget2D> table = (Hashtable<String , SbunTarget2D>)command.payload;
					
					SbunTarget2D ta = table.get("P_A");
					SbunTarget2D tb = table.get("P_B");
					
					speed_left = (int)ta.angle;
					speed_right = (int)tb.angle;
					if(sbunno!=null)
					sbunno.wheelDriver((int)ta.angle, (int)tb.angle);
					
				}
				
			}
		});
		
		Thread controlThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(sbunno!=null){
					sbunno.wheelDriver(speed_left, speed_right);
					
				}
				
			}
		});
		controlThread.start();
		
		/**
		 * Wait for connection and start
		 */
		LCD.drawString("Waiting...", 0, 0);
		bnode.waitAndStart();
		LCD.clear();
		
		
		
		/**
		 * STart Segway
		 */
		EncoderMotor left = new NXTMotor(MotorPort.C);
		EncoderMotor right = new NXTMotor(MotorPort.A);
		GyroSensor gyro = new GyroSensor(SensorPort.S2);
		sbunno = new Sbunnoway(left, right, gyro, 5.6);
		
		
		/**
		 * Wait for ESC Button press
		 */
		LCD.drawString("Press exit", 0, 0);
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){
			
		}
		
		
		bnode.forceClose();
		
		
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){
			
		}
		
		try {
			sbunno.join();
			controlThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
