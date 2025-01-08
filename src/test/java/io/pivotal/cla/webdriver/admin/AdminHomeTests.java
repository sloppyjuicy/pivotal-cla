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
package io.pivotal.cla.webdriver.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import io.pivotal.cla.security.WithAdminUser;
import io.pivotal.cla.security.WithClaAuthorUser;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;

@WithAdminUser
public class AdminHomeTests extends BaseWebDriverTests {

	@Test
	public void homeRedirectsToLinkCla() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/cla/link"));
	}

	@Test
	public void adminUserLinkClaLinkVisible() {
		AdminLinkClaPage page = AdminLinkClaPage.to(getDriver());
		page.assertAt();

		page = page.link();
		page.assertAt();
	}

	@Test
	public void adminUserManageLinkNotVisible() {
		AdminLinkClaPage page = AdminLinkClaPage.to(getDriver());
		page.assertManageLink(false);
	}

	@Test
	@WithClaAuthorUser
	public void claAuthorUserManageLinkVisible() {
		AdminLinkClaPage page = AdminLinkClaPage.to(getDriver());
		page.assertManageLink(true);
	}
}