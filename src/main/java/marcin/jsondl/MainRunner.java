package marcin.jsondl;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import marcin.jsondl.downloader.RequestCreator;
import marcin.jsondl.entity.Post;
import marcin.jsondl.writer.JsonFileWriter;

public class MainRunner {

	public static void main(String[] args) {
		RequestCreator creator = new RequestCreator();
		ObjectMapper mapper = new ObjectMapper();
		JsonFileWriter writer = new JsonFileWriter();
		Post[] posts = null;

		LOGGER.debug("Creating the request to {}.", LINK);
		String returnedString = creator.createAndExecuteRequest(LINK);

		if (returnedString != null && !returnedString.isEmpty() && writer.checkFileLocation()) {
			if (!returnedString.startsWith("[")) {
				returnedString = "[" + returnedString + "]";
			}

			try {
				posts = mapper.readValue(returnedString, Post[].class);
			} catch (IOException e) {
				LOGGER.error("Error occurred when parsing string to objects.");
				return;
			}

			Arrays.asList(posts).stream().forEach(post -> writer.writeToFile(post, mapper));
			LOGGER.debug("Successfully saved {} files in {} directory.", posts.length, JsonFileWriter.LOCATION);
		} else
			LOGGER.error("String empty or could not create target folder");
	}

	private static final String LINK = "https://jsonplaceholder.typicode.com/posts/";

	private static final Logger LOGGER = LogManager.getLogger(MainRunner.class);
}
