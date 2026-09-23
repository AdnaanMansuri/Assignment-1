# CSYE 6200 — Labs

Lab work for CSYE 6200 (Concepts of Object Oriented Design).

---

## Lab 1 — Hello World

A simple Hello World program in Java.

```powershell
javac -d bin -sourcepath src src\edu\neu\csye6200\lab1\HelloWorld.java
java -cp bin edu.neu.csye6200.lab1.HelloWorld
```

Output:

```
Hello, World!
```

---

## Lab 2 — Introduction to Java Swing

A Swing application that creates a user profile (first name, last name, gender,
age, phone, email and an optional photo) and displays it back in a message
dialog. Every field is validated and the user is prompted on the error.

```powershell
javac -d bin -sourcepath src src\edu\neu\csye6200\lab2\ui\MainJFrame.java
java -cp bin edu.neu.csye6200.lab2.ui.MainJFrame
```

In VS Code, press `F5` and choose **Run Lab 2 - User Profile**.

Events used: `ActionListener`, `FocusListener`, `DocumentListener`,
`KeyListener`, `ItemListener`, `MouseListener` and `WindowListener`.

Bonus: the photo is picked with a `JFileChooser`, scaled to 60×80 and shown in
the profile dialog.
