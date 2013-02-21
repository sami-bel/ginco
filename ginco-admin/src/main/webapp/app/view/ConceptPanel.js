/*
 * File: app/view/ThesaurusPanel.js
 * Term Creation/Edition Form
 * 
 */
Ext
		.define(
				'GincoApp.view.ConceptPanel',
				{
					extend : 'Ext.panel.Panel',

					thesaurusData : '',
					conceptData : '',

					alias : 'widget.conceptPanel',
					localized : true,
					closable : true,
					layout : {
						type : 'vbox',
						align : 'stretch'
					},

					//Labels
					xIdentifierLabel : 'Identifier',
					xCreatedDateLabel : 'Creation date',
					xModificationDateLabel : 'Modification date',
					xTopTermConceptLabel: 'Is a TopTerm Concept',
					xLexicalValueLabel : 'Lexical value',
					xLanguagesLabel : 'Languages',
					xRoleColumnLabel : 'Role',
					xPreferedColumnLabel : 'Prefered',
					xConceptPanelTitle : 'New Concept',
					xTermListGridTitle : 'Terms list', 
					xSave: 'Save',
					xDelete: 'Delete',
					xAddTerm: 'Add a term',
					xPreferedTerm: 'Prefered Term',
					xNonPreferedTerm: 'Non Prefered Term',
					xCreateTerm: 'Create Term',
					xExistingTerm: 'Select Existing Term',

					initComponent : function() {
						var me = this;
						me.conceptTermStore = Ext.create('GincoApp.store.ThesaurusTermStore');

						Ext
								.applyIf(
										me,
										{
											title : me.xConceptPanelTitle,
											items : [ {
												xtype : 'form',
												title : me.xConceptPanelTitle,
												flex : 1,
												autoScroll : true,
												pollForChanges : true,
												trackResetOnLoad : true,
												defaults : {
													anchor : '100%',
													afterLabelTextTpl : new Ext.XTemplate(
															'<tpl if="allowBlank === false"><span style="color:red;">*</span></tpl>',
															{
																disableFormats : true
															})
												},
												dockedItems : [ {
													xtype : 'toolbar',
													dock : 'top',
													items : [ {
														xtype : 'button',
														text : me.xSave,
														disabled : true,
														formBind : true,
														cls : 'save',
														iconCls : 'icon-save'
													}, {
														xtype : 'button',
														text : me.xDelete,
														disabled : true,
														itemId : 'delete',
														cls : 'delete',
														iconCls : 'icon-delete'
													} ]
												} ],
												items : [
														{
															xtype : 'displayfield',
															name : 'identifier',
															fieldLabel : me.xIdentifierLabel
														},
														{
															xtype : 'displayfield',
															name : 'created',
															fieldLabel : me.xCreatedDateLabel
														},
														{
															xtype : 'displayfield',
															name : 'modified',
															fieldLabel : me.xModificationDateLabel
														},
														{
															xtype : 'checkbox',
															name : 'topconcept',
															fieldLabel : me.xTopTermConceptLabel
														},
														{
															xtype : 'gridpanel',
															title : me.xTermListGridTitle,
															store : me.conceptTermStore,

															dockedItems : [ {
																xtype : 'toolbar',
																dock : 'top',
																items : [ {
																	xtype : 'button',
																	text : me.xAddTerm,
																	menu : {
																		xtype : 'menu',
																		items : [
																				{
																					xtype : 'menuitem',
																					text : me.xPreferedTerm,
																					menu : {
																						xtype : 'menu',
																						items : [
																								{
																									xtype : 'menuitem',
																									itemId : 'newTermFromConceptPrefBtn',
																									text : me.xCreateTerm
																								},
																								{
																									xtype : 'menuitem',
																									itemId : 'selectTermFromConceptPrefBtn',
																									text : me.xExistingTerm
																								} ]
																					}
																				},
																				{
																					xtype : 'menuitem',
																					text : me.xNonPreferedTerm,
																					menu : {
																						xtype : 'menu',
																						items : [
																								{
																									xtype : 'menuitem',
																									itemId : 'newTermFromConceptNonPrefBtn',
																									text : me.xCreateTerm
																								},
																								{
																									xtype : 'menuitem',
																									itemId : 'selectTermFromConceptNonPrefBtn',
																									text : me.xExistingTerm
																								} ]
																					}
																				} ]
																	}
																} ]
															} ],

															columns : [
																	{
																		dataIndex : 'identifier',
																		text : me.xIdentifierLabel,
																		id : 'identifier'
																	},
																	{
																		dataIndex : 'lexicalValue',
																		text : me.xLexicalValueLabel,
																		flex : 1
																	},
																	{
																		dataIndex : 'language',
																		text : me.xLanguagesLabel
																	},
																	{
																		xtype: 'booleancolumn',
																		dataIndex : 'prefered',
																		text : me.xPreferedColumnLabel
																	},
																	{
																		dataIndex : 'created',
																		text : me.xCreatedDateLabel
																	} ]
														} ]
											} ]
										});

						me.callParent(arguments);
					}
				});