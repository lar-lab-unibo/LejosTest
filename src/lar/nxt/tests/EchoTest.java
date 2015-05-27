package lar.nxt.tests;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.Sound;
import lejos.nxt.TachoMotorPort;

public class EchoTest {

	public static void main(String[] args) throws Exception {
		
		/**
		 * Bluetooth Node Creation
		 */
		BluetoothSlaveNode bnode = new BluetoothSlaveNode();
		
		NXTMotor m_left = new NXTMotor(MotorPort.A);
		NXTMotor m_right = new NXTMotor(MotorPort.C);
		
		/**
		 * CREATES INLINE BLUETOOTHNODE LISTENER
		 */
		bnode.addListener(new BluetoothSlaveNode.BluetoothSlaveNodeListener() {
			
			@Override
			public void consumeCommand(String str) {
				System.out.println(str);
				SbunCommand command = SbunCommand.parseCommandString(str);
				if(command.name.equals(CommandName.BUZZ)){
					Sound.buzz();
					LCD.clear();
				}
//				if(command.name.equals(CommandName.POSES)){
//					Graphics g = new Graphics();
//					
//					Hashtable<String , SbunTarget2D> table = (Hashtable<String , SbunTarget2D>)command.payload;
//					
//					SbunTarget2D ta = table.get("P_A");
//					SbunTarget2D tb = table.get("P_B");
//					
//					System.out.println(ta.angle);
//					
//					//m.setPower((int)ta.angle);
//					m_left.setPower(-(int)ta.angle);
//					m_right.setPower(-(int)tb.angle);
//					
//				}
				if(command.name.equals(CommandName.MOVE)){
					float[] vel = (float[])command.payload;
					m_left.setPower((int)vel[0]);
					m_right.setPower((int)vel[1]);
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
