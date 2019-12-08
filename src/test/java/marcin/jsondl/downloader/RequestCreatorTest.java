package marcin.jsondl.downloader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequestCreatorTest {

	private RequestCreator creator;

	@Before
	public void setUp() {
		creator = new RequestCreator();
	}

	@Test
	public void createAndExecuteRequestShouldReturnAStringForCorrectLink() throws Exception {
		String link = "https://jsonplaceholder.typicode.com/posts/1";
		String result = creator.createAndExecuteRequest(link);
		Assert.assertNotNull("Expected the result not to be null", result);
	}

	@Test
	public void createAndExecuteRequestShouldReturnEmptyStringWhenStatusCodeIsNotOk() throws Exception {
		String link = "https://ThisIsAnIncorrectLink";
		String result = creator.createAndExecuteRequest(link);
		Assert.assertNotNull("Expected the result not to be null", result);
		Assert.assertEquals("Expected the result to be", result, "");
	}
}
