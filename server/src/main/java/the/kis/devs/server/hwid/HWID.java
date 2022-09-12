package the.kis.devs.server.hwid;

import java.util.HashMap;


public class HWID {
	private final HashMap<String, String> properties;
	private final int availableProcessors;

	public HWID(String properties, int availableProcessors) {
		this.availableProcessors = availableProcessors;
		this.properties = new HashMap<>();

		properties = properties.replaceAll("_", " ");

		String[] split1 = properties.split("&");

		for(String rawProperty : split1) {
			if(rawProperty.contains("|")) {
				String[] split2 = rawProperty.split("\\|");
				this.properties.put(split2[0], split2.length > 1 ? split2[1] : "");
			}
		}
	}

	public String getHWID() {
		return properties.get("os.name") + properties.get("os.arch") + properties.get("os.version")
				+ availableProcessors + properties.get("PROCESSOR_IDENTIFIER")
				+ properties.get("PROCESSOR_ARCHITECTURE") + properties.get("PROCESSOR_ARCHITEW6432")
				+ properties.get("NUMBER_OF_PROCESSORS");
	}
}
