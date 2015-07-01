package lar.nxt.tests;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import lar.nxt.bluetooth.BluetoothSlaveNode;
import lar.nxt.protocols.sbun.SbunCommand;
import lar.nxt.protocols.sbun.SbunCommand.CommandName;
import lar.nxt.protocols.sbun.SbunCommand.SbunTarget2D;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.Sound;
import lejos.nxt.TachoMotorPort;
import lejos.util.Matrix;

public class EchoTest {

  //TASKS FLAGS
	static boolean line_following = true;
	static boolean obstacle_avoidance = false;

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

				SbunCommand command = SbunCommand.parseCommandString(str);

				if(command.name.equals(CommandName.POSES)){

					//Inserisce nell'hashtable tutti i vettori Q
					Hashtable<String , SbunTarget2D> table = (Hashtable<String , SbunTarget2D>)command.payload;

					//Prende i vettori q in base al nome
					SbunTarget2D robot = table.get("/lar_marker_202");
					SbunTarget2D target = table.get("/lar_marker_200");

					if(robot.x < 0){
						line_following = true;
						obstacle_avoidance = false;
					}else{
						line_following = false;
						obstacle_avoidance = true;
					}


				}

			}
		});


		/**
		 * Wait for connection and start
		 */
		LCD.drawString("Waiting...", 0, 0);
		bnode.waitAndStart();
		LCD.clear();


		while(line_following){
			//LINE FOLLOWING
		}

		while(obstacle_avoidance){
			//OBSTACLE AVOIDANCE
		}


		/**
		 * Wait for ESC Button press
		 */
		LCD.drawString("Press exit", 0, 0);
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){

		}


		bnode.forceClose();
	}
}
