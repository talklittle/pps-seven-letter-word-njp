package seven.f10.g3;

import java.util.ArrayList;

public class BidLog
{
	private ArrayList<Double> diffMean;
	private int sum;
	private int top;
	private double mean;
	private double median;
	private double stdDev;
	private double devMean;
	private double radiusMedian;
	
	public ArrayList<Double> getDiffMean()
	{
		return diffMean;
	}
	public void setDiffMean(ArrayList<Double> diffMean)
	{
		this.diffMean=diffMean;
	}
	public int getSum()
	{
		return sum;
	}
	public void setSum(int sum)
	{
		this.sum=sum;
	}
	public int getTop()
	{
		return top;
	}
	public void setTop(int top)
	{
		this.top=top;
	}
	public double getMean()
	{
		return mean;
	}
	public void setMean(double mean)
	{
		this.mean=mean;
	}
	public double getMedian()
	{
		return median;
	}
	public void setMedian(double median)
	{
		this.median=median;
	}
	public double getStdDev()
	{
		return stdDev;
	}
	public void setStdDev(double stdDev)
	{
		this.stdDev=stdDev;
	}
	public double getDevMean()
	{
		return devMean;
	}
	public void setDevMean(double devMean)
	{
		this.devMean=devMean;
	}
	public double getRadiusMean()
	{
		return radiusMedian;
	}
	public void setRadiusMedian(double radiusMean)
	{
		this.radiusMedian=radiusMean;
	}
}
