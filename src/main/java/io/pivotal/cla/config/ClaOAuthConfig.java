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
package io.pivotal.cla.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
@ConfigurationProperties(prefix = "security.oauth2")
public class ClaOAuthConfig {
	@Value("${security.oauth2.pivotal-cla.token-secret}")
	private String pivotalClaAccessToken;
	private OAuthClientCredentials main;
	private String scheme = "https";
	private int port = 443;
	private String gitHubApiHost = "api.github.com";
	private String gitHubHost = "github.com";

	public String getGitHubBaseUrl() {
		return getBaseUrl(gitHubHost);
	}

	public String getGitHubApiBaseUrl() {
		return getBaseUrl(gitHubApiHost);
	}

	private String getBaseUrl(String host) {
		String optionalPort = ":" + port;
		if ("https".equals(scheme) && port == 443) {
			optionalPort = "";
		}
		return scheme + "://" + host + optionalPort + "/";
	}

	public ClaOAuthConfig() {
	}

	public String getPivotalClaAccessToken() {
		return this.pivotalClaAccessToken;
	}

	public OAuthClientCredentials getMain() {
		return this.main;
	}

	public String getScheme() {
		return this.scheme;
	}

	public int getPort() {
		return this.port;
	}

	public String getGitHubApiHost() {
		return this.gitHubApiHost;
	}

	public String getGitHubHost() {
		return this.gitHubHost;
	}

	public void setPivotalClaAccessToken(final String pivotalClaAccessToken) {
		this.pivotalClaAccessToken = pivotalClaAccessToken;
	}

	public void setMain(final OAuthClientCredentials main) {
		this.main = main;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void setGitHubApiHost(final String gitHubApiHost) {
		this.gitHubApiHost = gitHubApiHost;
	}

	public void setGitHubHost(final String gitHubHost) {
		this.gitHubHost = gitHubHost;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ClaOAuthConfig)) return false;
		final ClaOAuthConfig other = (ClaOAuthConfig) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$pivotalClaAccessToken = this.getPivotalClaAccessToken();
		final java.lang.Object other$pivotalClaAccessToken = other.getPivotalClaAccessToken();
		if (this$pivotalClaAccessToken == null ? other$pivotalClaAccessToken != null : !this$pivotalClaAccessToken.equals(other$pivotalClaAccessToken)) return false;
		final java.lang.Object this$main = this.getMain();
		final java.lang.Object other$main = other.getMain();
		if (this$main == null ? other$main != null : !this$main.equals(other$main)) return false;
		final java.lang.Object this$scheme = this.getScheme();
		final java.lang.Object other$scheme = other.getScheme();
		if (this$scheme == null ? other$scheme != null : !this$scheme.equals(other$scheme)) return false;
		if (this.getPort() != other.getPort()) return false;
		final java.lang.Object this$gitHubApiHost = this.getGitHubApiHost();
		final java.lang.Object other$gitHubApiHost = other.getGitHubApiHost();
		if (this$gitHubApiHost == null ? other$gitHubApiHost != null : !this$gitHubApiHost.equals(other$gitHubApiHost)) return false;
		final java.lang.Object this$gitHubHost = this.getGitHubHost();
		final java.lang.Object other$gitHubHost = other.getGitHubHost();
		if (this$gitHubHost == null ? other$gitHubHost != null : !this$gitHubHost.equals(other$gitHubHost)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ClaOAuthConfig;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $pivotalClaAccessToken = this.getPivotalClaAccessToken();
		result = result * PRIME + ($pivotalClaAccessToken == null ? 43 : $pivotalClaAccessToken.hashCode());
		final java.lang.Object $main = this.getMain();
		result = result * PRIME + ($main == null ? 43 : $main.hashCode());
		final java.lang.Object $scheme = this.getScheme();
		result = result * PRIME + ($scheme == null ? 43 : $scheme.hashCode());
		result = result * PRIME + this.getPort();
		final java.lang.Object $gitHubApiHost = this.getGitHubApiHost();
		result = result * PRIME + ($gitHubApiHost == null ? 43 : $gitHubApiHost.hashCode());
		final java.lang.Object $gitHubHost = this.getGitHubHost();
		result = result * PRIME + ($gitHubHost == null ? 43 : $gitHubHost.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ClaOAuthConfig(pivotalClaAccessToken=" + this.getPivotalClaAccessToken() + ", main=" + this.getMain() + ", scheme=" + this.getScheme() + ", port=" + this.getPort() + ", gitHubApiHost=" + this.getGitHubApiHost() + ", gitHubHost=" + this.getGitHubHost() + ")";
	}
}
