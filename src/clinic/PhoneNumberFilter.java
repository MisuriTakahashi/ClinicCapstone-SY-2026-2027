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
    
    @Override
    public void insertString(FilterBypass fb, int offset,
            String string, AttributeSet attr)
            throws BadLocationException {

        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(FilterBypass fb,
            int offset,
            int length,
            String text,
            AttributeSet attrs)
            throws BadLocationException {

        String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = oldText.substring(0, offset)
                + text
                + oldText.substring(offset + length);

        // Allow only digits and max length of 11
        if (newText.matches("\\d*") && newText.length() <= 11) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
