/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 *
 * @author PC
 */
public class AppIcon {
    
    public static Image getIcon() {
        return new ImageIcon(
            AppIcon.class.getResource("/Assets/logo2.png")
        ).getImage();
    }
}
