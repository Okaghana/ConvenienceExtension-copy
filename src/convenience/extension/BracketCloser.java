package convenience.extension;

import processing.app.ui.Editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BracketCloser implements KeyListener {
	Editor editor;

	// needed to remove double brackets (when typing too fast)
	char lastChar;

	// define which characters should get closed
	char[] openingChar = { '(', '[', '{', '"', '\'' };
	char[] closingChar = { ')', ']', '}', '"', '\'' };

	public BracketCloser(Editor _editor) {
		editor = _editor;
		editor.getTextArea().addKeyListener(this);
	}

	public void update(char key) {
		// loop through array of opening brackets to trigger completion
		for (int i = 0; i < openingChar.length; i++) {
			// if nothing is selected just add closing bracket, else wrap brackets around
			// selection
			if (!editor.isSelectionActive()) {
				if (key == openingChar[i])
					addClosingChar(i);
				else if (key == closingChar[i] && lastChar == openingChar[i])
					removeClosingChar(i);
			} else if (key == openingChar[i] && editor.isSelectionActive())
				addClosingChar(i, editor.getSelectionStart(), editor.getSelectionStop());
		}
	}

	// add closing bracket and set caret inside brackets
	private void addClosingChar(int index) {
		editor.insertText(Character.toString(closingChar[index]));

		int cursorPos = editor.getCaretOffset();
		editor.setSelection(cursorPos - 1, cursorPos - 1);
		lastChar = openingChar[index];
	}

	// if something is selected wrap closing brackets around selection
	private void addClosingChar(int positionOfChar, int startSelection, int endSelection) {
		editor.setSelection(endSelection, endSelection);
		editor.insertText(Character.toString(closingChar[positionOfChar]));
		editor.setSelection(startSelection, startSelection);
		lastChar = openingChar[positionOfChar];
	}

	// prevents something like ()) when typing too fast
	private void removeClosingChar(int positionOfChar) {
		// return if character is ' or "
		if (closingChar[positionOfChar] == '\'' || closingChar[positionOfChar] == '"')
			return;

		String sketchContent = editor.getText();
		int cursorPos = editor.getCaretOffset();

		String newContent1 = sketchContent.substring(0, cursorPos);
		String newContent2 = sketchContent.substring(cursorPos + 1, sketchContent.length());

		editor.setText(newContent1 + newContent2);
		editor.setSelection(cursorPos, cursorPos);

		lastChar = closingChar[positionOfChar];
	}

	public void keyPressed(KeyEvent key) {
		update(key.getKeyChar());
	}

	public void keyReleased(KeyEvent key) {
	}

	public void keyTyped(KeyEvent key) {
	}
}
