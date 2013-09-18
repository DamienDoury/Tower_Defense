import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class td_missile
{
	protected int _idMissile = 0;
	protected static int nombre_missiles = 0;
	
	protected double _x = 0;
	protected double _y = 0;
	protected double _xIni = 0;
	protected double _yIni = 0;
	protected double _angle = 0.0;
	
	protected double _xDestination = 0;
	protected double _yDestination = 0;
	protected double _angleDestination = 0.0;
	
	protected double _vit = 10;
	protected double _vitRotation = Math.PI/32;
	
	protected double _porteeMin = 0;
	protected double _porteeMax = 70;
	protected double _vision = 200;
	
	protected double _force = 10;
	protected double _distanceParcourue = 0;
	
	protected boolean _explosif = false;
	protected boolean _pasDepasserCible = false;
	protected boolean _teleguide = true;
	protected boolean _systemeDAquisitionDeCibleEmbarque = true;
	
	protected boolean _enVol = true;
	protected int _idCible = -1;
	protected Image _img;
	protected static int _largeurTerrain = 800; //A passer par fonction, ou par l'objet "Terrain" qui contiendra aussi le chemin (et la vie du chateau ?).
	protected static int _hauteurTerrain = 600;
	
	public td_missile(String nomImage, double xSource, double ySource, double xDestination, double yDestination, int idCible, int mode)
	{
		/*
		 * Les missiles appartiendront a une tour.
		 * Les tours ne peuvent pas mourir, mais ceci pose probleme si l'on vend une tour.
		 * En effet, un missile en cours de vol sera d�truit, ce qui n'est pas coh�rent.
		 * N�anmoins, ce syst�me est coh�rent pour les tours tirant des lasers.
		 */
		
		nombre_missiles++;
		_idMissile = nombre_missiles;
		
		/*if(mode == 1) //Mode explosif
		{
			_pasDepasserCible = true;
			_explosif = true;
			_teleguide = false;
		}
		else if(mode == 2) //Mode t�l�guid�
		{
			_pasDepasserCible = false;
			_explosif = false;
			_teleguide = true;
		}
		else //Mode normal
		{
			_pasDepasserCible = false;
			_explosif = false;
			_teleguide = false;
		}
		*/
		
		_x = xSource;
		_y = ySource;
		_xIni = _x;
		_yIni = _y;
		_xDestination = xDestination;
		_yDestination = yDestination;
		calculerAngleDestination();
		_angle = _angleDestination;
		_idCible = idCible;
		
		try
		{
			_img = ImageIO.read(new File(nomImage + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void vivre(ArrayList<td_mob> adversaire)
	{
		aquisitionCible(adversaire);
		avancer();
		attaquer(adversaire);
	}
	
	public void setDestination(double xDestination, double yDestination)
	{
		_xDestination = xDestination;
		_yDestination = yDestination;
		calculerAngleDestination();
	}
	
	public void aquisitionCible(ArrayList<td_mob> adversaire)
	{
		if(!_teleguide) return;
		
		boolean cibleVerrouillee = false;
		
		for(int i = 0; i < adversaire.size(); i++)
		{
			if(adversaire.get(i).getId() == _idCible)
			{
				setDestination(adversaire.get(i).getX(), adversaire.get(i).getY());
				cibleVerrouillee = true;
				break;
			}
		}
		
		if(!cibleVerrouillee)
		{
			if(_systemeDAquisitionDeCibleEmbarque) choisirCible(adversaire);
			else
			{
				_xIni = _x;
				_yIni = _y;
				_teleguide = false;
			}
		}
	}
	
	public void choisirCible(ArrayList<td_mob> adversaire)
	{
		boolean cibleAquise = false;
		
		for(int i = 0; i < adversaire.size(); i++) //Il faudra le faire pour l'int�gralit� des vagues.
		{
			double mobX = adversaire.get(i).getX();
			double mobY = adversaire.get(i).getY();
				
			double ecartX = (mobX - _x);
			double ecartY = (mobY - _y);
			
			double distance = Math.sqrt(ecartX * ecartX + ecartY * ecartY);
			
			if(distance <= _vision)
			{
				_idCible = adversaire.get(i).getId();
				cibleAquise = true;
				break;
			}
		}
		
		if(!cibleAquise)
		{
			//_enVol = false;
			setDestination(_xDestination, _yDestination);
		}
	}
	
	private void calculerAngleDestination()
	{
		double hyp = Math.sqrt((_xDestination - _x)*(_xDestination - _x) + (_yDestination - _y)*(_yDestination - _y)); //d�termination de l'angle � adopter pour se rendre � un point donn�. Sert juste pour l'effet graphique depuis la nouvelle version.
		if(hyp == 0) return;
		else _angleDestination = -1*Math.acos((_yDestination - _y) / hyp); //acos compris entre 0 et 180, ce qui pose probl�me ...
		if(_x < _xDestination) _angleDestination = Math.PI/2 + _angleDestination;
		else _angleDestination = Math.PI/2 - _angleDestination;
		_angleDestination += Math.PI/2;
	}
	
	public void attaquer(ArrayList<td_mob> adversaire)
	{
		int explosion = -1;
		
		for(int i = 0; i < adversaire.size(); i++) //Il faudra le faire pour l'int�gralit� des vagues.
		{
			double mobX = adversaire.get(i).getX();
			double mobY = adversaire.get(i).getY();
				
			double ecartX = (mobX - _x);
			double ecartY = (mobY - _y);
			
			double distance = Math.sqrt(ecartX * ecartX + ecartY * ecartY);
			
			double tailleMini = adversaire.get(i).getWidth();
			if(tailleMini > adversaire.get(i).getHeight()) tailleMini = adversaire.get(i).getHeight();
			if(distance <= tailleMini)
			{
				_enVol = false;
				if(!_explosif) adversaire.get(i).setVies(_force);
				explosion = i;
				break;
			}
		}
		
		if(explosion > -1 && _explosif)
		{
			for(int i = 0; i < adversaire.size(); i++) //Tous les mobs sont affect�s par l'explosion.
			{				
				double mobX = adversaire.get(i).getX();
				double mobY = adversaire.get(i).getY();
					
				double ecartX = (mobX - _x);
				double ecartY = (mobY - _y);
				
				double distance = Math.sqrt(ecartX * ecartX + ecartY * ecartY);
				
				if(distance <= _porteeMax && distance >= _porteeMin)
				{
					adversaire.get(i).setVies(_force);
				}
			}
		}		
	}
	
	public void vivre()
	{
		avancer();
	}
	
	public void avancer()
	{
		double deplacementX = 0;
		double deplacementY = 0;

		if(_pasDepasserCible)
		{
			if((_xDestination - _x)*(_xDestination - _x) + (_yDestination - _y)*(_yDestination - _y) < _vit*_vit)
			{
				_enVol = false; //Pour que le missile ne d�passe pas sa cible.
			}
		}

		//if(_teleguide) calculerAngleDestination();

		tourner();

		deplacementX = _vit*Math.cos(_angle + Math.PI/2);
		deplacementY = _vit*Math.sin(_angle + Math.PI/2);

		double angleBis = (_angle + Math.PI/2)%(2*Math.PI);
		
		if(3*Math.PI/2 <= angleBis && angleBis < 2*Math.PI
		|| 0 <= angleBis && angleBis < Math.PI/2)
		{
			if(deplacementX > 0) deplacementX = -deplacementX;
		}
		else deplacementX = Math.abs(deplacementX);

		if(0 <= angleBis && angleBis < Math.PI)
		{
			if(deplacementY > 0) deplacementY = -deplacementY;
		}
		else deplacementY = Math.abs(deplacementY);

		_x += deplacementX;
		_y += deplacementY;
		_distanceParcourue += Math.sqrt(deplacementX*deplacementX + deplacementY*deplacementY);

		/*if(_x < 0 
			|| _y < 0
			|| _x > _largeurTerrain
			|| _y > _hauteurTerrain)
		{
			_enVol = false;
		}*/
	}
	
	private void tourner()
	{
		if(_angle != _angleDestination)
		{
			double difference = _angle - _angleDestination;
			if(difference < 0) difference += 2*Math.PI; //ce syst�me permet de contourner le modulo qui veut des int et pas des doubles. Il est utilis� de nombreuses fois.
			if(difference > Math.PI)
			{
				//sens trigo;
				_angle += _vitRotation;
				if(_angle > 2*Math.PI) _angle -= 2*Math.PI;
				if(_angle - _angleDestination < _vitRotation && _angle - _angleDestination > 0) _angle = _angleDestination;
			}
			else
			{
				//sens anti trigo;
				_angle -= _vitRotation;
				if(_angle < 0) _angle += 2*Math.PI;
				if(_angleDestination - _angle < _vitRotation && _angleDestination - _angle > 0) _angle = _angleDestination;
			}
		}
	}
	
	public Image getImg()
	{
		return _img;
	}

	public double getX()
	{
		return _x;// - _img.getWidth(null)/2;
	}
	
	public double getY()
	{
		return _y;// - _img.getHeight(null)/2;
	}
	
	public double getXDestination()
	{
		return _xDestination;
	}
	
	public double getYDestination()
	{
		return _yDestination;
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
	
	public boolean getEnVol()
	{
		return _enVol;
	}
	
	public void setAngle()
	{
		_angle += Math.PI/4;
		//System.out.println(_img.getWidth(null));
	}
}
