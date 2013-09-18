import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class td_mob
{
	protected int _idMob = 0;
	protected static int nombre_mobs = 0;
	
	protected double _x = 0;
	protected double _y = 0;
	protected double _angle = 0.0;
	
	protected double _xDestination = _x;
	protected double _yDestination = _y;
	protected double _angleDestination = 0.0;
	protected double _angleBis = 0.0;
	
	protected double _vit = 6;
	protected double _vitRotation = Math.PI / 18;
	
	protected double _vies = 30;
	protected boolean _enVie = true;
	
	protected int _etape = 1;
	protected double _distanceParcourue = 0;
	protected Image _img;
	protected int _idImage;
	protected static int _chemin[][] = new int[7][2];
	
	public td_mob(String nomImage)
	{
		nombre_mobs++;
		_idMob = nombre_mobs;
		
		_chemin[0][0] = -60; _chemin[0][1] = -60;
		_chemin[1][0] = 130; _chemin[1][1] = 110;
		_chemin[2][0] = 500; _chemin[2][1] = 120;
		_chemin[3][0] = 530; _chemin[3][1] = 430;
		_chemin[4][0] = 247; _chemin[4][1] = 460;
		_chemin[5][0] = 233; _chemin[5][1] = 252;
		_chemin[6][0] = 840; _chemin[6][1] = 238;
		
		_x = _chemin[0][0];
		_y = _chemin[0][1];
		_xDestination = _chemin[1][0];
		_yDestination = _chemin[1][1];
		calculerAngleDestination();
		//_angle = _angleDestination;
		
		try
		{
			_img = ImageIO.read(new File(nomImage + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		if(hyp == 0) return;
		else _angleDestination = -1*Math.acos((_yDestination - _y) / hyp); //acos compris entre 0 et 180, ce qui pose problème ...
		if(_x < _xDestination) _angleDestination = Math.PI/2 + _angleDestination;
		else _angleDestination = Math.PI/2 - _angleDestination;
		_angleDestination += Math.PI/2;
	}
	
	public void avancer()
	{
		double deplacementX = 0;
		double deplacementY = 0;
		
		if(_xDestination == _x && _yDestination == _y)
		{
			if(_etape < _chemin.length - 1)
			{
				++_etape;
				setDestination(_chemin[_etape][0], _chemin[_etape][1]);
			}
		}
		
		tourner();
				
		deplacementX = _vit*Math.cos(_angleDestination + Math.PI/2);
		deplacementY = _vit*Math.sin(_angleDestination + Math.PI/2);
		
		double angleBis = (_angleDestination + Math.PI/2)%(2*Math.PI);
		
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
		
		if(Math.sqrt((_xDestination - _x)*(_xDestination - _x) + (_yDestination - _y)*(_yDestination - _y)) < _vit)
		{
			_x = _xDestination;
			_y = _yDestination;
			_distanceParcourue += Math.sqrt((_xDestination - _x)*(_xDestination - _x) + (_yDestination - _y)*(_yDestination - _y));
		}
	}
	
	private void tourner()
	{
		if(_angle != _angleDestination)
		{
			double difference = _angle - _angleDestination;
			if(difference < 0) difference += 2*Math.PI; //ce système permet de contourner le modulo qui veut des int et pas des doubles. Il est utilisé de nombreuses fois.
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
	
	/*
	Version simple qui n'ouvre pas d'image à chaque fois.
	public boolean autoriserSuivant()
	{
		double tailleMax = _img.getWidth(null);
		if(tailleMax < _img.getHeight(null)) tailleMax = _img.getHeight(null);
		
		if((double)_distanceParcourue > tailleMax) return true;
		else return false;
	}
	*/
	
	public boolean autoriserSuivant(String nomImage)
	{
		double tailleMax = _img.getWidth(null);
		if(tailleMax < _img.getHeight(null)) tailleMax = _img.getHeight(null);
		
		double tailleMax2 = 0;
		try
		{
			tailleMax2 = ImageIO.read(new File(nomImage + ".png")).getWidth();
			if(tailleMax2 < ImageIO.read(new File(nomImage + ".png")).getHeight()) tailleMax2 = ImageIO.read(new File(nomImage + ".png")).getHeight();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if((double)_distanceParcourue > (tailleMax + tailleMax2)/2) return true;
		else return false;
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
	
	public void setAngle()
	{
		_angle += Math.PI/4;
		//System.out.println(_img.getWidth(null));
	}
	
	public boolean getEnVie()
	{
		return _enVie;
	}
	
	public void setVies(double n)
	{
		_vies -= n;
		if(_vies < 0)
		{
			_vies = 0;
			_enVie = false;
		}
	}
	
	public int getId()
	{
		return _idMob;
	}
}
