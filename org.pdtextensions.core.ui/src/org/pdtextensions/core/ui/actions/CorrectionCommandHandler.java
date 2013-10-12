/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.actions;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.text.correction.ICommandAccess;
import org.eclipse.php.internal.ui.text.correction.PHPCorrectionProcessor;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.wst.sse.ui.StructuredTextInvocationContext;
import org.pdtextensions.core.ui.PEXUIPlugin;

/**
 * This class allow to run quick assist / quick fix directly from shortcut
 * 
 * Similar to JDT/JSDT implementation
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class CorrectionCommandHandler extends AbstractHandler  {
	private final static PHPCorrectionProcessor phpCorrectionProcessor = new PHPCorrectionProcessor();
	private final static String DOCUMENT_PART = "org.eclipse.php.PHP_DEFAULT"; //$NON-NLS-1$
	

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String commandId = event.getCommand().getId();
		final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
		
		final PHPStructuredEditor editor = (PHPStructuredEditor) context.getVariable(ISources.ACTIVE_PART_NAME);
		final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		
		final StructuredTextInvocationContext assistInvocation = new StructuredTextInvocationContext(editor.getViewer(), selection.getOffset(), selection.getLength(), new HashMap());
		
		final ICompletionProposal[] proposals = phpCorrectionProcessor.computeQuickAssistProposals(assistInvocation);
		
		if (proposals == null || proposals.length == 0) {
			return null;
		}
		
		for (ICompletionProposal proposal : proposals) {
			if (proposal instanceof ICommandAccess) {
				final ICommandAccess command = (ICommandAccess) proposal;
				if (command.getCommandId() != null && command.getCommandId().equals(commandId)) {
					runProposal(editor, proposal, assistInvocation);
					return null;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Triggers are ignored
	 */
	private void runProposal(PHPStructuredEditor editor, ICompletionProposal proposal, StructuredTextInvocationContext context) {
		if (proposal instanceof ICompletionProposalExtension2) {
			((ICompletionProposalExtension2) proposal).apply(editor.getTextViewer(), (char) 0, 0, context.getOffset());
		} else if (proposal instanceof ICompletionProposalExtension && editor.getDocument() != null) {
			((ICompletionProposalExtension) proposal).apply(editor.getDocument(), (char)0, context.getOffset());
		} else {
			proposal.apply(editor.getDocument());
		}
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		
		try {
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			if (!(context.getVariable(ISources.ACTIVE_PART_NAME) instanceof PHPStructuredEditor)) {
				throw new IllegalArgumentException();
			}
			PHPStructuredEditor editor = (PHPStructuredEditor) context.getVariable(ISources.ACTIVE_PART_NAME);
			
			final ISelection selection = editor.getSelectionProvider().getSelection();
			if (!(selection instanceof ITextSelection)) {
				throw new IllegalArgumentException();
			}
			final int offset = ((ITextSelection)selection).getOffset();
			ITypedRegion partition = editor.getDocument().getPartition(offset);
			if (!partition.getType().equals(DOCUMENT_PART)) {
				setBaseEnabled(false);
			} else {
				setBaseEnabled(true);
			}
			return;
		} catch(IllegalArgumentException e) {
		} catch (BadLocationException e) {
			PEXUIPlugin.log(e);
		}
		setBaseEnabled(false);
	}
	
	/**
	 * 
	 */
	public static String getShortcut(String commandId) {
		final IBindingService keys = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		
		if (commandId != null && keys != null) {
			TriggerSequence trigger = keys.getBestActiveBindingFor(commandId);
			if (trigger != null && trigger.getTriggers().length > 0) {
				return trigger.format();
			}
		}
		
		return "";
	}

	public static StyledString appendStyledDisplay(StyledString styled, ICompletionProposal proposal) {
		if (proposal instanceof ICommandAccess) {
			final int start = styled.length();
			String keys = getShortcut(((ICommandAccess)proposal).getCommandId());
			if (keys.length() < 1) {
				return styled;
			}
			
			styled.append(" ("); //$NON-NLS-1$
			styled.append(keys);
			styled.append(")"); //$NON-NLS-1$
			
			styled.setStyle(start + 1, keys.length() + 2, StyledString.QUALIFIER_STYLER);
			
		}
		
		return styled;
	}
}
