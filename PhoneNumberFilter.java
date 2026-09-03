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

    private static final int MAX_DIGITS = 11;

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

        // Strip non-digits instead of rejecting the whole paste — keeps
        // pasting "0912-345-6789" or "0912 345 6789" from feeling broken.
        String digitsOnly = text.replaceAll("\\D", "");

        int currentLength = fb.getDocument().getLength();
        int roomLeft = MAX_DIGITS - (currentLength - length);

        if (roomLeft <= 0) return; // already at the limit

        if (digitsOnly.length() > roomLeft) {
            digitsOnly = digitsOnly.substring(0, roomLeft);
        }

        super.replace(fb, offset, length, digitsOnly, attrs);
    }
}
