/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.php.internal.core.project.PHPNature;
import org.osgi.service.prefs.Preferences;
import org.pdtextensions.semanticanalysis.IValidatorFactory;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory;
import org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl;
import org.pdtextensions.semanticanalysis.validation.Identifier;

@Singleton
@SuppressWarnings("restriction")
final public class Manager implements IValidatorManager {
	public static String DEFAULT_CATEGORY_ID = "org.pdtextensions.semanticanalysis.defaultCategory"; //$NON-NLS-1$

	final private Map<String, Category> categories = new HashMap<String, Category>();
	final private Map<String, Validator> validators = new HashMap<String, Validator>();
	final private Map<String, Identifier> identifiers = new HashMap<String, Identifier>();
	final private Validator[] emptyList = new Validator[0];

	@Inject
	@Preference(nodePath=PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID)
	private IEclipsePreferences preferences;

	@Inject
	@Preference(nodePath="/" + ProjectScope.SCOPE) //$NON-NLS-1$
	private IEclipsePreferences projectPreferences;

	@PostConstruct
	public void initialize(IExtensionRegistry registry) {
		ValidatorsPackageImpl.init();
		final IConfigurationElement[] config = registry.getConfigurationElementsFor(Extension_ID);

		// build categories
		for(final IConfigurationElement el : config) {
			registerCategory(el);
		}

		// build validators
		for (final IConfigurationElement el : config) {
			registerValidator(el);
		}
	}

	private void registerCategory(IConfigurationElement el) {
		if (!el.getName().equals("category")) { //$NON-NLS-1$
			return;
		}
		final Category category = ValidatorsFactory.eINSTANCE.createCategory();
		category.setId(el.getAttribute("id")); //$NON-NLS-1$
		category.setLabel(el.getAttribute("label")); //$NON-NLS-1$
		category.setDescription(el.getAttribute("description")); //$NON-NLS-1$
		
		categories.put(category.getId(), category);
	}

	private void registerValidator(IConfigurationElement el) {
		if (!el.getName().equals("validator")) { //$NON-NLS-1$
			return;
		}

		final Validator validator = ValidatorsFactory.eINSTANCE.createValidator();
		String categoryId = el.getAttribute("categoryId"); //$NON-NLS-1$

		validator.setId(el.getAttribute("id")); //$NON-NLS-1$
		if (!categories.containsKey(categoryId)) {
			if (categoryId != null)
				PEXAnalysisPlugin.error(String.format("Validator %s has reference to a non-existent category %s", validator.getId(), categoryId)); //$NON-NLS-1$

			categoryId = DEFAULT_CATEGORY_ID;
		}

		validator.setCategory(categories.get(categoryId));
		validator.getCategory().getValidators().add(validator);
		
		try {
			for (IConfigurationElement typeCfg : el.getChildren("type")) { //$NON-NLS-1$
				Type type = ValidatorsFactory.eINSTANCE.createType();
				type.setName(typeCfg.getAttribute("id")); //$NON-NLS-1$
				type.setImport(Boolean.valueOf(typeCfg.getAttribute("import"))); //$NON-NLS-1$ //$NON-NLS-2$
				
				type.setNum(Integer.valueOf(typeCfg.getAttribute("num"))); //$NON-NLS-1$
				type.setDefaultSeverity(ProblemSeverity.valueOf(preferences.node(validator.getId()).get(type.getName(), typeCfg.getAttribute("defaultSeverity") == null ? "warning" : typeCfg.getAttribute("defaultSeverity")).toUpperCase())); //$NON-NLS-1$ //$NON-NLS-2$
				type.setLabel(typeCfg.getAttribute("label")); //$NON-NLS-1$
				type.setDescription(typeCfg.getAttribute("description")); //$NON-NLS-1$
				
				final Identifier ident = new Identifier(type.isImport(), type.getNum());
				
				type.setId(ident);
				identifiers.put(validator.getId() + type.getId(), ident);
				
				type.setValidator(validator);
				validator.getTypes().add(type);
			}
			IValidatorFactory exec = (IValidatorFactory) el.createExecutableExtension("class");  //$NON-NLS-1$
			ContextInjectionFactory.inject(exec, PEXAnalysisPlugin.getEclipseContext());
			validator.setValidatorFactory(exec);
			validators.put(validator.getId(), validator);
		} catch (CoreException e) {
			PEXAnalysisPlugin.error(String.format("Error during %s extension initialization.", validator.getId())); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isEnabled(IScriptProject scriptProject) {
		try {
			if (!scriptProject.getProject().hasNature(PHPNature.ID)) {
				return false;
			}

			return getProjectPreferences(scriptProject).getBoolean(PreferenceConstants.ENABLED, true);
		} catch (CoreException e) {
			PEXAnalysisPlugin.error(e);
			
			return false;
		}
	}
	
	@Override
	public ProblemSeverity getSeverity(IScriptProject scriptProject, String validator, String type) {
		return getSeverity(scriptProject, validators.get(validator), type);
	}
	
	@Override
	public ProblemSeverity getSeverity(IScriptProject scriptProject, Validator validator, String type) {
		assert validator != null;
		if (!isEnabled(scriptProject)) {
			return ProblemSeverity.IGNORE;
		}
		
		Preferences prefs = getProjectPreferences(scriptProject).node(validator.getId());
		Type t = validator.getType(type);
		
		try {
			return ProblemSeverity.valueOf(prefs.get(type, preferences.node(validator.getId()).get(type, t.getDefaultSeverity().toString())));
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
			
			return t.getDefaultSeverity();
		}
	}
	
	private Preferences getProjectPreferences(IScriptProject project) {
		Preferences node = projectPreferences.node(project.getProject().getName()).node(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		if (node.get(PreferenceConstants.ENABLED, null) != null) {
			return node;
		}
		
		return preferences;
	}

	@Override
	public Validator[] getValidators() {
		return validators.values().toArray(new Validator[validators.size()]);
	}

	@Override
	public Validator[] getValidators(IScriptProject scriptProject) {
		if (!isEnabled(scriptProject)) {
			return emptyList;
		}
		Set<Validator> ret = new HashSet<Validator>();
		for (Validator v : validators.values()) {
			boolean found = false;
			for (Type t : v.getTypes()) {
				if (!getSeverity(scriptProject, v, t.getName()).equals(ProblemSeverity.IGNORE)) {
					found = true;
					break;
				}
			}
			if (found) {
				ret.add(v);
			}
		}
		
		return ret.toArray(new Validator[ret.size()]);
	}

	@Override
	public Category[] getCategories() {
		return categories.values().toArray(new Category[categories.size()]);
	}

	@Override
	public Category getCategory(String id) {
		return categories.get(id);
	}

	@Override
	public Validator getValidator(String id) {
		return validators.get(id);
	}
}
