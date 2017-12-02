/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sushant oberoi
 */
import javax.swing.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/**
 * A simple example showing how to use RSyntaxTextArea to add Java syntax
 * highlighting to a Swing application.<p>
 */
public class TextEditorDemo extends JFrame {

   public TextEditorDemo() {

      JPanel cp = new JPanel();
      
      RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea(20, 60);
      syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      // No other property of RSyntaxTextArea is allowed to use
              
      // You can now modify textArea object similar to any other JTextArea object to add other functionality
      JTextArea textArea = syntaxTextArea;
      JScrollPane sp = new JScrollPane(textArea);
      cp.add(sp);
      
      setContentPane(cp);
      setTitle("Text Editor Demo");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      pack();
      setLocationRelativeTo(null);
   }

   public static void main(String[] args) {
      // Start all Swing applications on the EDT.
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new TextEditorDemo().setVisible(true);
         }
      });
   }

}

