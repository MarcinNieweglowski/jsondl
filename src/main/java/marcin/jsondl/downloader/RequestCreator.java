package marcin.jsondl.downloader;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestCreator {

	private Request buildRequest(String link) {
		Request.Builder request = new Request.Builder();

		request.url(link);
		request.method(GET, null);
		return request.build();
	}

	public String createAndExecuteRequest(String link) {
		String responseString = "";
		OkHttpClient client = new OkHttpClient();

		Request request = buildRequest(link);
		Call call = client.newCall(request);

		try (Response response = call.execute(); ResponseBody body = response.body()) {
			int statusCode = response.code();
			if (statusCode == HTTP_STATUS_OK) {
				LOGGER.debug("Status code - ok.");
				responseString = body.string();
			} else
				LOGGER.error("Error while getting the response. The response code was {}.", statusCode);
		} catch (IOException e) {
			LOGGER.error("Exception occurred while retrieving the string.");
		}
		return responseString;
	}

	private static final int HTTP_STATUS_OK = 200;

	private static final String GET = "GET";

	private static final Logger LOGGER = LogManager.getLogger(RequestCreator.class);
}
