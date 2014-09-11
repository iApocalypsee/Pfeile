package comp;

import javax.swing.JButton;

@Deprecated
public class ConfirmButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	public int type;
	
	public ConfirmButton() {
		super();
	}
	
	public ConfirmButton(String text) {
		super(text);
	}
	
}
