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
				
				SbunCommand command = SbunCommand.parseCommandString(str);
				if(command.name.equals(CommandName.BUZZ)){
					Sound.buzz();
					LCD.clear();
				}
				if(command.name.equals(CommandName.POSES)){
					
					//Inserisce nell'hashtable tutti i vettori Q
					Hashtable<String , SbunTarget2D> table = (Hashtable<String , SbunTarget2D>)command.payload;
					
					//Prende i vettori q in base al nome
					SbunTarget2D robot = table.get("/lar_marker_202");
					SbunTarget2D target = table.get("/lar_marker_200");
					
					
					double target_angle = target.angle * 180 / Math.PI;
					double robot_angle = robot.angle * 180 / Math.PI;
					if (robot_angle<0) robot_angle=360+robot_angle;
					if (target_angle<0) target_angle=360+target_angle;
					
					//target_angle = target_angle % 360;
					//robot_angle = robot_angle % 360;
					double delta_angle = robot_angle-target_angle;
					double dd = delta_angle;// Math.sin(delta_angle);
					float k =0.8f;
					
					int l_power = (int)(dd*k);
					int r_power = -(int)(dd*k);
					
					LCD.clear();
					LCD.drawString("q_goal:"+target_angle, 0, 0);
					LCD.drawString("q_robot:"+robot_angle, 0, 1);
					LCD.drawString("diff:"+delta_angle, 0, 3);
					
					
					
					m_left.setPower(l_power);
					m_right.setPower(r_power);
					//System.out.println(str);
					
					
					//m.setPower((int)ta.angle);
					
					
				}
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
