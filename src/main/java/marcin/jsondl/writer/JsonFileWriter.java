package marcin.jsondl.writer;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import marcin.jsondl.entity.Post;

public class JsonFileWriter {

	public void writeToFile(Post post, ObjectMapper mapper) {
		try {
			File file = new File(String.format("%s%s%d.json", LOCATION, File.separator, post.getId()));
			mapper.writeValue(file, post);
		} catch (IOException exc) {
			LOGGER.error("Error creating the json file for: {}", post.getId());
		}
	}

	public boolean checkFileLocation() {
		LOGGER.debug("Checking target folder location.");
		File file = new File(LOCATION);
		if (!file.exists()) {
			boolean isCreated = file.mkdirs();
			LOGGER.debug("Attempted to create the folder... Result: {}.", isCreated);
			return isCreated;
		}
		LOGGER.debug("Target folder exists.");
		return true;
	}

	public static final String LOCATION = System.getProperty("user.dir") + File.separator + "downloads";

	private static final Logger LOGGER = LogManager.getLogger(JsonFileWriter.class);
}
