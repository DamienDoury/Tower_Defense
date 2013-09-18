import javax.swing.JFrame;

public class td_fenetre extends JFrame
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private td_panneau pann = new td_panneau();
     
     public td_fenetre()
     { 
             this.setTitle("Tower Defense");
             this.setSize(800, 600);
             this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             this.setLocationRelativeTo(null);
             this.setContentPane(pann);
             this.setVisible(true);
     }
     
     public void go()
     {
    	 pann.go();
     }
}
