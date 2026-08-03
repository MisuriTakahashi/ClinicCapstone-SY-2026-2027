/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author PC
 */
public class NameInputFilter extends DocumentFilter {
       
       @Override
       public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
           if (isValidNameText(string)) {
               super.insertString(fb, offset, string, attr);
           }
       }

       @Override
       public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
           if (isValidNameText(text)) {
               super.replace(fb, offset, length, text, attrs);
           }
       }

       private boolean isValidNameText(String text) {
           return text.matches("[\\p{L} .'-]*");
       }
}
