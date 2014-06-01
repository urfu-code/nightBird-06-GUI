import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;


public class WoodActionListener implements ActionListener {
	private JLabel label;
	private File woodFile;

	public WoodActionListener(JLabel label) {
		this.label = label;
	}
	
	public File getWoodFile() {
		return this.woodFile;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("rawtypes")
		JComboBox box = (JComboBox)e.getSource();
        String item = (String)box.getSelectedItem();
        label.setText(item);
        woodFile = new File("woods/" + item + ".txt");
	}

}
