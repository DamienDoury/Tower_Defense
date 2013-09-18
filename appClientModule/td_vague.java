import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class td_vague
{
	protected static int _nombreImages = 0;
	protected static ArrayList<Image> _listeImages = new ArrayList<Image>();
	protected String _listeMobs[] = new String[10];
	protected ArrayList<td_mob> _vagues = new ArrayList<td_mob>();
	protected int _nombreMobsLances = 1;
	
	public td_vague()
	{
		_vagues.add(new td_mob("bestiole"));
		
		for(int i = 0; i < _listeMobs.length; i++)
		{
			if(i%2 == 1) _listeMobs[i] = "david";
			else _listeMobs[i] = "bestiole";
		}
	}
	
	public void vivre()
	{
		if(_nombreMobsLances < _listeMobs.length && _vagues.get(_vagues.size() - 1).autoriserSuivant(_listeMobs[_vagues.size()]))
		{
			_vagues.add(new td_mob(_listeMobs[_vagues.size()]));
			_nombreMobsLances++;
		}
		
		gerer_mobs();
	}
	
	public void gerer_mobs()
	{		
		for(int i = 0; i < _vagues.size(); i++)
		{
			_vagues.get(i).avancer();
			if(!_vagues.get(i).getEnVie()) _vagues.remove(i);
		}
	}
	
	public Graphics afficherVague(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		for(int i = _vagues.size() - 1; i >= 0; i--)
		{
			AffineTransform tr = new AffineTransform();
			
			//tr.translate(_vagues.get(i).getWidth()/2, _vagues.get(i).getHeight()/2);
			tr.translate((double)(_vagues.get(i).getX()), (double)(_vagues.get(i).getY()));
			tr.rotate(_vagues.get(i).getAngle());
			tr.translate(-1*_vagues.get(i).getWidth()/2, -1*_vagues.get(i).getHeight()/2);
			//tr.scale(0.8, 0.8);
			
			g2.drawImage(_vagues.get(i).getImg(), tr, null);
		}
		
		return g;
	}
	
	public void ga(double x, double y)
	{
		_vagues.get(0).setDestination(x, y);
	}
	
	public int getTailleActuelleVague()
	{
		return _vagues.size();
	}
	
	public td_mob getMob(int i)
	{
		return _vagues.get(i);
	}
}
