/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.ui.preferences.formatter;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.PHPFormatterConfigurationBlock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 *
 */
@SuppressWarnings("restriction")
public class PDEFormatterConfigurationBlock extends
		PHPFormatterConfigurationBlock {

	/**
	 * @param context
	 * @param project
	 * @param container
	 */
	public PDEFormatterConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, container);

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.ui.preferences.PHPFormatterConfigurationBlock#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createContents(Composite parent) {

		Composite wrapper = new Composite(parent, SWT.NONE);
		wrapper.setLayout(new GridLayout());
		
		Label label = new Label(wrapper, SWT.NONE);		
		label.setText("Replaced by PDT Extensions formatter.");
		
		return wrapper;
	}

}
