/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.extjs.view.utils;

import fr.mcc.ginco.beans.AssociativeRelationship;
import fr.mcc.ginco.beans.AssociativeRelationshipRole;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.extjs.view.pojo.AssociativeRelationshipView;
import fr.mcc.ginco.services.IAssociativeRelationshipRoleService;
import fr.mcc.ginco.services.IAssociativeRelationshipService;
import fr.mcc.ginco.services.IThesaurusConceptService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 */
@Component("associativeRelationshipViewConverter")
public class AssociativeRelationshipViewConverter {

	@Inject
	@Named("thesaurusConceptService")
	private IThesaurusConceptService thesaurusConceptService;

	@Inject
	@Named("associativeRelationshipService")
	private IAssociativeRelationshipService associativeRelationshipService;

	@Inject
	@Named("associativeRelationshipRoleService")
	private IAssociativeRelationshipRoleService associativeRelationshipRoleService;


	public AssociativeRelationshipView convert(AssociativeRelationship associativeRelationship,
	                                           ThesaurusConcept concept) {
		AssociativeRelationshipView view = new AssociativeRelationshipView();

		String leftId = associativeRelationship.getConceptRight().getIdentifier();
		if (leftId.equals(concept.getIdentifier())) {
			leftId = associativeRelationship.getConceptLeft().getIdentifier();
		}
		view.setIdentifier(leftId);
		view.setLabel(thesaurusConceptService.getConceptLabel(leftId));
		if (associativeRelationship.getRelationshipRole() == null) {
			view.setRoleCode(associativeRelationshipRoleService.getDefaultAssociativeRelationshipRoleRole().getCode());
		} else {
			view.setRoleCode(associativeRelationship.getRelationshipRole().getCode());
		}
		return view;
	}

	public AssociativeRelationship convert(AssociativeRelationshipView associativeRelationshipView,
	                                       ThesaurusConcept concept) {

		AssociativeRelationship associativeRelationship =
				associativeRelationshipService.getAssociativeRelationshipById(concept.getIdentifier(),
						associativeRelationshipView.getIdentifier());

		if (associativeRelationship == null) {
			associativeRelationship = new AssociativeRelationship();

			AssociativeRelationship.Id id = new AssociativeRelationship.Id();
			id.setConcept1(concept.getIdentifier());
			id.setConcept2(associativeRelationshipView.getIdentifier());

			associativeRelationship.setConceptLeft(concept);
			associativeRelationship.setConceptRight(
					thesaurusConceptService.getThesaurusConceptById(associativeRelationshipView.getIdentifier()));
			associativeRelationship.setIdentifier(id);
		}

		AssociativeRelationshipRole role = null;
		if (StringUtils.isNotEmpty(associativeRelationshipView.getRoleCode())) {
			role = associativeRelationshipRoleService.getRoleById(associativeRelationshipView.getRoleCode());
		} else {
			role = associativeRelationshipRoleService.getDefaultAssociativeRelationshipRoleRole();
		}
		associativeRelationship.setRelationshipRole(role);
		return associativeRelationship;
	}
}