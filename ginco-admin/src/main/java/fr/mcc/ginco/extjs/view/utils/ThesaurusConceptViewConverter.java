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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import fr.mcc.ginco.IThesaurusConceptService;
import fr.mcc.ginco.IThesaurusService;
import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.beans.ThesaurusTerm;
import fr.mcc.ginco.exceptions.BusinessException;
import fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView;
import fr.mcc.ginco.extjs.view.pojo.ThesaurusTermView;
import fr.mcc.ginco.log.Log;
import fr.mcc.ginco.utils.DateUtil;

@Component("thesaurusConceptViewConverter")
public class ThesaurusConceptViewConverter {
	@Log
	private Logger logger;
	
	@Inject
	@Named("thesaurusService")
	private IThesaurusService thesaurusService;
	
    @Inject
    @Named("thesaurusConceptService")
    private IThesaurusConceptService thesaurusConceptService;
    
	
	public ThesaurusConceptView convert(ThesaurusConcept concept,
			List<ThesaurusTerm> thesaurusTerms) {
		ThesaurusConceptView view = new ThesaurusConceptView();
		view.setIdentifier(concept.getIdentifier());
		view.setCreated(concept.getCreated().toString());
		view.setModified(concept.getModified().toString());
		view.setTopconcept(concept.getTopConcept());
		view.setThesaurusId(concept.getThesaurus().getIdentifier());
		List<ThesaurusTermView> terms = new ArrayList<ThesaurusTermView>();
		for (ThesaurusTerm thesaurusTerm : thesaurusTerms) {
			terms.add(new ThesaurusTermView(thesaurusTerm));
		}
		view.setTerms(terms);
		return view;
	}
	
	/**
	 * @param source source to work with
	 * @return ThesaurusConcept
	 * @throws BusinessException
	 * This method extracts a ThesaurusConcept from a ThesaurusConceptView given in argument
	 */
	public ThesaurusConcept convert(ThesaurusConceptView source) throws BusinessException {
		ThesaurusConcept thesaurusConcept;
		
		//Test if ThesaurusConcept already exists. If yes we get it, if no we create a new one
		if ("".equals(source.getIdentifier())) {
			thesaurusConcept = new ThesaurusConcept();
			thesaurusConcept.setCreated(DateUtil.nowDate());
			thesaurusConcept.setModified(DateUtil.nowDate());
			logger.info("Creating a new concept");
		} else {
			thesaurusConcept = thesaurusConceptService.getThesaurusConceptById(source.getIdentifier());
			logger.info("Getting an existing concept");
		}
		
		if ("".equals(source.getThesaurusId())){
			throw new BusinessException("ThesaurusId is mandatory to save a concept");
		} else {
			Thesaurus thesaurus = new Thesaurus();
			thesaurus = thesaurusService.getThesaurusById(source.getThesaurusId());
			thesaurusConcept.setThesaurus(thesaurus);
		}
		thesaurusConcept.setModified(DateUtil.nowDate());
		thesaurusConcept.setTopConcept(source.getTopconcept());
		return thesaurusConcept;
	}
	
	
}