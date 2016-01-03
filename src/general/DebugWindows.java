package general;

import gui.screen.Screen;

import javax.swing.*;
import java.util.Optional;

public class DebugWindows {

	private JFrame debugFrame = new JFrame("Debug window");
	private boolean windowEnabled = false;

    // <editor-fold desc="Screen control attributes">

    private JPanel screenControls = new JPanel();
    private JComboBox<String> screenList = new JComboBox<>();
    private JCheckBox boundsDraw = new JCheckBox("Draw bounds?");
    private JButton reloadScreenList = new JButton("Reload screen list");

    private void initializeScreenControlAttribs(JPanel mainPanel) {
        screenControls.setBorder(BorderFactory.createTitledBorder("Screen controls"));
        screenControls.add(screenList);
        screenControls.add(boundsDraw);
        screenControls.add(reloadScreenList);

        screenList.addItemListener(e -> {
            final Optional<Screen> screenOpt = findScreenByName((String) screenList.getSelectedItem());
            screenOpt.ifPresent(this::updateScreenControlComponents);
        });

        boundsDraw.addActionListener(e -> {
            final Optional<Screen> screenOpt = findScreenByName((String) screenList.getSelectedItem());
            screenOpt.ifPresent(screen -> screen.setBoundsDrawEnabled(boundsDraw.isSelected()));
        });

        reloadScreenList.addActionListener(e -> this.reloadScreenList());

        mainPanel.add(screenControls);
    }

    /**
     * Update the screen control components according to the given screen.
     * @param screen The screen to update the components to.
     */
    private void updateScreenControlComponents(Screen screen) {
        boundsDraw.setSelected(screen.isBoundsDrawEnabled());
    }

    // </editor-fold>

    // <editor-fold desc="Coordinate grid">

    private void initializeCoordinateGridLayout(JPanel mainPanel) {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Coordinate grid"));

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

        mainPanel.add(panel);

    }

    // </editor-fold>

	public DebugWindows() {

		JPanel panel = new JPanel();

        initializeScreenControlAttribs(panel);
        initializeCoordinateGridLayout(panel);

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

    // <editor-fold desc="Screen control component methods">

    public void reloadScreenList() {
        String[] screens = Main.getGameWindow().getScreenManager().getScreens().values().stream().map(Screen::getName).toArray(String[]::new);
        screenList.setModel(new DefaultComboBoxModel<>(screens));
    }

    private Optional<Screen> findScreenByName(String name) {
        return Main.getGameWindow().getScreenManager().getScreens().values().stream().filter(screen -> screen.getName().equals(name)).findFirst();
    }

    // </editor-fold>
}
