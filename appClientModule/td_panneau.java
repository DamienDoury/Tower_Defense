import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class td_panneau extends JPanel implements MouseListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4217820153607081830L;
	private Image _ground;
	private ArrayList<td_vague> _vague = new ArrayList<td_vague>(); //A remplacer plus tard par la classe "adversaire" qui gère les vagues.
	private td_tour _tour = new td_tour("nukem"); //A remplacer plus tard par la classe "joueur" qui gère les tours.
	
	protected double _sourisX;
	protected double _sourisY;
	protected int _bouton = 0;
	
	private int _attente = 20;
	
	public td_panneau()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		/*this.setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		//constraint.gridheight = 7;
		//constraint.gridwidth = 5;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.ipadx = 75;
		constraint.ipady = 25;
		//constraint.weightx = 1;
		//constraint.weighty = 1;
		//constraint.anchor = GridBagConstraints.SOUTHEAST;
		//constraint.fill = GridBagConstraints.VERTICAL;
		constraint.insets = new Insets(10, 10, 10, 10);
		
		final JButton boutondacnee = new JButton("0");
		boutondacnee.addActionListener(new ActionListener()
		{
			Integer nb = 0;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				nb++;
				boutondacnee.setText(nb.toString());
			}
		});
		
		this.add(boutondacnee, constraint);
		constraint.gridx = 1;
		constraint.gridy = 1;
		this.add(new JButton("Bonjour2"), constraint);
		constraint.gridx = 1;
		constraint.gridy = 2;
		this.add(new JButton("Bonjour3"), constraint);*/
		
		_vague.add(new td_vague());
		
		try
		{
			_ground = ImageIO.read(new File("ground.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g)
    {		 
		g.drawImage(_ground, 0, 0, this);
		
        g = _tour.afficher(g, _sourisX, _sourisY);
        
        for(int i = 0; i < _vague.size(); i++)
        	g = _vague.get(i).afficherVague(g);
        
        g.drawString("Attaque de Moufis et de Chifmols détectée !", 10, 20);
    }
	
	public void go()
	{
		//verifier_triche_temps();
		for(int i = 0; i < _vague.size(); i++)
			_vague.get(i).vivre();
		_tour.vivre(_vague, _sourisX, _sourisY);
		
		this.repaint();
		
		try
		{
			Thread.sleep(_attente);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		_bouton = e.getButton();
		
		if(e.getButton() == 1)
			_vague.add(new td_vague());
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(_bouton == 3)
			_vague.get(_vague.size() - 1).ga(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(e.getX() >= 0) _sourisX = e.getX();
		if(e.getY() >= 0) _sourisY = e.getY();
	}
}
