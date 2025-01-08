/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.mockwebserver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Allows externalizing queued responses using classpath resources. This is
 * useful when dealing with larger responses from the server that may be
 * difficult to read inline within a test. An example test can be seen below:
 *
 * <pre>
 * package okhttp3.mockwebserver;
 *
 * public class EnqueueResourcesMockWebServerITest {
 * 	&#64;Rule
 * 	public EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();
 *
 * 	&#64;Test
 * 	public void multipleRequests() throws IOException {
 * 		OkHttpClient client = new OkHttpClient.Builder().build();
 * 		HttpUrl url = server.getServer().url("/");
 *
 * 		Request request = new Request.Builder().get().url(url).build();
 *
 * 		Response response = client.newCall(request).execute();
 *
 * 		assertEquals(200, response.code());
 * 		assertEquals("Hi", response.body().string());
 *
 * 		response = client.newCall(request).execute();
 *
 * 		assertEquals(500, response.code());
 * 		assertEquals("Fail", response.body().string());
 * 	}
 * }
 * </pre>
 *
 * <p>
 * For this to work the following classpath resources are present:
 * </p>
 *
 * okhttp3.mockwebserver.EnqueueResourcesMockWebServerITest_okhttp3.
 * multipleRequests/1
 *
 * <pre>
 * HTTP/1.1 200 OK
 *
 * Hi
 * </pre>
 *
 * okhttp3.mockwebserver.EnqueueResourcesMockWebServerITest_okhttp3.
 * multipleRequests/2
 *
 * <pre>
 * HTTP/1.1 500 Internal Server Error
 *
 * Fail
 * </pre>
 *
 * In addition the following special headers can be used:
 *
 * <ul>
 * <li>OkHttp-ThrottleBody - Sets the MockResponse's throttleBody. Takes the
 * format of &lt;long bytesPerPeriod&gt;;&lt;long period&gt;;&lt;TimeUnit
 * unit&gt;</li>
 * <li>OkHttp-ChunkedBody - Sets the MockResponse's chunked body value. Takes
 * the format of &lt;int maxChunkSize&gt;</li>
 * <li>OkHttp-BodyDelay - Sets the MockResponse's bodyDelay. Takes the format of
 * &lt;long delay&gt;TimeUnit unit&gt;</li>
 * </ul>
 *
 * If additional customizations are necessary, the MockResponse can be accessed
 * using {@link #peek()}.
 *
 * @author Rob Winch
 */
public final class EnqueueRequestsExtension implements BeforeEachCallback, AfterEachCallback {

	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		Class<?> testClass = extensionContext.getRequiredTestClass();
		Object testInstance = extensionContext.getRequiredTestInstance();

		findEnqueueResourcesMockWebServerMembers(testClass, testInstance)
			.forEach(server -> {
				try {
					server.getServer().start();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				Method method = extensionContext.getRequiredTestMethod();
				server.enqueue(testClass, method);
			});


	}

	@Override
	public void afterEach(ExtensionContext extensionContext) throws Exception {
		Class<?> testClass = extensionContext.getRequiredTestClass();
		Object testInstance = extensionContext.getRequiredTestInstance();

		findEnqueueResourcesMockWebServerMembers(testClass, testInstance)
				.forEach(server -> {
					try {
						server.getServer().shutdown();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	private static Stream<EnqueueResourcesMockWebServer> findEnqueueResourcesMockWebServerMembers(Class<?> testClass, Object testInstance) {
		return Arrays.stream(testClass.getDeclaredFields())
				.filter(f -> f.getType().equals(EnqueueResourcesMockWebServer.class))
				.map(f -> {
					try {
						f.setAccessible(true);
						return (EnqueueResourcesMockWebServer) f.get(testInstance);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				});
	}
}