package clinic;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PC
 */
public class PhoneNumberFilter extends DocumentFilter {

    private static final int MAX_LENGTH = 11; // 09 followed by 9 digits

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length); // always allow deleting/backspacing
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text == null) text = "";

        String current = fb.getDocument().getText(0, fb.getDocument().getLength());
        String candidate = current.substring(0, offset) + text + current.substring(offset + length);
        // Permit partial editing, but only numeric characters in an 11-digit field.
        if (candidate.length() <= MAX_LENGTH && candidate.matches("^\\d*$")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
