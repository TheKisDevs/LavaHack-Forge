package the.kis.devs.server.hwid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * 
 * @author superblaubeere27
 *
 */
public class HWID {
	private final HashMap<String, String> properties;
	private final int availableProcessors;

	public HWID(String properties, int availableProcessors) {
		this.availableProcessors = availableProcessors;
		this.properties = new HashMap<>();

		String[] split1 = properties.split("&");

		for(String rawProperty : split1) {
			if(rawProperty.contains("|")) {
				String[] split2 = rawProperty.split("\\|");
				this.properties.put(split2[0], split2.length > 1 ? split2[1] : "");
			}
		}
	}

	private final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public String getHWID() {
		return bytesToHex(generateHWID());
	}

	public byte[] generateHWID() {
		try {
			MessageDigest hash = MessageDigest.getInstance("MD5");

			String s = properties.get("os.name") + properties.get("os.arch") + properties.get("os.version")
					+ availableProcessors + properties.get("PROCESSOR_IDENTIFIER")
					+ properties.get("PROCESSOR_ARCHITECTURE") + properties.get("PROCESSOR_ARCHITEW6432")
					+ properties.get("NUMBER_OF_PROCESSORS");
			return hash.digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new Error("Algorithm wasn't found.", e);
		}

	}

	public byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
