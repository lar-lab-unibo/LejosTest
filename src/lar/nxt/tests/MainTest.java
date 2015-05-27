package lar.nxt.tests;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class MainTest {

	public static void main(String[] args) throws Exception {
		
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
					SbunTarget2D tc = table.get("P_C");
					
					g.fillRect((int)ta.x,(int)ta.y,5,10);
					g.fillRect((int)tb.x,(int)tb.y,8,10);
					g.fillRect((int)tc.x,(int)tc.y,2,10);
					
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
