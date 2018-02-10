Description:

A type of messaging software(or application) which provides users to interact and share important resources with each other and to form groups for a particular purpose.(inspired from slack messaging app).
<br>
Basic features 
● Direct messaging:(Single chat) 
    a. Register/Login for each user. b. Profile page for each user (profile status, username , profile photo, company, contacts etc.) c. Upload and download a file/image from local system. d. Mark priority of a message. e. Search for users to start a conversation or view profile.
   <br>
● Channel messaging:(Group chat) a. Support for creating a new group. b. Support for sending invites to other users to join a particular group. c. Searching groups on the app. d. Sending request to join the group. Creator will accept the request. e. Support for viewing member list in a particular group. f. Support all use cases of Direct messaging.

Advanced features 
1. Show notifications for count of unread messages/group invites. 2. Showing online users.(or showing current status for users(offline/online) 3. Spell-Check.

<br>
Support for emoticons.
Support for sharing a long post when the message-content is large.(Great for long-form content, like project plans or documentation).(Whenever user wants to post a long message a new text window will pop up and the user can write his message with basic text-formatting in that and post it).
<br>
Support for sharing code or text snippet. Snippet should consists of title, text-type(Plaintext,C,C++,JAVA,HTML,CSS,javascript) and code formatting accordingly.(can use RSyntaxTextArea library)
<br>
Messages(already read) can be seen without internet connectivity(Local Cache for storing data)
<br>
Extra Features Added: 
1. Friends Recommendation to users on the basis of their connected component in graph network of users.

[![Build Status](https://travis-ci.org/bobbylight/RSyntaxTextArea.svg?branch=master)](https://travis-ci.org/bobbylight/RSyntaxTextArea)
[![Coverage Status](https://coveralls.io/repos/bobbylight/RSyntaxTextArea/badge.svg)](https://coveralls.io/r/bobbylight/RSyntaxTextArea)
<br>
RSyntaxTextArea is a customizable, syntax highlighting text component for Java Swing applications.  Out of
the box, it supports syntax highlighting for 40+ programming languages, code folding, search and replace,
and has add-on libraries for code completion and spell checking.  Syntax highlighting for additional languages
[can be added](https://github.com/bobbylight/RSyntaxTextArea/wiki) via tools such as [JFlex](http://jflex.de).

RSyntaxTextArea is available under a [modified BSD license](https://github.com/bobbylight/RSyntaxTextArea/blob/master/src/main/dist/RSyntaxTextArea.License.txt).
For more information, visit [http://bobbylight.github.io/RSyntaxTextArea/](http://bobbylight.github.io/RSyntaxTextArea/).

Available in the [Maven Central repository](http://search.maven.org/#search%7Cga%7C1%7Crsyntaxtextarea%20jar) (`com.fifesoft:rsyntaxtextarea:XXX`).
SNAPSHOT builds of the in-development, unreleased version are hosted on [Sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/fifesoft/rsyntaxtextarea/).

# Building

RSyntaxTextArea uses [Gradle](http://gradle.org/) to build.  To compile, run
all unit tests, and create the jar, run:

    ./gradlew build

RSTA requires a Java 8 JDK to compile, but builds classes with Java 6 binary compatibility if possible
(and indeed, the artifacts in Maven Central are Java 6-compatible).
To that end, the boot classpath will be set to accommodate this if a variable `java6CompileBootClasspath`
is set to the location of `rt.jar` in a Java 6 JDK.  This can be added to `<maven-home>/gradle.properties`
if desired, to avoid diffs in the project's `gradle.properties`.

# Example Usage

RSyntaxTextArea is simply a subclass of JTextComponent, so it can be dropped into any Swing application with ease.

```java
import javax.swing.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class TextEditorDemo extends JFrame {

   public TextEditorDemo() {

      JPanel cp = new JPanel(new BorderLayout());

      RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      textArea.setCodeFoldingEnabled(true);
      RTextScrollPane sp = new RTextScrollPane(textArea);
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
```
# Sister Projects

RSyntaxTextArea provides syntax highlighting, code folding, and many other features out-of-the-box, but when building a code editor you often want to go further.  Below is a list of small add-on libraries that add more complex functionality:

* [AutoComplete](https://github.com/bobbylight/AutoComplete) - Adds code completion to RSyntaxTextArea (or any other JTextComponent).
* [RSTALanguageSupport](https://github.com/bobbylight/RSTALanguageSupport) - Code completion for RSTA for the following languages: Java, JavaScript, HTML, PHP, JSP, Perl, C, Unix Shell.  Built on both RSTA and AutoComplete.
* [SpellChecker](https://github.com/bobbylight/SpellChecker) - Adds squiggle-underline spell checking to RSyntaxTextArea.
* [RSTAUI](https://github.com/bobbylight/RSTAUI) - Common dialogs needed by text editing applications: Find, Replace, Go to Line, File Properties.

# Getting Help

* Add an issue on GitHub
* Peruse [the wiki](https://github.com/bobbylight/RSyntaxTextArea/wiki)
* Ask in the [project forum](http://fifesoft.com/forum/)
* Check the project's [home page](http://bobbylight.github.io/RSyntaxTextArea/)

