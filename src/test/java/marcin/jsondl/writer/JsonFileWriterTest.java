package marcin.jsondl.writer;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import marcin.jsondl.entity.Post;

@PrepareForTest({ JsonFileWriter.class })
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class JsonFileWriterTest {

	private JsonFileWriter writer;

	private Post post;

	private ObjectMapper mapper;

	private File file;

	@Before
	public void setUp() throws Exception {
		writer = new JsonFileWriter();
		post = createDummyTestPost();
		mapper = PowerMockito.mock(ObjectMapper.class);

		file = PowerMockito.mock(File.class);
		PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);
	}

	@Test
	public void writeToFileShouldCreateANewFileObject() throws Exception {
		String fileName = String.format("%s%s%d.json", JsonFileWriter.LOCATION, File.separator, post.getId());

		writer.writeToFile(post, mapper);

		PowerMockito.verifyNew(File.class, Mockito.times(1)).withArguments(fileName);
	}

	@Test
	public void writeToFileShouldCallJacksonsWriteValue() throws Exception {
		writer.writeToFile(post, mapper);

		Mockito.verify(mapper, Mockito.timeout(1)).writeValue(file, post);
	}

	@Test
	public void checkFileLocationShouldReturnTrueIfTheFileExists() throws Exception {
		PowerMockito.when(file.exists()).thenReturn(true);

		boolean result = WhiteboxImpl.invokeMethod(writer, "checkFileLocation");

		Assert.assertTrue("Expected the result to be true.", result);
		Mockito.verify(file, Mockito.times(1)).exists();
	}

	@Test
	public void checkFileLocationShouldCallMkdirsIfTheFileDoesNotExists() throws Exception {
		PowerMockito.when(file.exists()).thenReturn(false);
		PowerMockito.when(file.mkdirs()).thenReturn(true);

		boolean result = WhiteboxImpl.invokeMethod(writer, "checkFileLocation");

		Assert.assertTrue("Expected the result to be true.", result);
		Mockito.verify(file, Mockito.times(1)).exists();
	}

	@Test
	public void checkFileLocationShouldReturnFalseIfTheDirectoryWasNotCreated() throws Exception {
		PowerMockito.when(file.exists()).thenReturn(false);
		PowerMockito.when(file.mkdirs()).thenReturn(false);

		boolean result = WhiteboxImpl.invokeMethod(writer, "checkFileLocation");

		Assert.assertFalse("Expected the result to be false.", result);
		Mockito.verify(file, Mockito.times(1)).exists();
	}

	private Post createDummyTestPost() {
		Post post = new Post();
		post.setUserId(123);
		post.setId(999);
		post.setTitle("Test post");
		post.setBody("Some very long text");

		return post;
	}
}
