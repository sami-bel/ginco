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

Ext.define('GincoApp.controller.ThesaurusTabPanelController', {
	extend : 'Ext.app.Controller',
	localized : true,
	models : [ 'ThesaurusModel' ],
	xSaveMsgLabel : 'Do you want to save changes?',
	xSaveMsgTitle : 'Save changes?',
	tabs : {
		conceptPanel : 'GincoApp.view.ConceptPanel',
		termPanel : 'GincoApp.view.TermPanel',
		conceptGroupPanel: 'GincoApp.view.ConceptGroupPanel',
		conceptArrayPanel: 'GincoApp.view.ConceptArrayPanel',
		sandboxPanel : 'GincoApp.view.SandBoxPanel',
		complexconceptsPanel: 'GincoApp.view.ComplexConceptsPanel'
	},
	
	onNewThesaurus : function (thePanel) {
		var newThesaurusFormPanel = Ext.create('GincoApp.view.ThesaurusPanel');
		var itemTabPanel = thePanel.down('#thesaurusItemsTabPanel');
		itemTabPanel.add(newThesaurusFormPanel);
	},
	openThesaurusForm : function (thePanel) {
		// We look for existing ThesaurusPanel
		var existingPanel = thePanel.down('thesaurusPanel');
		var itemTabPanel = thePanel.down('#thesaurusItemsTabPanel');
		if (existingPanel!=null)
		{
			itemTabPanel.setActiveTab(existingPanel);
		} else {
			var newThesaurusFormPanel = Ext.create('GincoApp.view.ThesaurusPanel');
			var itemTabPanel = thePanel.down('#thesaurusItemsTabPanel');
			itemTabPanel.add(newThesaurusFormPanel);
		}
	},
	onLoad : function(thePanel) {
		if (thePanel.thesaurusData!=null)
		{
			thePanel.setTitle(thePanel.thesaurusData.title);
		}
	},
	openConceptTab : function(tabPanel, aConceptId)
	{
		this.openGenericTab(tabPanel, aConceptId, "conceptPanel","GincoApp.view.ConceptPanel");
	},
	openTermTab : function(tabPanel, aTermId)
	{
		this.openGenericTab(tabPanel, aTermId, "termPanel","GincoApp.view.TermPanel");
	},
	openSandboxTab : function (tabPanel)
	{
		this.openGenericTab(tabPanel, null, "sandboxPanel","GincoApp.view.SandBoxPanel");
	}, 
	openGroupTab : function (tabPanel, aGroupId)
	{
		this.openGenericTab(tabPanel, aGroupId, "conceptGroupPanel","GincoApp.view.ConceptGroupPanel");
	}, 
	openArrayTab : function (tabPanel, aArrayId)
	{
		this.openGenericTab(tabPanel, aArrayId, "conceptArrayPanel","GincoApp.view.ConceptArrayPanel");
	}, 
	openComplexConceptsTab : function (tabPanel)
	{
		this.openGenericTab(tabPanel, null, "complexconceptsPanel","GincoApp.view.ComplexConceptsPanel");
	}, 
	openComplexConceptTab: function (tabPanel, aConceptId)
	{
		this.openGenericTab(tabPanel, aConceptId, "complexconceptPanel","GincoApp.view.ComplexConceptPanel");
	},
	openGenericTab : function (tabPanel, aGincoId, aXtype, aClass)
	{
		var existingTabs = tabPanel.query(aXtype);
		var tabExists = false;
		var itemTabPanel = tabPanel.down('#thesaurusItemsTabPanel');
		if (aGincoId!=null) {
			Ext.Array.each(existingTabs, function(element, index, array) {
				if (element.gincoId != null
						&& element.gincoId == aGincoId) {
					tabExists = element;
				}
			});
		} else
		{
			if (existingTabs.length>0)
				tabExists = existingTabs[0];
		}
		if (!tabExists) {
			var newPanel = Ext.create(
					aClass, {
						gincoId : aGincoId
					});
			var tab = itemTabPanel.add(newPanel);
			itemTabPanel.setActiveTab(tab);
			tab.show();
		} else {
			itemTabPanel.setActiveTab(tabExists);
		}
	},
	
	init : function(application) {
		this.application.on({
	        scope: this
	 });
		this.control({
			'thesaurusTabPanel' : {
				afterrender : this.onLoad,
				newthesaurus : this.onNewThesaurus,
				openthesaurusform : this.openThesaurusForm,
				openconcepttab : this.openConceptTab,
				opensandboxtab : this.openSandboxTab,
				opentermtab : this.openTermTab,
				opengrouptab : this.openGroupTab,
				openarraytab : this.openArrayTab,
				opencomplexconceptstab : this.openComplexConceptsTab,
				opencomplexconcepttab: this.openComplexConceptTab
			}
		});
	}
});