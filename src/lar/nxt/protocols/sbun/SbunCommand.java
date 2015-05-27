package lar.nxt.protocols.sbun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import lar.nxt.utils.StringUtilities;

/**
 * String Buffer (for) Ugly Networks
 * 
 * Command Pattern:
 * 
 * [COMMAND_NAME!DATA]
 * 
 * @author Daniele
 *
 */
public class SbunCommand {

	/**
	 * Class representing 2D Target
	 * 
	 * @author Daniele
	 *
	 */
	public static class SbunTarget2D {
		public float x = 0.0f;
		public float y = 0.0f;
		public float angle = 0.0f;
		public String name = "NONE";
	};

	/**
	 * Commands enumerations
	 *
	 */
	public static enum CommandName {
		POSES,MOVE, START, CLOSE, BUZZ, INVALID
	};

	/**
	 * Command name
	 */
	public CommandName name;

	/**
	 * Raw Command data;
	 */
	public String rawData;

	/**
	 * Command Parsed Payload
	 */
	public Object payload;

	/**
	 * Builds Command from command String. May be invalid
	 * 
	 * @param commandString
	 */
	public SbunCommand(String commandString) {
		SbunCommand command = parseCommandString(commandString);
		this.name = command.name;
		this.rawData = command.rawData;
		this.parseData();
	}

	/**
	 * Builds Command from NAME/DATA couple
	 */
	public SbunCommand(CommandName name, String data) {
		this.name = name;
		this.rawData = data;
		this.parseData();
	}

	private void parseData() {
		if (rawData == null || rawData.equals(""))
			return;

		try {
			if (name.equals(CommandName.POSES)) {

				Hashtable<String, SbunTarget2D> targets = new Hashtable<String, SbunCommand.SbunTarget2D>();

				String[] chunks = StringUtilities.split(":", rawData);
				int size = Integer.parseInt(chunks[0]);
				String posesString = chunks[1];
				String[] values = StringUtilities.split(";", posesString);
				int prop = 4;
				for (int i = 0; i < size; i++) {
					SbunTarget2D target = new SbunTarget2D();
					target.name = values[i * prop];
					target.x = Float.parseFloat(values[prop * i + 1]);
					target.y = Float.parseFloat(values[prop * i + 2]);
					target.angle = Float.parseFloat(values[prop * i + 3]);
					targets.put(target.name, target);
				}
				this.payload = targets;
			}
			if (name.equals(CommandName.MOVE)) {

				float[] vel = new float[2];

				String[] chunks = StringUtilities.split(";", rawData);
				vel[0] = Float.parseFloat(chunks[0]);
				vel[1] = Float.parseFloat(chunks[1]);
				this.payload = vel;
			}
		} catch (Exception e) {
			System.out.println("PDAT!" + e.getClass().toString());
		}
	}

	/**
	 * Builds CommandName by String.Auto Checks consistency
	 * 
	 * @param strName
	 * @return
	 */
	public static CommandName nameByString(String strName) {
		if (strName.equals("POSES")) {
			return CommandName.POSES;
		} else if (strName.equals("CLOSE")) {
			return CommandName.CLOSE;
		} else if (strName.equals("MOVE")) {
			return CommandName.MOVE;
		} else if (strName.equals("START")) {
			return CommandName.START;
		} else if (strName.equals("BUZZ")) {
			return CommandName.BUZZ;
		} else {
			return CommandName.INVALID;
		}

	}

	/**
	 * Creates SbunCommand from string
	 * 
	 * @param commandString
	 * @return SbunCommand
	 */
	public static SbunCommand parseCommandString(String commandString) {
		try {
			String[] chunks = StringUtilities.split("!", commandString);
			CommandName name = nameByString(chunks[0]);
			String data = chunks[1];
			SbunCommand command = new SbunCommand(name, data);
			return command;
		} catch (Exception e) {
			SbunCommand command = new SbunCommand(CommandName.INVALID, "");
			return command;
		}
	}

}
