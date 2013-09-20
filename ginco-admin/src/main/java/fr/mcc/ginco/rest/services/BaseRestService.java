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
package fr.mcc.ginco.rest.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.exceptions.BusinessException;
import fr.mcc.ginco.extjs.view.ExtJsonFormLoadData;
import fr.mcc.ginco.extjs.view.enums.ThesaurusListNodeType;
import fr.mcc.ginco.extjs.view.node.IThesaurusListNode;
import fr.mcc.ginco.extjs.view.node.ThesaurusListBasicNode;
import fr.mcc.ginco.extjs.view.pojo.UserInfo;
import fr.mcc.ginco.extjs.view.utils.ArraysGenerator;
import fr.mcc.ginco.extjs.view.utils.ChildrenGenerator;
import fr.mcc.ginco.extjs.view.utils.FolderGenerator;
import fr.mcc.ginco.extjs.view.utils.GroupsGenerator;
import fr.mcc.ginco.extjs.view.utils.OrphansGenerator;
import fr.mcc.ginco.extjs.view.utils.TopTermGenerator;
import fr.mcc.ginco.services.IAdminUserService;
import fr.mcc.ginco.services.IThesaurusService;

/**
 * Base REST service intended to be used for getting tree of {@link Thesaurus},
 * and its children.
 */
@Service
@Path("/baseservice")
@PreAuthorize("isAuthenticated()")
public class BaseRestService {
	@Inject
	@Named("thesaurusService")
	private IThesaurusService thesaurusService;

	@Inject
	@Named("folderGenerator")
	private FolderGenerator folderGenerator;

	@Inject
	@Named("orphansGenerator")
	private OrphansGenerator orphansGenerator;

	@Inject
	@Named("topTermGenerator")
	private TopTermGenerator topTermGenerator;
	
	@Inject
	@Named("arraysGenerator")
	private ArraysGenerator thesaurusArrayGenerator;
	
	@Inject
	@Named("groupsGenerator")
	private GroupsGenerator thesaurusGroupGenerator;

    @Inject
    @Named("childrenGenerator")
    private ChildrenGenerator childrenGenerator;    
    
    @Inject
    @Named("adminUserService")
    private IAdminUserService adminUserService; 
   
   

	/**
	 * Public method used to get list of all existing Thesaurus objects in
	 * database.
	 * 
	 * @return list of objects, if not found - {@code null}
	 */
	@GET
	@Path("/getTreeContent")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<IThesaurusListNode> getTreeContent(
			@QueryParam("id") String nodeParam) throws BusinessException {
		List<IThesaurusListNode> result;

		if (nodeParam.startsWith(FolderGenerator.ORPHANS_PREFIX)) {
			String vocId = getIdFromParam(nodeParam,
					FolderGenerator.ORPHANS_PREFIX);
			result = orphansGenerator.generateOrphans(vocId);
		} else if (nodeParam.startsWith(FolderGenerator.CONCEPTS_PREFIX)) {
			String vocId = getIdFromParam(nodeParam,
					FolderGenerator.CONCEPTS_PREFIX);
			result = topTermGenerator.generateTopTerm(vocId);
		} else if (nodeParam.startsWith(ChildrenGenerator.ID_PREFIX)) {
            String conceptTopTermId = getIdFromParam(nodeParam,
                    ChildrenGenerator.ID_PREFIX);
            result = childrenGenerator.getChildrenByConceptId(conceptTopTermId);
        } else if (nodeParam.startsWith(ArraysGenerator.ID_PREFIX)) {
        	String vocId = getIdFromParam(nodeParam,
        			FolderGenerator.ARRAYS_PREFIX);
            result = thesaurusArrayGenerator.generateArrays(vocId);
        } else if (nodeParam.startsWith(GroupsGenerator.ID_PREFIX)) {
        	String vocId = getIdFromParam(nodeParam,
        			FolderGenerator.GROUPS_PREFIX);
            result = thesaurusGroupGenerator.generateGroups(vocId);
        } 
		else {
			result = new ArrayList<IThesaurusListNode>();
			for (Thesaurus thesaurus : thesaurusService.getThesaurusList()) {
				IThesaurusListNode node = new ThesaurusListBasicNode();
				node.setExpanded(false);
				node.setTitle(thesaurus.getTitle());
				node.setId(thesaurus.getIdentifier());
				node.setType(ThesaurusListNodeType.THESAURUS);
				if(thesaurus.getCreator() != null) {
                    node.setOrganizationName(thesaurus.getCreator().getName());
                }
                node.setChildren(folderGenerator.generateFolders(thesaurus
						.getIdentifier()));
				node.setDisplayable(true);
				result.add(node);
			}
		}
		return result;
	}

	private String getIdFromParam(String param, String prefix) {
		return param.substring(param.indexOf(prefix) + prefix.length());
	}
	
	/**
	 * Public method used to get the name of the user currently connected
	 * 
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@GET
	@Path("/getSession")
	@Produces({ MediaType.APPLICATION_JSON })
	public long getSession(@Context HttpServletRequest request) {
		
		return request.getSession().getLastAccessedTime();
	}
	
	/**
	 * Public method used to get the name of the user currently connected
	 * 
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@GET
	@Path("/getUserInfo")
	@Produces({ MediaType.APPLICATION_JSON })
	public ExtJsonFormLoadData<UserInfo> getUserInfo() {
		UserInfo userInfos = new UserInfo();
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		userInfos.setUsername(auth.getName());		
		userInfos.setAdmin(adminUserService.isUserAdmin(auth.getName()));
		return new ExtJsonFormLoadData<UserInfo>(userInfos);
	}		
	
}
