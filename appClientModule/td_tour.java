import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class td_tour
{
	protected int _idTour = 0;
	protected static int nombre_tours = 0;
	
	protected double _x = 400;
	protected double _y = 180;
	protected double _angle = Math.PI/2;
	
	protected double _xDestination = 0;
	protected double _yDestination = 0;
	protected double _angleDestination = Math.PI/2;
	
	protected double _vitRotation = Math.PI/72;
	protected double _porteeMin = 100;
	protected double _porteeMax = 300;
	protected double _cadence = 100;
	protected long _dernierTir = System.currentTimeMillis();
	protected boolean _ok = true;
	
	protected ArrayList<td_missile> _missiles = new ArrayList<td_missile>();
	protected Image _img;
	
	public td_tour(String nomImage)
	{
		nombre_tours++;
		_idTour = nombre_tours;		

		//calculerAngleDestination();
		
		try
		{
			_img = ImageIO.read(new File(nomImage + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void vivre(ArrayList<td_vague> liste_vagues, double sourisX, double sourisY) //td_vague est à remplacer par td_adversaire.
	{
		ArrayList<td_mob> adversaire = new ArrayList<td_mob>();
		
		for(int i = 0; i < liste_vagues.size(); i++)
		{
			for(int j = 0; j < liste_vagues.get(i).getTailleActuelleVague(); j++)
			{
				adversaire.add(liste_vagues.get(i).getMob(j));
			}
		}
		
		tourner();
		attaquer(adversaire);
		tourner();
		gerer_missiles(adversaire);
	}
	
	public void attaquer(ArrayList<td_mob> adversaire)
	{
		for(int i = 0; i < adversaire.size(); i++)
		{
			double mobX = adversaire.get(i).getX();
			double mobY = adversaire.get(i).getY();
				
			double ecartX = (mobX - _x);
			double ecartY = (mobY - _y);
			
			double distance = Math.sqrt(ecartX * ecartX + ecartY * ecartY);
			
			if(distance >= _porteeMin && distance <= _porteeMax)
			{
				//Système à changer. Il faut parcourir tous les mobs pour choisir une cible.
				setDestination(mobX, mobY);
				tirer(mobX, mobY, adversaire.get(i).getId());
				break; //Activé pour cibler la plus vieille.
			}
		}
	}
	
	public void gerer_missiles(ArrayList<td_mob> adversaire)
	{
		for(int i = 0; i < _missiles.size(); i++)
		{
			_missiles.get(i).vivre(adversaire);
		}
		
		for(int i = 0; i < _missiles.size(); i++)
		{
			if(!_missiles.get(i).getEnVol()) _missiles.remove(i);
		}
	}
	
	public void tirer(double x, double y, int idCible)
	{
		if(System.currentTimeMillis() > _dernierTir + _cadence
		&& x != _x 
		&& y != _y
		&& Math.abs(_angle - _angleDestination) < Math.PI/72)
		{
			_missiles.add(new td_missile("shoot", _x, _y, x, y, idCible, 2));
			_dernierTir = System.currentTimeMillis();
		}
	}
	
	public Graphics afficher(Graphics g, double sourisX, double sourisY)
	{
		Graphics2D g2 = (Graphics2D)g;
    	sourisX += _img.getWidth(null)/2;
    	sourisY += _img.getHeight(null)/2;

		for(int i = 0; i < _missiles.size(); i++)
		{
			AffineTransform tr2 = new AffineTransform();
			
			//tr.translate(_vagues.get(i).getWidth()/2, _vagues.get(i).getHeight()/2);
			tr2.translate(_missiles.get(i).getX(), _missiles.get(i).getY());
			tr2.rotate(_missiles.get(i).getAngle());
			tr2.translate(-1*_missiles.get(i).getWidth()/2, -1*_missiles.get(i).getHeight()/2);
			//tr.scale(0.8, 0.8);
				
			g2.drawImage(_missiles.get(i).getImg(), tr2, null);
		}
		
		AffineTransform tr = new AffineTransform();
		
		//tr.translate(_vagues.get(i).getWidth()/2, _vagues.get(i).getHeight()/2);
		tr.translate(_x, _y);
		tr.rotate(_angle);
		tr.translate(-1*_img.getWidth(null)/2, -1*_img.getHeight(null)/2);
		//tr.scale(0.8, 0.8);
		
		g2.drawImage(_img, tr, null);
		
		if(sourisX >= _x && sourisX <= _x + _img.getWidth(null)
		&& sourisY >= _y && sourisY <= _y + _img.getHeight(null))
		{
			g2.setColor(Color.red);
	        g2.drawOval((int)(_x - _porteeMax), (int)(_y - _porteeMax), (int)_porteeMax*2, (int)_porteeMax*2);
	        
	        g2.setColor(Color.orange);
	        g2.drawOval((int)(_x - _porteeMin), (int)(_y - _porteeMin), (int)_porteeMin*2, (int)_porteeMin*2);
		}

		return g;
	}
	
	public void setDestination(double xDestination, double yDestination)
	{
		_xDestination = xDestination;
		_yDestination = yDestination;
		calculerAngleDestination();
	}
	
	private void calculerAngleDestination()
	{
		double hyp = Math.sqrt((_xDestination - _x)*(_xDestination - _x) + (_yDestination - _y)*(_yDestination - _y)); //détermination de l'angle à adopter pour se rendre à un point donné. Sert juste pour l'effet graphique depuis la nouvelle version.
		if(hyp == 0.0) return;
		else _angleDestination = -1*Math.acos((_yDestination - _y) / hyp); //acos compris entre 0 et 180, ce qui pose problème ...
		if(_x < _xDestination) _angleDestination = Math.PI/2 + _angleDestination;
		else _angleDestination = Math.PI/2 - _angleDestination;
		_angleDestination += Math.PI/2;
	}
	
	private void tourner()
	{
		//Cette méthode foire si la vitesse de rotation est trop grande.
		//En effet, après une rotation, l'angle cible est dépassé et la boucle suivante, l'objet tourne dans le sens inverse.
		if(_angle != _angleDestination)
		{
			double difference = _angle - _angleDestination;
			if(difference < 0) difference += 2*Math.PI; //ce système permet de contourner le modulo.
			if(difference < _vitRotation)
			{
				_angle = _angleDestination;
				return;
			}
			if(difference > Math.PI)
			{
				//sens trigo;
				_angle += _vitRotation;
				if(_angle > 2*Math.PI) _angle -= 2*Math.PI;
				//if(_angle - _angleDestination < _vitRotation && _angle - _angleDestination > 0) _angle = _angleDestination;
			}
			else
			{
				//sens anti trigo;
				_angle -= _vitRotation;
				if(_angle < 0) _angle += 2*Math.PI;
				//if(_angleDestination - _angle < _vitRotation && _angleDestination - _angle > 0) _angle = _angleDestination;
			}
		}
	}
	
	public Image getImg()
	{
		return _img;
	}

	public double getX()
	{
		return _x;
	}
	
	public double getY()
	{
		return _y;
	}
	
	public int getWidth()
	{
		return _img.getWidth(null);
	}
	
	public int getHeight()
	{
		return _img.getHeight(null);
	}
	
	public double getAngle()
	{
		return _angle;
	}
	
	public double getPorteeMax()
	{
		return _porteeMax;
	}
	
	public double getPorteeMin()
	{
		return _porteeMin;
	}
	
	public void setAngle()
	{
		_angle += Math.PI/4;
		System.out.println(_img.getWidth(null));
	}
}
