package general;

import javax.swing.*;

public class DebugWindows {

	private JFrame debugFrame = new JFrame("Debug window");
	private boolean windowEnabled = false;

	public DebugWindows() {
		JPanel panel = new JPanel();

		JCheckBox coordinateGridActive = new JCheckBox("Coordinate grid");
		coordinateGridActive.addActionListener(e -> {
			boolean flag = coordinateGridActive.isSelected();
			Main.getGameWindow().getGrid().setActivated(flag);
		});

		JSpinner coordinateGridDensity = new JSpinner(new SpinnerNumberModel(100, 10, 1000, 10));
		coordinateGridDensity.addChangeListener(e -> {
			final Integer newDensity = (Integer) coordinateGridDensity.getValue();
			Main.getGameWindow().getGrid().setDensity(newDensity);
		});

		JSpinner offsetX = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		JSpinner offsetY = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		offsetX.addChangeListener(e -> Main.getGameWindow().getGrid().setOffsetX((Integer) offsetX.getValue()));
		offsetY.addChangeListener(e -> Main.getGameWindow().getGrid().setOffsetY((Integer) offsetY.getValue()));

		panel.add(coordinateGridActive);
		panel.add(coordinateGridDensity);
		panel.add(offsetX);
		panel.add(offsetY);
		debugFrame.add(panel);
		debugFrame.pack();
	}

	public boolean isWindowEnabled() {
		return windowEnabled;
	}

	public void setWindowEnabled(boolean windowEnabled) {
		this.windowEnabled = windowEnabled;
		debugFrame.setVisible(windowEnabled);
	}
}
