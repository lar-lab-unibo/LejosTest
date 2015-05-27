package lar.nxt.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class BluetoothSlaveNode implements Runnable {

	/**
	 * Connection Handle
	 */
	private NXTConnection connection;

	/**
	 * Input Stream Handle
	 */
	private InputStream input;

	/**
	 * Stream Internal Buffer
	 */
	private StringBuilder buffer;

	/**
	 * Alive Flag
	 */
	private boolean alive = false;

	/**
	 * Bluetooth Slave DELIMITER
	 */
	private static final byte DELIMITER = (byte) '#';

	
	/**
	 * Listeners List.
	 */
	private ArrayList<BluetoothSlaveNodeListener> listeners = new ArrayList<BluetoothSlaveNodeListener>();
	
	/**
	 * Constructor: Costruisce un BluetoothSlaveNode e lancia in automatico un Thread che lo tiene in vita
	 */
	public BluetoothSlaveNode() {
		buffer = new StringBuilder();
		alive = true;
	}
	
	/**
	 * Waits for connection and start
	 */
	public void waitAndStart(){
		connection = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		connection.setIOMode(lejos.nxt.comm.NXTConnection.RAW);
		input = connection.openInputStream();
		
		/**
		 * Start Thread
		 */
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Add target listener to Listeners List
	 * @param listener target Listener
	 */
	public void addListener(BluetoothSlaveNodeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Propagates Received Command String to all Listeners
	 * @param command Command String
	 */
	public void propagateCommandString(String command){
		for(BluetoothSlaveNodeListener listener: listeners){
			listener.consumeCommand(command);
		}
	}
	
	/**
	 * Tries to close Connection
	 */
	public void forceClose(){
		alive = false;
		try {
			input.close();
			connection.close();
		} catch (IOException e) {
			System.out.println("FCLS!"+e.getClass().toString());
		}
	}
	
	@Override
	public void run() {

		byte[] buf = new byte[1];

		while (alive) {
			try {
				input.read(buf);

				if (buf[0] != (int) DELIMITER) {
					buffer.append(new String(buf));
				} else {
					String command = buffer.toString();
					propagateCommandString(command);
					buffer.setLength(0);
					Thread.sleep(10);
				}

			} catch (Exception e) {
				System.out.println("RX:"+e.getClass().toString());
			}

		}
		
		/**
		 * Close connections
		 */
		try {
			input.close();
			connection.close();
		} catch (IOException e) {
			System.out.println("CLS:"+e.getClass().toString());
		}
		
	}
	
	/**
	 * Classe Listener da utilizzare per ascoltare i comandi ricevuti tramite il canale 
	 * Bluetooth
	 * 
	 */
	public static interface BluetoothSlaveNodeListener{
			public void consumeCommand(String str);
	}

}
