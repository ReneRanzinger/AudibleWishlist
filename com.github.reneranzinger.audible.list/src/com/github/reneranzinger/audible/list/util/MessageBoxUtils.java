package com.github.reneranzinger.audible.list.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Help class for the generation of message boxes
 *
 * @author logan
 *
 */
public class MessageBoxUtils
{
    /**
     * Create an error message box with an OK button
     *
     * @param a_shell
     *            Shell used to generate the dialog
     * @param a_title
     *            Title of the dialog
     * @param a_message
     *            Message of the dialog
     */
    public static void createErrorMessageBox(Shell a_shell, String a_title, String a_message)
    {
        MessageBox t_messageBox = new MessageBox(a_shell, SWT.ICON_ERROR | SWT.OK);
        t_messageBox.setText(a_title);
        t_messageBox.setMessage(a_message);
        t_messageBox.open();
    }

    /**
     * Create an message box with Yes and No button
     *
     * @param a_shell
     *            Shell used to generate the dialog
     * @param a_title
     *            Title of the dialog
     * @param a_message
     *            Message of the dialog
     * @return either SWT.YES or SWT.NO depending on the user response
     */
    public static int createConfirmationMessageBox(Shell a_shell, String a_title, String a_message)
    {
        MessageBox t_messageBox = new MessageBox(a_shell, SWT.YES | SWT.NO);
        t_messageBox.setText(a_title);
        t_messageBox.setMessage(a_message);
        int t_response = t_messageBox.open();
        return t_response;
    }
}
