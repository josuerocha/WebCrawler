package query_eval.util;

import java.io.Serializable;
import java.util.HashMap;

public class Tupla<X,Y> implements Comparable<Tupla<X,Y>>,Serializable
{
  


	private X x;
	private Y y;
	private boolean orderByX = true;
	public Tupla(X x,Y y)
	{
		this.x = x;
		this.y = y;
	}
	public Tupla(X x,Y y,boolean orderByX)
	{
		this(x,y);
		this.orderByX = orderByX;
	}
	
	public X getX()
	{
		return x;
	}
	public void setX(X val)
	{
		x = val;
	}
	public Y getY()
	{
		return y;
	}
	public void setY(Y val)
	{
		y = val;
	}
	public boolean isXAndYComparable()
	{
		return x instanceof Comparable && y instanceof Comparable;
	}
	@Override
	public int compareTo(Tupla<X, Y> o) {
		// TODO Auto-generated method stub
		if(this.isXAndYComparable() && o.isXAndYComparable())
		{
			if(orderByX)
			{
				int compX =((Comparable)x).compareTo(o.getX()); 
				if(compX == 0)
				{
					return ((Comparable)y).compareTo(o.getY());
				}
				return compX;
			}else
			{
				int compY = ((Comparable)y).compareTo(o.getY());
				if(compY == 0)	
				{
					return ((Comparable)x).compareTo(o.getX());	
				}
				return compY;
			}
		}
		if(this.equals(o))
		{
			return 0;
		}else
		{
			return -1;
		}
		
	}
	
	public String toString()
	{
		return "{x="+x.toString()+",y="+y.toString()+"}";
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Tupla<X,Y> t = (Tupla<X,Y>) obj;

		return ((this.x == null && t.getX() == null) || this.x.equals(t.getX())) && ((this.y == null && this.getY() == null) || this.y.equals(t.getY()));
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return new StringBuilder().append(this.x).append('_').append(this.y).toString().hashCode();
	}
	
	public static void main(String[] args)
	{
		HashMap<Tupla<Integer,Integer>,String> map = new HashMap<Tupla<Integer,Integer>, String>();
		map.put(new Tupla<Integer, Integer>(2, 3), "asdasd");
		
		boolean cont =  map.containsKey(new Tupla<Integer, Integer>(3, 2));
		System.out.println(cont);
	}
}
