/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.php.internal.core.project.PHPNature;
import org.osgi.service.prefs.Preferences;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory;
import org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl;
import org.pdtextensions.semanticanalysis.validation.IValidatorFactory;
import org.pdtextensions.semanticanalysis.validation.IValidatorIdentifier;
import org.pdtextensions.semanticanalysis.validation.IValidatorManager;

@Singleton
@SuppressWarnings("restriction")
final public class Manager implements IValidatorManager {
	private static final String SLASH = "/";  //$NON-NLS-1$
	
	final private Map<String, Category> categories = new HashMap<String, Category>();
	final private Map<String, Validator> validators = new HashMap<String, Validator>();
	final private Validator[] emptyList = new Validator[0];

	@Inject
	@Preference(nodePath=PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID)
	private IEclipsePreferences preferences;
	
	@Inject
	@Preference(nodePath=SLASH + ProjectScope.SCOPE) 
	private IEclipsePreferences projectPreferences;

	@PostConstruct
	public void initialize(IExtensionRegistry registry) {
		ValidatorsPackageImpl.init();
		final IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);

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
		if (!el.getName().equals(ELEMENT_CATEGORY)) {
			return;
		}
		final Category category = ValidatorsFactory.eINSTANCE.createCategory();
		category.setId(el.getAttribute(ATTR_ID));
		category.setLabel(el.getAttribute(ATTR_LABEL));
		category.setDescription(el.getAttribute(ATTR_DESCRIPTION));
		
		categories.put(category.getId(), category);
	}

	private void registerValidator(IConfigurationElement el) {
		if (!el.getName().equals(ELEMENT_VALIDATOR)) {
			return;
		}

		final Validator validator = ValidatorsFactory.eINSTANCE.createValidator();
		String categoryId = el.getAttribute(ATTR_CATEGORY);

		validator.setId(el.getAttribute(ATTR_ID));
		if (!categories.containsKey(categoryId)) {
			if (categoryId != null)
				PEXAnalysisPlugin.error(String.format("Validator %s has reference to a non-existent category %s", validator.getId(), categoryId)); //$NON-NLS-1$

			categoryId = DEFAULT_CATEGORY_ID;
		}

		validator.setCategory(categories.get(categoryId));
		validator.getCategory().getValidators().add(validator);
		
		for (IConfigurationElement typeCfg : el.getChildren(ELEMENT_TYPE)) {
			Type type = ValidatorsFactory.eINSTANCE.createType();
			type.setId(typeCfg.getAttribute(ATTR_ID)); 
			
			type.setDefaultSeverity(ProblemSeverity.valueOf(preferences.node(validator.getId()).get(type.getId(), typeCfg.getAttribute(ATTR_DEFAULT_SEVERITY) == null ? DEFAULT_SEVERITY: typeCfg.getAttribute(ATTR_DEFAULT_SEVERITY)).toUpperCase()));
			type.setLabel(typeCfg.getAttribute(ATTR_LABEL)); 
			type.setDescription(typeCfg.getAttribute(ATTR_DESCRIPTION)); 
			
			type.setValidator(validator);
			validator.getTypes().add(type);
		}

		validators.put(validator.getId(), validator);
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
	public Type getType(IValidatorIdentifier identifier) {
		assert getValidator(identifier.validator()) != null;
		
		return getValidator(identifier.validator()).getType(identifier.type());
	}
	
	@Override
	public ProblemSeverity getSeverity(IScriptProject scriptProject, IValidatorIdentifier identifier) {
		return getSeverity(scriptProject, getType(identifier));
	}
	
	@Override
	public ProblemSeverity getSeverity(IScriptProject scriptProject, Type type) {
		assert type != null;
		if (!isEnabled(scriptProject)) {
			return ProblemSeverity.IGNORE;
		}
		
		Preferences prefs = getProjectPreferences(scriptProject).node(type.getValidator().getId());
		
		try {
			return ProblemSeverity.valueOf(prefs.get(type.getId(), preferences.node(type.getValidator().getId()).get(type.getId(), type.getDefaultSeverity().toString())));
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
			
			return type.getDefaultSeverity();
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
				if (!getSeverity(scriptProject, t).equals(ProblemSeverity.IGNORE)) {
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
	

	@Override
	public IValidatorFactory getValidatorFactory(Validator validator) throws CoreException {
		for (IConfigurationElement cfg : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID)) {
			if (cfg.getName().equals(ELEMENT_VALIDATOR) && cfg.getAttribute(ATTR_ID).equals(validator.getId())) {
				return (IValidatorFactory) cfg.createExecutableExtension(ATTR_CLASS);
			}
		}
		
		return null;
	}
}
