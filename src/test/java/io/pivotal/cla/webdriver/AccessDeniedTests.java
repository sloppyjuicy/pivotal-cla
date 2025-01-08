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
package io.pivotal.cla.webdriver;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.service.github.CurrentUserRequest;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;

public class AccessDeniedTests extends BaseWebDriverTests {

	@Test
	@WithSigningUser(requestedAdmin = true)
	public void adminForbiddenForUserRequestedAdmin() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockGitHub.getCurrentUser(any(CurrentUserRequest.class))).thenReturn(user);

		String url = AdminLinkClaPage.url();

		mockMvc.perform(get(url))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithSigningUser
	public void adminRedirectForUserNotRequestedAdmin() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockGitHub.getCurrentUser(any(CurrentUserRequest.class))).thenReturn(user);

		String url = AdminLinkClaPage.url();

		mockMvc.perform(get(url))
			.andExpect(status().is3xxRedirection());
	}
}
