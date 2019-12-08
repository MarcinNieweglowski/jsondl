package marcin.jsondl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import marcin.jsondl.downloader.RequestCreator;
import marcin.jsondl.entity.Post;
import marcin.jsondl.writer.JsonFileWriter;

@PrepareForTest({ MainRunner.class })
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class MainRunnerTest {

	private RequestCreator creator;

	private ObjectMapper mapper;

	private JsonFileWriter writer;

	private String[] args = null;

	@Before
	public void setUp() throws Exception {
		creator = PowerMockito.mock(RequestCreator.class);
		mapper = PowerMockito.mock(ObjectMapper.class);
		writer = PowerMockito.mock(JsonFileWriter.class);

		PowerMockito.whenNew(RequestCreator.class).withNoArguments().thenReturn(creator);
		PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapper);
		PowerMockito.whenNew(JsonFileWriter.class).withNoArguments().thenReturn(writer);
	}

	@Test
	public void mainShouldNotNotFailIfReturnedStringIsNull() throws Exception {
		PowerMockito.when(creator.createAndExecuteRequest(ArgumentMatchers.anyString())).thenReturn(null);

		MainRunner.main(args);

		Mockito.verifyZeroInteractions(mapper);
		Mockito.verifyZeroInteractions(writer);
	}

	@Test
	public void mainShouldNotDoAnythingIfReturnedStringIsEmpty() throws Exception {
		PowerMockito.when(creator.createAndExecuteRequest(ArgumentMatchers.anyString())).thenReturn("");

		MainRunner.main(args);

		Mockito.verifyZeroInteractions(mapper);
		Mockito.verifyZeroInteractions(writer);
	}

	@Test
	public void mainShouldNotDoAnythingIfTargetFolderWasNotCreated() throws Exception {
		PowerMockito.when(creator.createAndExecuteRequest(ArgumentMatchers.anyString())).thenReturn("Some value");
		PowerMockito.when(writer.checkFileLocation()).thenReturn(false);

		MainRunner.main(args);

		Mockito.verifyZeroInteractions(mapper);
		Mockito.verify(writer, Mockito.times(1)).checkFileLocation();
		Mockito.verifyNoMoreInteractions(writer);
	}

	@Test
	public void mainShouldParseTheReturnedStringIntoAnArrayIfNeeded() throws Exception {
		String returnedString = "Some value";
		String changedToArray = "[" + returnedString + "]";
		Post[] posts = createDummyTestPost();

		mainMethodPowerMockSetup(returnedString, posts);

		MainRunner.main(args);

		Mockito.verify(mapper, Mockito.times(1)).readValue(changedToArray, Post[].class);
	}

	@Test
	public void mainShouldNotDoubleParseTheReturnedStringIntoAnArray() throws Exception {
		String returnedString = "[Some value]";
		Post[] posts = createDummyTestPost();

		mainMethodPowerMockSetup(returnedString, posts);

		MainRunner.main(args);

		Mockito.verify(mapper, Mockito.times(1)).readValue(returnedString, Post[].class);
	}

	@Test
	public void mainShouldCallWritersWriteToFileForEachPostObject() throws Exception {
		String returnedString = "[Some value]";
		Post[] posts = createDummyTestPost();

		mainMethodPowerMockSetup(returnedString, posts);

		MainRunner.main(args);

		Mockito.verify(writer, Mockito.times(2)).writeToFile(ArgumentMatchers.any(Post.class),
				ArgumentMatchers.any(ObjectMapper.class));

	}

	@Test
	@SuppressWarnings("deprecation")
	public void mainShouldNotCreateFilesIfJsonParsingErrorOccurred() throws Exception {
		String returnedString = "[Some value]";

		PowerMockito.when(creator.createAndExecuteRequest(ArgumentMatchers.anyString())).thenReturn(returnedString);
		PowerMockito.when(writer.checkFileLocation()).thenReturn(true);
		PowerMockito.doThrow(new JsonMappingException("TEST")).when(mapper).readValue(returnedString, Post[].class);

		MainRunner.main(args);

		Mockito.verify(writer, Mockito.times(1)).checkFileLocation();
		Mockito.verifyNoMoreInteractions(writer);
	}

	private void mainMethodPowerMockSetup(String returnedString, Post[] posts)
			throws JsonProcessingException, JsonMappingException {
		PowerMockito.when(creator.createAndExecuteRequest(ArgumentMatchers.anyString())).thenReturn(returnedString);
		PowerMockito.when(writer.checkFileLocation()).thenReturn(true);
		PowerMockito.when(mapper.readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(Post[].class)))
				.thenReturn(posts);
	}

	private Post[] createDummyTestPost() {
		Post post = new Post();
		post.setUserId(123);
		post.setId(999);
		post.setTitle("Test post");
		post.setBody("Some very long text");

		Post postTwo = new Post();
		postTwo.setUserId(555);
		postTwo.setId(678);
		postTwo.setTitle("This is number two");
		postTwo.setBody("Not so long text");

		Post[] posts = new Post[2];
		posts[0] = post;
		posts[1] = postTwo;

		return posts;
	}
}