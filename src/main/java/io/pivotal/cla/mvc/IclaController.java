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
package io.pivotal.cla.mvc;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.ClaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.Map;

@Controller
public class IclaController {
	@Autowired
	private ContributorLicenseAgreementRepository clas;
	@Autowired
	private IndividualSignatureRepository individual;
	@Autowired
	private ClaService claService;

	@GetMapping("/sign/{claName}/icla")
	public String claForm(@AuthenticationPrincipal User user, @ModelAttribute SignClaForm signClaForm, Map<String, Object> model) {
		String claName = signClaForm.getClaName();
		IndividualSignature signed = claService.findIndividualSignaturesFor(user, claName);
		ContributorLicenseAgreement cla = signed == null ? clas.findByNameAndPrimaryTrue(claName) : signed.getCla();
		if (cla == null) {
			throw new ResourceNotFoundException();
		}
		if (cla.getSupersedingCla() != null) {
			cla = cla.getSupersedingCla();
		}
		signClaForm.setSigned(signed != null);
		signClaForm.setName(user.getName());
		signClaForm.setClaId(cla.getId());
		model.put("cla", cla);
		return "cla/icla/sign";
	}

	@GetMapping("/view/{claName}/icla")
	public String view(@PathVariable String claName, Map<String, Object> model) {
		ContributorLicenseAgreement cla = clas.findByNameAndPrimaryTrue(claName);
		if (cla == null) {
			throw new ResourceNotFoundException();
		}
		if (cla.getSupersedingCla() != null) {
			cla = cla.getSupersedingCla();
		}
		model.put("cla", cla);
		return "cla/icla/view";
	}

	@PostMapping("/sign/{claName}/icla")
	public String signCla(@AuthenticationPrincipal User user, @Valid SignClaForm signClaForm, BindingResult result, Map<String, Object> model, RedirectAttributes redirect) throws Exception {
		String claName = signClaForm.getClaName();
		Integer pullRequestId = signClaForm.getPullRequestId();
		String repositoryId = signClaForm.getRepositoryId();
		ContributorLicenseAgreement cla = clas.findOne(signClaForm.getClaId());
		if (result.hasErrors()) {
			model.put("cla", cla);
			return "cla/icla/sign";
		}
		IndividualSignature signature = new IndividualSignature();
		signature.setCla(cla);
		signature.setName(signClaForm.getName());
		signature.setCountry(signClaForm.getCountry());
		signature.setEmail(signClaForm.getEmail());
		signature.setMailingAddress(signClaForm.getMailingAddress());
		signature.setDateOfSignature(new Date());
		signature.setTelephone(signClaForm.getTelephone());
		signature.setGitHubLogin(user.getGitHubLogin());
		individual.save(signature);
		// update github
		redirect.addAttribute("claName", claName);
		if (repositoryId == null || pullRequestId == null) {
			return "redirect:/sign/{claName}/icla";
		}
		ClaPullRequestStatusRequest updatePullRequest = signClaForm.createUpdatePullRequestStatus(user.getGitHubLogin());
		if (updatePullRequest != null) {
			updatePullRequest.getCommitStatus().setSuccess(true);
			claService.savePullRequestStatus(updatePullRequest);
		}
		redirect.addAttribute("repositoryId", repositoryId);
		redirect.addAttribute("pullRequestId", pullRequestId);
		return "redirect:/sign/{claName}/icla";
	}
}
